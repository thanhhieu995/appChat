package com.example.chatapp.main

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
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.ProfileLoginActivity
import com.example.chatapp.R
import com.example.chatapp.User
import com.example.chatapp.accountLogin.LogIn
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.iid.FirebaseInstanceId
import com.google.firebase.iid.InstanceIdResult


class MainActivity : AppCompatActivity() {

    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userList: ArrayList<User>
    private lateinit var adapter: UserAdapter

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference

    private lateinit var sharedPreferences: SharedPreferences
    lateinit var editor: SharedPreferences.Editor

    private var hasMore: Boolean = false

    var statusUserLogin: String = ""

    var statusFriend: String = ""

    lateinit var actionTitle: TextView

    var title: String = ""

    var userLogin: User = User()

    var newToken: String? = null

//    var logging: Boolean = true
    val TIME_INTERVAL = 2000

    var deleteToken: Boolean = false

    val listTokenTem: ArrayList<String> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)


        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().reference

        userList = ArrayList()
        adapter = UserAdapter(this, userList)

        userRecyclerView = findViewById(R.id.userRecyclerView)

        userRecyclerView.layoutManager = LinearLayoutManager(this)
        userRecyclerView.adapter = adapter

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        editor = sharedPreferences.edit()

//        logging = sharedPreferences.getBoolean("logging_Success", false)

        userList.clear()

//        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener(this@MainActivity, OnSuccessListener {
//            tokenReloadtokenReload = it.token
//
////            if (!userLogin.listToken!!.contains(tokenReloadtokenReload)) {
//////                                tokenReload.let { userLogin.listToken!!.add(it) }
//////                                mDbRef.child("user").child(userLogin.uid.toString()).child("listToken").setValue(userLogin.listToken)
////                mDbRef.child("user").child(userLogin.uid.toString()).child("listToken").setValue(tokenReloadtokenReload)
////            } else {
////                Log.d("token", tokenReloadtokenReload.toString())
////            }
//        })
//        mDbRef.child("user").addValueEventListener(object : ValueEventListener{
//            override fun onDataChange(snapshot: DataSnapshot) {
//                for(postSnapshot in snapshot.children) {
//                    if (postSnapshot.getValue(User::class.java)?.uid == mAuth.uid) {
//                        statusAccount(mAuth.uid)
//                    } else {
//                        Toast.makeText(this@MainActivity, "Account is not in Firebase Database!!!", Toast.LENGTH_LONG).show()
//                    }
//                }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//                TODO("Not yet implemented")
//            }
//
//        })
        statusAccount(mAuth.uid)
