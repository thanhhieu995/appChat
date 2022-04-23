package com.example.chatapp.main

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        mAuth = FirebaseAuth.getInstance()
        mDbRef = FirebaseDatabase.getInstance().getReference()

        userList = ArrayList()
        adapter = UserAdapter(this, userList)

        userRecyclerView = findViewById(R.id.userRecyclerView)

        userRecyclerView.layoutManager = LinearLayoutManager(this)
        userRecyclerView.adapter = adapter

        statusAccount(mAuth.uid)

        mDbRef.child("user").addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                userList.clear()
                for (postSnapshot in snapshot.children) {

                    val currentUser = postSnapshot.getValue(User::class.java)

                    if (currentUser != null && mAuth.uid != currentUser.uid) {
                        adapter.addItems(currentUser)
                    }
                }
                adapter.addUidLogin(mAuth.uid)
                adapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {

        if(item.itemId == R.id.logout) {
            mAuth.signOut()
            val intent = Intent(this@MainActivity, LogIn::class.java)
            startActivity(intent)
            finish()
            statusAccount(mAuth.uid)
            return true
        }
        return false
    }

    override fun onPause() {
        hasMore = true
        super.onPause()
        statusAccount(mAuth.uid)
    }

    override fun onStop() {
        //hasMore = true
        super.onStop()
        statusAccount(mAuth.uid)
    }


    private fun statusAccount(loginUid: String? ) {
        val studentRef = FirebaseDatabase.getInstance().getReference("student").child(loginUid!!)
        val connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected")

        connectedRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val connected = snapshot.getValue(Boolean::class.java)!!
                if (connected) {
                    if (hasMore) {
                        studentRef.child("status").setValue("offline!!!")
                    } else {
                        studentRef.child("status").onDisconnect().setValue("Offline!")
                        studentRef.child("status").setValue("Online")
                    }
                } else {
                    studentRef.child("status").setValue("offline")
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

}