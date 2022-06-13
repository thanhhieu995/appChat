package com.example.chatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.example.chatapp.chat.ChatActivity
import com.google.android.material.floatingactionbutton.FloatingActionButton

class VideoCallOutgoing : AppCompatActivity() {

    lateinit var btnDecline: FloatingActionButton
    var loginUid: String = ""
    var friendUid: String = ""
    var hasMore: Boolean = false
    lateinit var userLogin: User
    lateinit var userFriend: User

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_call_outgoing)

        btnDecline = findViewById(R.id.btn_decline_outgoing)
        loginUid = intent.getSerializableExtra("uidLogin") as String
        friendUid = intent.getSerializableExtra("uidFriend") as String
        hasMore = intent.getBooleanExtra("hasMore", false)
        userLogin = intent.getSerializableExtra("userLogin") as User
        userFriend = intent.getSerializableExtra("userFriend") as User

        btnDecline.setOnClickListener {
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