//        addFriendUser()
    }

    override fun onResume() {
        super.onResume()
        hasMore = intent.getBooleanExtra("hasMore", false)
//        statusAccount(mAuth.uid)
        addFriendUser()

        val hashMap: HashMap<String, String> = HashMap()
        hashMap.put("status", "online")
        FirebaseDatabase.getInstance().getReference("user").child(mAuth.uid.toString())
            .updateChildren(hashMap as Map<String, Any>)

//        if (userLogin.status == "offline") {
//            mDbRef.child("user").child(userLogin.uid.toString()).child("status").setValue("online")
//        }


        FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                newToken = task.result.token
            } else
                Toast.makeText(this, "token from firebase fail!!!", Toast.LENGTH_LONG).show()
        }
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.actionbar_title, menu)
        menuInflater.inflate(R.menu.menu, menu)
        menuInflater.inflate(R.menu.dashboard, menu)
        //menuInflater.inflate(R.layout.actionbar_title, menu)

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
                    adapter.notifyDataSetChanged()
                } else {
                    if (newText != null) {
                        addSearchCharacter(newText)
                    }
                }
                return true
            }
        })

        searchView.setSearchableInfo(searchManager.getSearchableInfo(componentName))
        return super.onCreateOptionsMenu(menu)
    }

    override fun onPrepareOptionsMenu(menu: Menu?): Boolean {
        var menuItem: MenuItem = menu!!.findItem(R.id.action_name_title)
        mDbRef.child("user").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (postSnapshot in snapshot.children) {
                    if (postSnapshot.getValue(User::class.java)?.uid == mAuth.currentUser?.uid) {
                        menuItem.title = userLogin.name
                        //menuItem.setTitle(titleColor.red)
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

        if(item.itemId == R.id.logout) {
            //hasMore = true
            if (mAuth.uid != null) {
                val hashMap: HashMap<String, String> = HashMap()
                hashMap.put("status", "offline")
                FirebaseDatabase.getInstance().getReference("user").child(mAuth.uid.toString())
                    .updateChildren(hashMap as Map<String, Any>)

                if (mAuth.uid.toString() == userLogin.uid.toString() && userLogin.listToken != null) {
//                    for (token in userLogin.listToken!!) {
//                        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
//                            if (token == it.token) {
//                                mDbRef.child("user").child(userLogin.uid.toString()).child("listToken").removeValue()
//                            }
//                        }
//                    }

//                    FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener(this@MainActivity,
//                        OnSuccessListener<InstanceIdResult> { instanceIdResult ->
//                            val tokenFirebase = instanceIdResult.token
//                            for (token in userLogin.listToken!!) {
//                                if (token == tokenFirebase) {
//                                    mDbRef.child("user").child(userLogin.uid.toString()).child("listToken").removeValue()
//                                }
//                            }
//                        })

//                    FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener { instanceIdResult ->
//                        val newToken = instanceIdResult.token
//                       for (token in userLogin.listToken!!) {
//                           if (token == newToken) {
//                               mDbRef.child("user").child(userLogin.uid.toString()).child("listToken").removeValue()
//                           } else {
//                               Log.d("listToken not remove", newToken)
//                           }
//                       }
//                    }





//                    FirebaseInstanceId.getInstance().instanceId.addOnCompleteListener { task ->
//                        if (task.isSuccessful) {
//                            val newToken = task.result.token
//                            for (token in userLogin.listToken!!) {
//                                if (token == newToken) {
//                                    mDbRef.child("user").child(userLogin.uid.toString()).child("listToken").removeValue()
//                                }
//                            }
//                        } else
//                            Toast.makeText(this, "token from firebase fail!!!", Toast.LENGTH_LONG).show()
//                    }
                    for (token in userLogin.listToken!!) {
                        if (token == newToken) {
                            mDbRef.child("user").child(userLogin.uid.toString()).child("listToken").removeValue()
                        }
                    }

                }
//                val token = FirebaseInstanceId.getInstance().instanceId
            }
//            for (token in userLogin.listToken!!) {
//
//                FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {
//                    if (token == it.token) {
//                        mDbRef.child("user").child(userLogin.uid.toString()).child("listToken").removeValue()
//                    }
//                }
//            }
            mAuth.signOut()

            editor.putBoolean("logging_Success", false)
            editor.commit()

//            for (token in userLogin.listToken!!) {
//                if (token == tokenReloadtokenReload) {
////                    userLogin.listToken!!.remove(token)
////                    mDbRef.child("user").child(userLogin.uid.toString()).child("listToken").setValue(userLogin.listToken)
////                    deleteToken = true
//                    userLogin.listToken!!.remove(token)
//                }
//            }
//            tokenReloadtokenReload?.let { userLogin.listToken!!.remove(it) }


            val intent = Intent(this@MainActivity, LogIn::class.java)
            startActivity(intent)
            finish()
            return true
        } else if (item.itemId == R.id.action_name_title) {
            val intent = Intent(this@MainActivity, ProfileLoginActivity::class.java)
//            //intent.putExtra("user", user)
//            intent.putExtra("uid", user.uid)
//            intent.putExtra("name", user.name)
            intent.putExtra("userLogin", userLogin)
            startActivity(intent)
//            finish()
        }
        return false
    }

    fun popUpProfileLoginUser() {

    }

    private fun statusAccount(Uid: String? ) {
        val studentRef = FirebaseDatabase.getInstance().getReference("user").child(Uid!!)
        val connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected")

        connectedRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val connected = snapshot.getValue(Boolean::class.java)!!
                val hashMap1 : HashMap<String, String> = HashMap()
                hashMap1.put("status", "offline")
                val hashMap2: HashMap<String, String> = HashMap()
                hashMap2.put("status", "online")
                if (connected) {
                    studentRef.onDisconnect().updateChildren(hashMap1 as Map<String, Any>)
                    studentRef.updateChildren(hashMap2 as Map<String, Any>)
                }else {
                    studentRef.updateChildren(hashMap1 as Map<String, Any>)
                    }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, error.message, Toast.LENGTH_LONG).show()
            }
        })
    }

    override fun onBackPressed() {
//        super.onBackPressed()
//        val intent = Intent(this, LogIn::class.java)
//        FirebaseDatabase.getInstance().getReference("user").child(mAuth.uid!!).child("status").setValue("offline")
////        mAuth.signOut()
//        startActivity(intent)
//        finish()

//        val mBackPressed: Long
//        finish()

//        val intent = Intent(Intent.ACTION_MAIN)
//        intent.addCategory(Intent.CATEGORY_HOME)
//        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP //***Change Here***
//
//        startActivity(intent)
//        finish()
//        exitProcess(0)


        AlertDialog.Builder(this).setIcon(android.R.drawable.ic_dialog_alert).setTitle("Are you want to exit app?")
            .setMessage("Are you sure?")
            .setPositiveButton("yes", DialogInterface.OnClickListener { dialog, which ->
                val intent = Intent(Intent.ACTION_MAIN)
                intent.addCategory(Intent.CATEGORY_HOME)
                intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK
                val hashMap: HashMap<String, String> = HashMap()
                hashMap.put("status", "offline")
                //.ref.updateChildren(hashMap as Map<String, Any>)
                FirebaseDatabase.getInstance().getReference("user").child(mAuth.uid!!).updateChildren(
                    hashMap as Map<String, Any>)
                startActivity(intent)
                finish()
            }).setNegativeButton("no", null).show()
    }


    private fun addFriendUser() {
        mDbRef.child("user").addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                userList.clear()
                adapter.addUidLogin(mAuth.uid)

                for (postSnapshot in snapshot.children) {
                    if (postSnapshot.getValue(User::class.java)?.uid != null && mAuth.uid != null && postSnapshot.getValue(User::class.java)?.uid != mAuth.uid) {
                        userList.add(postSnapshot.getValue(User::class.java)!!)
                    } else {
                        userLogin = postSnapshot.getValue(User::class.java)!!
                        adapter.addUserLogin(userLogin)

//                        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener(this@MainActivity, OnSuccessListener {
//                            tokenReload = it.token
//
//                            if (!userLogin.listToken!!.contains(tokenReload) && userLogin.status == "online") {
//                                userLogin.listToken!!.add(tokenReload!!)
////                                tokenReload.let { userLogin.listToken!!.add(it) }
////                                mDbRef.child("user").child(userLogin.uid.toString()).child("listToken").setValue(userLogin.listToken)
//                                mDbRef.child("user").child(userLogin.uid.toString()).child("listToken").setValue(userLogin.listToken)
//                            } else {
//                                Log.d("token", tokenReload.toString())
//                            }
//                        })
                        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener {


                            if (!userLogin.listToken?.contains(it.token)!! && userLogin.status == "online") {

                                val listToken = ArrayList<String>()
                                listToken.add(it.token)

//                                userLogin.listToken?.add(it.token)
                                mDbRef.child("user").child(userLogin.uid.toString()).child("listToken").setValue(listToken)
                            } else {
                                Log.d("token", it.token)
                            }
                        }

                    }
                }
                adapter.addUserList(userList)
                if (userList.isEmpty() && hasMore && userLogin.uid == mAuth.uid) {
                    Toast.makeText(this@MainActivity, "No friend to show", Toast.LENGTH_LONG).show()
                }
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, error.message, Toast.LENGTH_LONG).show()
            }

        })
    }

