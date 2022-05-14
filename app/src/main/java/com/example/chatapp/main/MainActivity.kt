package com.example.chatapp.main

import android.app.SearchManager
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.widget.SearchView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.R
import com.example.chatapp.User
import com.example.chatapp.accountLogin.LogIn
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {

    private lateinit var userRecyclerView: RecyclerView
    private lateinit var userList: ArrayList<User>
    private lateinit var adapter: UserAdapter

    private lateinit var mAuth: FirebaseAuth
    private lateinit var mDbRef: DatabaseReference

    private var hasMore: Boolean = false

    var statusUserLogin: String = ""

    var statusFriend: String = ""

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

        supportActionBar?.title = mAuth.currentUser?.displayName

        statusAccount(mAuth.uid)
        addFriendUser()
    }

    override fun onResume() {
        super.onResume()
        hasMore = intent.getBooleanExtra("hasMore", false)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
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
                        adapter.notifyDataSetChanged()
                    } else {
                        supportActionBar?.title = postSnapshot.getValue(User::class.java)?.name
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