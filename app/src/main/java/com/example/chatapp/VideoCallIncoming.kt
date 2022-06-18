package com.example.chatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import com.example.chatapp.chat.ChatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.activity_chat.*
import java.util.*
import kotlin.collections.HashMap

class VideoCallIncoming : AppCompatActivity() {

    lateinit var imgAvatarIncoming: ImageView
    lateinit var txtName: TextView

    lateinit var btnAccept: FloatingActionButton
    lateinit var btnDecline: FloatingActionButton

    lateinit var database: FirebaseDatabase

    lateinit var mDbRef: DatabaseReference

    var loginUid: String = ""
    var friendUid: String = ""
    var hasMore: Boolean = false
    lateinit var userLogin: User
    lateinit var userFriend: User

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
        hasMore = intent.getBooleanExtra("hasMore", false)
        userLogin = intent.getSerializableExtra("userLogin") as User
        userFriend = intent.getSerializableExtra("userFriend") as User

        txtName.text = userFriend.name

        FirebaseStorage.getInstance().reference.child("images").child(friendUid).downloadUrl.addOnSuccessListener {
            Picasso.get().load(it).into(imgAvatarIncoming)
        }

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

                val intent = Intent(this@VideoCallIncoming, ChatActivity::class.java)
                intent.putExtra("uidLogin", loginUid)
                intent.putExtra("uidFriend", friendUid)
                intent.putExtra("hasMore", hasMore)
                intent.putExtra("userLogin", userLogin)
                intent.putExtra("userFriend", userFriend)
                startActivity(intent)
            }

        })

        database.reference.child("user").child(friendUid).addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                val user: User? = snapshot.getValue(User::class.java)
                if (user != null && user.uid == friendUid) {
                    if (!user.calling) {
                        val intent = Intent(this@VideoCallIncoming, ChatActivity::class.java)
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

    override fun onResume() {
        super.onResume()

    }
}