//    fun String.containsAnyOfIgnoreCase(listToken: ArrayList<String>): Boolean {
//        for (tokenReloadtokenReload in listToken) {
//            if (this.contains(tokenReloadtokenReload, true)) return true
//        }
//        return false
//    }


    fun addSearchFriend(query: String) {
        mDbRef.child("user").addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                userList.clear()
                adapter.addUidLogin(mAuth.uid)

                for (postSnapshot in snapshot.children) {
                    if (postSnapshot.getValue(User::class.java)?.uid != null && postSnapshot.getValue(User::class.java)?.uid != mAuth.uid) {
                        if (query == postSnapshot.getValue(User::class.java)!!.name.toString().toLowerCase()) {
                            userList.add(postSnapshot.getValue(User::class.java)!!)
                        }
                        adapter.notifyDataSetChanged()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, error.message, Toast.LENGTH_LONG).show()
            }

        })
    }

    fun addSearchCharacter(query: String) {
        mDbRef.child("user").addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                userList.clear()
                adapter.addUidLogin(mAuth.uid)

                for (postSnapshot in snapshot.children) {
                    if (postSnapshot.getValue(User::class.java)?.uid != null && postSnapshot.getValue(User::class.java)?.uid != mAuth.uid) {
//                        if (postSnapshot.getValue(User::class.java)!!.name?.toLowerCase().contentEquals(query.toLowerCase())) {
//                            userList.add(postSnapshot.getValue(User::class.java)!!)
//                        }
                        if (postSnapshot.getValue(User::class.java)!!.name?.toLowerCase()!!.startsWith(query.toLowerCase())) {
                            userList.add(postSnapshot.getValue(User::class.java)!!)
                        }
                        adapter.notifyDataSetChanged()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@MainActivity, error.message, Toast.LENGTH_LONG).show()
            }

        })
    }
}