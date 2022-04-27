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

    var statusUserLogin: String = ""

    var statusFriend: String = ""

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

        userList.clear()

        statusAccount(mAuth.uid)

        mDbRef.child("user").addValueEventListener(object: ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                userList.clear()
                adapter.addUidLogin(mAuth.uid)

                for (postSnapshot in snapshot.children) {
                    //userList.clear()
//                        val currentUser = postSnapshot.getValue(User::class.java)
//
//                        if (currentUser?.uid != null && mAuth.uid != currentUser.uid) {
//                                if (currentUser.uid != null) {
//                                    //statusAccount(currentUser.uid)
//                                }
//                            adapter.addItems(currentUser)
//                        }


                    if (postSnapshot.getValue(User::class.java)?.uid != null && postSnapshot.getValue(User::class.java)?.uid != mAuth.uid) {
                        //statusAccount(postSnapshot.getValue(User::class.java)?.uid)
                            userList.add(postSnapshot.getValue(User::class.java)!!)
                       // adapter.addItems(postSnapshot.getValue(User::class.java))
                        adapter.notifyDataSetChanged()
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    override fun onResume() {
        super.onResume()
        hasMore = intent.getBooleanExtra("hasMore", false)
    }


    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu, menu)
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

//    override fun onRestart() {
//        super.onRestart()
//        hasMore = false
//        //statusAccount(mAuth.uid)
//    }
//
//    override fun onPause() {
////        var logIn = LogIn()
////        if (logIn.hasMore) {
////            hasMore = true
////        }
//        hasMore = true
//        super.onPause()
//        if (mAuth.uid != null) {
//            //statusAccount(mAuth.uid)
//        }
//    }

//    override fun onStop() {
//        hasMore = true
//        super.onStop()
//        statusAccount(mAuth.uid)
//    }


    private fun statusAccount(Uid: String? ) {
        val studentRef = FirebaseDatabase.getInstance().getReference("user").child(Uid!!)
        val connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected")

        connectedRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val connected = snapshot.getValue(Boolean::class.java)!!
                if (connected) {
                    studentRef.child("status").onDisconnect().setValue("Offline!")
                    studentRef.child("status").setValue("Online")
                }else {
                    studentRef.child("status").setValue("offline!!!")
                    }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }


//    private fun getStatusFriend(uidFriend: String?) {
//        mDbRef.child("user").child(uidFriend.toString())
//            .addValueEventListener(object : ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    for (postSnapshot in snapshot.children) {
//                        statusFriend = postSnapshot.getValue(String:: class.java).toString()
//                    }
//                    adapter.addStatusAccountLogin(statusFriend)
//                    adapter.notifyDataSetChanged()
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//
//                }
//
//            })
//    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this, LogIn::class.java)
        startActivity(intent)
    }

//    fun onBackPress() {
//        super.onBackPressed()
//        FirebaseDatabase.getInstance().getReference("user").child(mAuth.uid.toString()).child("status").setValue("offline")
//    }
}