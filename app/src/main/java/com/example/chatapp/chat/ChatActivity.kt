package com.example.chatapp.chat

import android.annotation.SuppressLint
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.Message
import com.example.chatapp.R
import com.example.chatapp.Status
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*


class ChatActivity : AppCompatActivity() {

    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageBox: EditText
    private lateinit var sentButton: ImageView
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var messageList: ArrayList<Message>
    private lateinit var mDbRef: DatabaseReference

    private lateinit var statusMessage: String


    private var roomSender: String? = null
    private var roomReceiver: String? = null

    private var status: String? = ""


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val name = intent.getSerializableExtra("name")
        val friendUid = intent.getSerializableExtra("uidFriend")

        val loginUid = intent.getSerializableExtra("uidLogin")

        status = intent.getSerializableExtra("status").toString()


//        val loginUid = FirebaseAuth.getInstance().currentUser?.uid // lay uid tu account???
//        //???????????????


        mDbRef = FirebaseDatabase.getInstance().reference


        roomReceiver = loginUid.toString() + friendUid
        roomSender = friendUid.toString() + loginUid

        supportActionBar?.title = name.toString() + " " +  status

        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        messageBox = findViewById(R.id.messageBox)
        sentButton = findViewById(R.id.img_sent)

        messageList = ArrayList()
        chatAdapter = ChatAdapter(this, messageList)


        chatRecyclerView.layoutManager = LinearLayoutManager(this)


        chatRecyclerView.adapter = chatAdapter

        chatRecyclerView.scrollToPosition(messageList.size - 1)

        val sdf = SimpleDateFormat("dd/M/yyyy  hh:mm:ss aaa")
        val currentDate = sdf.format(Date())

        mDbRef.child("chats").child(roomSender!!).child("messages")
            .addValueEventListener(object : ValueEventListener {
                @SuppressLint("SetTextI18n")
                override fun onDataChange(snapshot: DataSnapshot) {

                    messageList.clear()

                    for (postSnapshot in snapshot.children) {

                        val message = postSnapshot.getValue(Message::class.java)

                        //messageList.add(message!!)
                        if (message != null) {
                            chatAdapter.addMessage(message, loginUid as String, friendUid as String)
                        }
                        chatAdapter.notifyDataSetChanged()
                    }
                    chatRecyclerView.scrollToPosition(messageList.size - 1)
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

        statusAccount(loginUid as String?)


//        if (loginUid != null) {
//            mDbRef.child("student").child(loginUid)
//                .addValueEventListener(object : ValueEventListener {
//                    override fun onDataChange(snapshot: DataSnapshot) {
//                        for (postSnapshot in snapshot.children) {
//                            statusMessage = postSnapshot.getValue(String::class.java).toString()
//                        }
//                    }
//
//                    override fun onCancelled(error: DatabaseError) {
//
//                    }
//
//                })
//        }

        //chatAdapter.addStatus(statusMessage)


        sentButton.setOnClickListener {
            sendChatMessage(
                loginUid.toString(),
                currentDate,
                friendUid as String?
            )
            //statusAccount(loginUid as String?)
            chatRecyclerView.scrollToPosition(messageList.size - 1)

            chatAdapter.notifyDataSetChanged()
        }
    }

    private fun sendChatMessage(
        loginUid: String?,
        currentDate: String?,
        friendUid: String?
    ) {
        val message = messageBox.text.toString()
        val messageObject = Message(message, loginUid, currentDate)
        if (loginUid != null && message.trim().isNotEmpty()) {
            if (friendUid != null) {
                chatAdapter.addMessage(messageObject, loginUid, friendUid)
            }
            mDbRef.child("chats").child(roomSender!!).child("messages").push()
                .setValue(messageObject).addOnSuccessListener{
                    mDbRef.child("chats").child(roomReceiver!!).child("messages").push()
                        .setValue(messageObject)
                }


        } else {
            Toast.makeText(this@ChatActivity, "Please enter the character!!!!", Toast.LENGTH_LONG)
                .show()
        }
        messageBox.setText("")
        chatRecyclerView.scrollToPosition(messageList.size - 1)
    }

    private fun statusAccount(loginUid: String?) {
        val studentRef = FirebaseDatabase.getInstance().getReference("student").child(loginUid!!)
        val connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected")

        connectedRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val connected = snapshot.getValue(Boolean::class.java)!!
                if (connected) {
                    studentRef.child("status").onDisconnect().setValue("offline")
                    studentRef.child("status").setValue("Online")
                } else {
                    studentRef.child("status").setValue("offline")
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}