package com.example.chatapp.main

import android.annotation.SuppressLint
import android.app.ActionBar
import android.app.SearchManager
import android.content.Context
import android.content.Intent
import android.graphics.Typeface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import android.widget.Toolbar
import androidx.appcompat.widget.SearchView
import androidx.core.graphics.green
import androidx.core.graphics.red
import androidx.core.graphics.toColorInt
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.User
import com.example.chatapp.accountLogin.LogIn
import com.example.chatapp.chat.ChatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.firebase.storage.FirebaseStorage
import kotlinx.android.synthetic.main.actionbar_title.*
import kotlinx.android.synthetic.main.actionbar_title.view.*
import java.io.File
import java.net.URI

class MainActivity : AppCompatActivity() {

    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userList: ArrayList<User>
    private lateinit var adapter: UserAdapter

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference

    private var hasMore: Boolean = false

    var statusUserLogin: String = ""

    var statusFriend: String = ""

    lateinit var actionTitle: TextView

    var title: String = ""

    var user: User = User()



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

        userList.clear()

        //var customView: View = LayoutInflater.from(this).inflate(R.layout.actionbar_title, null, false)
        //actionTitle.text = mAuth.currentUser?.displayName
        //supportActionBar?.title = mAuth.currentUser?.displayName
//        actionTitle.setOnClickListener {
//            Toast.makeText(this, "title clicked", Toast.LENGTH_LONG).show()

        //actionbar_title.actionbarTitle.text = mAuth.currentUser?.displayName
        //actionbar_title.actionbarTitle.text = mAuth.currentUser?.displayName ?:
//        if (actionbar != null) {
//
//            actionBar?.setDisplayShowTitleEnabled(false)
//            actionBar?.setDisplayShowCustomEnabled(true)
////            customTitle.setTypeface(Typeface.MONOSPACE);
//            val customView: View = layoutInflater.inflate(R.layout.actionbar_title, null)
//            actionTitle = customView.findViewById(R.id.actionbarTitle)
//            actionTitle.typeface = Typeface.MONOSPACE
//
//            actionTitle.setOnClickListener(object : View.OnClickListener{
//                override fun onClick(v: View?) {
//                    Log.d("MainActivity", "success")
//                }
//
//            })
//
//            actionbar.customView = customView
//        }
        //actionbar.title = findViewById<>()

       // supportActionBar.title = findViewById<TextView>(R.id.actionbarTitle)

//        supportActionBar?.customView?.actionbarTitle?.setOnClickListener(object : View.OnClickListener{
//            override fun onClick(v: View?) {
//                val intent = Intent(this@MainActivity, ChatActivity::class.java)
//                startActivity(intent)
//            }
//
//        })

        statusAccount(mAuth.uid)
        addFriendUser()

       // addAvatar()
        //supportActionBar?.title = findViewById<Toolbar>(androidx.appcompat.R.id.action_bar).toString()
    }

    override fun onResume() {
        super.onResume()
        hasMore = intent.getBooleanExtra("hasMore", false)
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
        //menuItem.title = mAuth.currentUser!!.displayName
        //val user: FirebaseUser? = mAuth.currentUser
       // menuItem.title = user.name
        mDbRef.child("user").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (postSnapshot in snapshot.children) {
                    if (postSnapshot.getValue(User::class.java)?.uid == mAuth.currentUser?.uid) {
                        menuItem.title = user.name
                        //menuItem.setTitle(titleColor.red)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        return super.onPrepareOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == R.id.logout) {
            //hasMore = true
            if (mAuth.uid != null) {
                FirebaseDatabase.getInstance().getReference("user").child(mAuth.uid.toString())
                    .child("status").setValue("offline")
            }
            mAuth.signOut()
            val intent = Intent(this@MainActivity, LogIn::class.java)
            startActivity(intent)
            finish()
            return true
        }
//        if (item.itemId == com.google.android.material.R.id.action_bar_title) {
//            val intent : Intent = Intent()
//            startActivity(intent)
//        }
//        if (item.itemId == androidx.appcompat.R.id.action_bar_title) {
//            Toast.makeText(this, "success actionbar", Toast.LENGTH_LONG).show()
//        }
        return false
    }

    private fun statusAccount(Uid: String? ) {
        val studentRef = FirebaseDatabase.getInstance().getReference("user").child(Uid!!)
        val connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected")

        connectedRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val connected = snapshot.getValue(Boolean::class.java)!!
                if (connected) {
                    studentRef.child("status").onDisconnect().setValue("offline")
                    studentRef.child("status").setValue("online")
                }else {
                    studentRef.child("status").setValue("offline")
                    }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, LogIn::class.java)
        FirebaseDatabase.getInstance().getReference("user").child(mAuth.uid!!).child("status").setValue("offline")
        mAuth.signOut()
        startActivity(intent)
    }


    private fun addFriendUser() {
        mDbRef.child("user").addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                userList.clear()
                adapter.addUidLogin(mAuth.uid)

                for (postSnapshot in snapshot.children) {
                    if (postSnapshot.getValue(User::class.java)?.uid != null && mAuth.uid != null && postSnapshot.getValue(User::class.java)?.uid != mAuth.uid) {
                        //statusAccount(postSnapshot.getValue(User::class.java)?.uid)
                        userList.add(postSnapshot.getValue(User::class.java)!!)
                        // adapter.addItems(postSnapshot.getValue(User::class.java))
                        //addAvatar()
                        //adapter.addAvatar()
                        adapter.notifyDataSetChanged()
                    } else {
                        user = postSnapshot.getValue(User::class.java)!!
                        supportActionBar?.title = postSnapshot.getValue(User::class.java)?.name
                        //title = postSnapshot.getValue(User::class.java)?.name.toString()
                        //getTitle(title)
                        //onMenuItemSelected(title, R.id.action_name_title)
                        //onPrepareOptionsMenu(menu)
                        //val menuItem: MenuItem = menu.findItem(R.id.action_name_title)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

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

            }

        })
    }
}