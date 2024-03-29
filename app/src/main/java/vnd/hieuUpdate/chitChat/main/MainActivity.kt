package vnd.hieuUpdate.chitChat.main

import android.app.SearchManager
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.TextUtils
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.core.view.MenuItemCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.iid.FirebaseInstanceId
import vnd.hieuUpdate.chitChat.ProfileLoginActivity
import vnd.hieuUpdate.chitChat.R
import vnd.hieuUpdate.chitChat.Unread
import vnd.hieuUpdate.chitChat.User
import vnd.hieuUpdate.chitChat.accountLogin.LogIn


class MainActivity : AppCompatActivity() {

    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userList: ArrayList<User>
    private lateinit var adapter: UserAdapter

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference

    private lateinit var sharedPreferences: SharedPreferences
    lateinit var editor: SharedPreferences.Editor

    private var hasMore: Boolean = false

    var title: String = ""

    var userLogin: User = User()

    var newToken: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().reference

        userList = ArrayList()
        adapter = UserAdapter(this)

        userRecyclerView = findViewById(R.id.userRecyclerView)

        userRecyclerView.layoutManager = LinearLayoutManager(this)
        userRecyclerView.adapter = adapter

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        editor = sharedPreferences.edit()

        userList.clear()

        statusAccount(mAuth.uid)
    }

    override fun onResume() {
        super.onResume()
        hasMore = intent.getBooleanExtra("hasMore", false)
        findUserLogin()
        addListFriend()
        unReadChange()
        val hashMap: HashMap<String, String> = HashMap()
        hashMap.put("status", "online")
        FirebaseDatabase.getInstance().getReference("user").child(mAuth.uid.toString())
            .updateChildren(hashMap as Map<String, Any>)

        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                newToken = task.result.token
            } else
                Toast.makeText(this, "token from firebase fail!!!", Toast.LENGTH_LONG).show()
        }
        adapter.setButtonAddFriendClick(object : UserAdapter.ClickAddFriend {
            override fun onClick(user: User) {
                mDbRef.child("listFriend").child(userLogin.uid.toString())
                    .child(user.uid.toString()).setValue(user)
                Toast.makeText(this@MainActivity, "add into list", Toast.LENGTH_LONG).show()
            }
        })
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.actionbar_title, menu)
        menuInflater.inflate(R.menu.menu, menu)
        menuInflater.inflate(R.menu.dashboard, menu)

        val searchItem: MenuItem? = menu?.findItem(R.id.action_search)
        val searchManager = getSystemService(Context.SEARCH_SERVICE) as SearchManager
        val searchView: SearchView = searchItem?.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                userList.clear()
                if (query != null) {
                    addSearchFriend(query.toLowerCase())
                }
                return true
            }

            override fun onQueryTextChange(newText: String?): Boolean {
                if (TextUtils.isEmpty(newText)) {
                    addFriendUser()
                } else {
                    if (newText != null) {
                        addSearchCharacter(newText)
                    }
                }
                return true
            }
        })

        MenuItemCompat.setOnActionExpandListener(
            searchItem,
            object : MenuItemCompat.OnActionExpandListener {
                override fun onMenuItemActionExpand(item: MenuItem): Boolean {
                    return true
                }

                override fun onMenuItemActionCollapse(item: MenuItem): Boolean {
                    addListFriend()
                    return true
                }
            })


        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        val menuItem: MenuItem = menu!!.findItem(R.id.action_name_title)
        mDbRef.child("user").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (postSnapshot in snapshot.children) {
                    if (postSnapshot.getValue(User::class.java)?.uid == mAuth.currentUser?.uid) {
                        menuItem.title = userLogin.name
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, error.message, Toast.LENGTH_LONG).show()
            }
        })
        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.logout -> {
                if (mAuth.uid != null) {
                    val hashMap: HashMap<String, String> = HashMap()
                    hashMap.put("status", "offline")
                    FirebaseDatabase.getInstance().getReference("user").child(mAuth.uid.toString())
                        .updateChildren(hashMap as Map<String, Any>)

                    if (mAuth.uid.toString() == userLogin.uid.toString() && !userLogin.listToken.isNullOrEmpty()) {


                        for (token in userLogin.listToken!!) {
                            if (token == newToken) {
                                mDbRef.child("user").child(userLogin.uid.toString())
                                    .child("listToken").removeValue()
                            }
                        }
                    }
                }
                mAuth.signOut()
                editor.putBoolean("logging_Success", false)
                editor.commit()
                val intent = Intent(this@MainActivity, LogIn::class.java)
                startActivity(intent)
                finish()
                return true
            }
            R.id.action_name_title -> {
                val intent = Intent(this@MainActivity, ProfileLoginActivity::class.java)
                intent.putExtra("userLogin", userLogin)
                startActivity(intent)
            }
            R.id.action_search -> {
                addFriendUser()
            }
        }
        return false
    }

    private fun addListFriend() {
        mDbRef.child("listFriend").child(mAuth.uid.toString())
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val list: ArrayList<String> = ArrayList()
                    list.clear()
                    adapter.addUidLogin(mAuth.uid)
                    if (snapshot.key == mAuth.uid && snapshot.value != null && snapshot.exists()) {
                        for (postSnapshot in snapshot.children) {
                            val user = postSnapshot.getValue(User::class.java)
                            if (user?.uid != null) {
                                if (mAuth.uid != null && user.uid != mAuth.uid) {
                                    list.add(user.uid.toString())
                                }
                            }
                        }
                    }
                    if (list.isEmpty() && userLogin.uid == mAuth.uid) {
                        addFriendUser()
                    } else {
                        checkShowFriend(list)
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@MainActivity, error.message, Toast.LENGTH_LONG).show()
                }
            })
    }

    private fun statusAccount(Uid: String?) {
        val studentRef = FirebaseDatabase.getInstance().getReference("user").child(Uid!!)
        val connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected")

        connectedRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val connected = snapshot.getValue(Boolean::class.java)!!
                val hashMap1: HashMap<String, String> = HashMap()
                hashMap1.put("status", "offline")
                val hashMap2: HashMap<String, String> = HashMap()
                hashMap2.put("status", "online")
                if (connected) {
                    studentRef.onDisconnect().updateChildren(hashMap1 as Map<String, Any>)
                    studentRef.updateChildren(hashMap2 as Map<String, Any>)
                } else {
                    studentRef.updateChildren(hashMap1 as Map<String, Any>)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, error.message, Toast.LENGTH_LONG).show()
            }
        })
    }

    @Deprecated("Deprecated in Java")
    override fun onBackPressed() {
        AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert)
            .setTitle("Are you want to exit app?")
            .setMessage("Are you sure?")
            .setPositiveButton("yes", DialogInterface.OnClickListener { dialog, which ->
                val intent = Intent(Intent.ACTION_MAIN)
                intent.addCategory(Intent.CATEGORY_HOME)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                val hashMap: HashMap<String, String> = HashMap()
                hashMap.put("status", "offline")
                FirebaseDatabase.getInstance().getReference("user").child(mAuth.uid!!)
                    .updateChildren(
                        hashMap as Map<String, Any>
                    )
                startActivity(intent)
                finish()
            }).setNegativeButton("no", null).show()
    }

    fun checkShowFriend(list: ArrayList<String>) {
        mDbRef.child("user").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                adapter.addUidLogin(mAuth.uid)
                if (snapshot.key == "user" && snapshot.value != null && snapshot.exists()) {
                    for (postSnapshot in snapshot.children) {
                        val user = postSnapshot.getValue(User::class.java)
                        if (user?.uid != null) {
                            if (mAuth.uid != null && user.uid != mAuth.uid) {
                                if (list.contains(user.uid)) {
                                    userList.add(user)
                                    adapter.setGoneButtonAdd(true)
                                }
                            } else {
                                userLogin = postSnapshot.getValue(User::class.java)!!
                                adapter.addUserLogin(userLogin)
                                FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
                                    if (!userLogin.listToken?.contains(it.token)!! && userLogin.status == "online") {

                                        val listToken = ArrayList<String>()
                                        listToken.add(it.token)

                                        mDbRef.child("user").child(userLogin.uid.toString())
                                            .child("listToken").setValue(listToken)
                                    } else {
                                        Log.d("token", it.token)
                                    }
                                }
                            }
                        }
                    }
                    adapter.updateListFriend(userList)
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, error.message, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun addFriendUser() {
        mDbRef.child("user").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                adapter.addUidLogin(mAuth.uid)
                if (snapshot.key == "user" && snapshot.value != null && snapshot.exists()) {
                    for (postSnapshot in snapshot.children) {
                        val user = postSnapshot.getValue(User::class.java)
                        if (user?.uid != null) {
                            if (mAuth.uid != null && user.uid != mAuth.uid) {
                                if (user.lastMsg != null) {
                                    adapter.addLastMsg(user.lastMsg.toString())
                                }
                                adapter.setGoneButtonAdd(false)
                                userList.add(user)
                            } else {
                                userLogin = postSnapshot.getValue(User::class.java)!!
                                if (userLogin.unRead != 0 && userLogin.receiveUid != "") {
                                    adapter.addUserLogin(userLogin)
                                }
                                adapter.addUserLogin(userLogin)
                                FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {


                                    if (!userLogin.listToken?.contains(it.token)!! && userLogin.status == "online") {

                                        val listToken = ArrayList<String>()
                                        listToken.add(it.token)

                                        mDbRef.child("user").child(userLogin.uid.toString())
                                            .child("listToken").setValue(listToken)
                                    } else {
                                        Log.d("token", it.token)
                                    }
                                }
                            }
                        }
                    }
                }
                adapter.updateListFriend(userList)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, error.message, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun findUserLogin() {
        mDbRef.child("user").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                adapter.addUidLogin(mAuth.uid)
                if (snapshot.key == "user" && snapshot.value != null && snapshot.exists()) {
                    for (postSnapshot in snapshot.children) {
                        val user = postSnapshot.getValue(User::class.java)
                        if (user != null) {
                            if (mAuth.uid != null && user.uid == mAuth.uid) {
                                userLogin = user
                                if (userLogin.unRead != 0 && userLogin.receiveUid != "") {
                                    adapter.addUserLogin(userLogin)
                                }
                                adapter.addUserLogin(userLogin)
                                FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
                                    if (!userLogin.listToken?.contains(it.token)!! && userLogin.status == "online") {
                                        val listToken = ArrayList<String>()
                                        listToken.add(it.token)
                                        mDbRef.child("user").child(userLogin.uid.toString())
                                            .child("listToken").setValue(listToken)
                                    } else {
                                        Log.d("token", it.token)
                                    }
                                }
                            }
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, error.message, Toast.LENGTH_LONG).show()
            }
        })
    }

    fun addSearchFriend(query: String) {
        mDbRef.child("user").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                adapter.addUidLogin(mAuth.uid)
                for (postSnapshot in snapshot.children) {
                    if (postSnapshot.getValue(User::class.java)?.uid != null && postSnapshot.getValue(
                            User::class.java
                        )?.uid != mAuth.uid
                    ) {
                        if (query == postSnapshot.getValue(User::class.java)!!.name.toString()
                                .toLowerCase()
                        ) {
                            userList.add(postSnapshot.getValue(User::class.java)!!)
                        }
                        adapter.updateListFriend(userList)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, error.message, Toast.LENGTH_LONG).show()
            }
        })
    }

    fun addSearchCharacter(query: String) {
        mDbRef.child("user").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                userList.clear()
                adapter.addUidLogin(mAuth.uid)
                for (postSnapshot in snapshot.children) {
                    if (postSnapshot.getValue(User::class.java)?.uid != null && postSnapshot.getValue(
                            User::class.java
                        )?.uid != mAuth.uid
                    ) {
                        if (postSnapshot.getValue(User::class.java)!!.name?.toLowerCase()!!
                                .startsWith(query.toLowerCase())
                        ) {
                            userList.add(postSnapshot.getValue(User::class.java)!!)
                        }
                        adapter.updateListFriend(userList)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, error.message, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun unReadChange() {
        mDbRef.child("unRead").child(mAuth.uid.toString())
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (postSnapshot in snapshot.children) {
                        val unRead: Unread? = postSnapshot.getValue(Unread::class.java)
                        if (unRead != null && postSnapshot.key != userLogin.uid) {
                            if (unRead.fromUid != "" || unRead.toUid != "" && unRead.toUid == userLogin.uid) {
                                adapter.unRead(unRead)
                            }
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@MainActivity, error.message, Toast.LENGTH_LONG).show()
                }
            })
    }
}