package com.example.chatapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import java.util.*
import kotlin.collections.HashMap

class VideoCallIncoming : AppCompatActivity() {

    lateinit var imgAvatarIncoming: ImageView
    lateinit var txtName: TextView

    lateinit var btnAccept: Button
    lateinit var btnDecline: Button

    lateinit var database: FirebaseDatabase

    lateinit var mDbRef: DatabaseReference

    var loginUid: String = ""
    var friendUid: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_call_incoming)

        database = FirebaseDatabase.getInstance()
        imgAvatarIncoming = findViewById(R.id.img_avatar_incoming)
        txtName = findViewById(R.id.txtName_incoming)
        btnAccept = findViewById(R.id.btn_accept_incoming)
        btnDecline = findViewById(R.id.btn_decline_incoming)

        loginUid = intent.getStringExtra("loginUid").toString()
        friendUid = intent.getStringExtra("friendUid").toString()


        database.reference.child("user").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if (snapshot.exists()) {

                } else {

                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

        btnDecline.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
//                mDbRef.child("user").child(friendUid).addValueEventListener(object : ValueEventListener{
//                    override fun onDataChange(snapshot: DataSnapshot) {
//                        for (postSnapshot in snapshot.children) {
//                            val hashMap: HashMap<String, Boolean> = HashMap()
//                            hashMap.put("calling", false)
//                            postSnapshot.ref.updateChildren(hashMap as Map<String, Any>)
//                        }
//                    }
//
//                    override fun onCancelled(error: DatabaseError) {
//
//                    }
//
//                })

                FirebaseDatabase.getInstance().reference.child("user").child(friendUid).child("calling").setValue(false)
            }

        })
    }
}