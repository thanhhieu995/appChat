package com.example.chatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.example.chatapp.chat.ChatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.database.*
import com.google.firebase.database.ktx.getValue
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class VideoCallOutgoing : AppCompatActivity() {

    private lateinit var btnDecline: FloatingActionButton
    var loginUid: String = ""
    var friendUid: String = ""
    var hasMore: Boolean = false
    lateinit var userLogin: User
    lateinit var userFriend: User

    lateinit var mDbRef: DatabaseReference

    lateinit var txtNameOutgoing: TextView
    lateinit var imgAvatarOutgoing: ImageView

    lateinit var database: FirebaseDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_call_outgoing)

        btnDecline = findViewById(R.id.btn_decline_outgoing)
        txtNameOutgoing = findViewById(R.id.txtName_outgoing)
        imgAvatarOutgoing = findViewById(R.id.img_avatar_outgoing)

        database = FirebaseDatabase.getInstance()

        loginUid = intent.getSerializableExtra("uidLogin") as String
        friendUid = intent.getSerializableExtra("uidFriend") as String
        hasMore = intent.getBooleanExtra("hasMore", false)
        userLogin = intent.getSerializableExtra("userLogin") as User
        userFriend = intent.getSerializableExtra("userFriend") as User

//        val bundle: Bundle? = intent.extras
//
//        if (bundle != null) {
//
//        } else {
//            Toast.makeText(this, "Data missing", Toast.LENGTH_LONG).show()
//        }

        txtNameOutgoing.text = userFriend.name

       FirebaseStorage.getInstance().reference.child("images").child(friendUid)
           .downloadUrl.addOnSuccessListener {
               Picasso.get().load(it).into(imgAvatarOutgoing)
           }

        btnDecline.setOnClickListener {
            FirebaseDatabase.getInstance().reference.child("user").child(loginUid).child("calling").setValue(false)

            val intent = Intent(this@VideoCallOutgoing, ChatActivity::class.java)
            intent.putExtra("uidLogin", loginUid)
            intent.putExtra("uidFriend", friendUid)
            intent.putExtra("hasMore", hasMore)
            intent.putExtra("userLogin", userLogin)
            intent.putExtra("userFriend", userFriend)
            startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        database.reference.child("user").child(loginUid).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val user: User? = snapshot.getValue(User::class.java)
                if (user != null && user.uid == loginUid) {
                    if (!user.calling) {
                        val intent = Intent(this@VideoCallOutgoing, ChatActivity::class.java)
                        intent.putExtra("uidLogin", loginUid)
                        intent.putExtra("uidFriend", friendUid)
                        intent.putExtra("hasMore", hasMore)
                        intent.putExtra("userLogin", userLogin)
                        intent.putExtra("userFriend", userFriend)
                        startActivity(intent)
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }
}