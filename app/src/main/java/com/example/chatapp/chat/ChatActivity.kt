package com.example.chatapp.chat

import android.annotation.SuppressLint
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.Message
import com.example.chatapp.R
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList

class ChatActivity : AppCompatActivity() {

    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageBox: EditText
    private lateinit var sentButton: ImageView
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var messageList: ArrayList<Message>
    private lateinit var mDbRef: DatabaseReference

    private var statusMessage: String = ""


    private var roomSender: String? = null
    private var roomReceiver: String? = null

    var hasMore: Boolean? = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val name = intent.getSerializableExtra("name")
        val friendUid = intent.getSerializableExtra("uidFriend")

        val loginUid = intent.getSerializableExtra("uidLogin")

//        val loginUid = FirebaseAuth.getInstance().currentUser?.uid // lay uid tu account???
//        //???????????????


        mDbRef = FirebaseDatabase.getInstance().reference

//        roomSent = senderUid + receiverUid
//        romReceive = receiverUid.toString() + senderUid

        roomReceiver = loginUid.toString() + friendUid
        roomSender = friendUid.toString() + loginUid

        supportActionBar?.title = name.toString()

        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        messageBox = findViewById(R.id.messageBox)
        sentButton = findViewById(R.id.img_sent)

        messageList = ArrayList()
        chatAdapter = ChatAdapter(this, messageList)


        chatRecyclerView.layoutManager = LinearLayoutManager(this)


        //chatAdapter.addUid(friendUid.toString())

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
                    //chatAdapter.notifyDataSetChanged()
                    chatRecyclerView.scrollToPosition(messageList.size - 1)
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

//        mDbRef.child("chats").child(roomReceiver!!).child("messages")
//            .addValueEventListener(object : ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    for (postSnapshot in snapshot.children) {
//                        val message = postSnapshot.getValue(Message::class.java)
//
//                        messageList.add(message!!)
//
//                    }
//
//                    chatRecyclerView.scrollToPosition(messageList.size - 1)
//
//                    chatAdapter.notifyDataSetChanged()
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//
//                }
//
//            })

        sentButton.setOnClickListener {
            sendChatMessage(
                loginUid.toString(),
                currentDate,
                statusMessage,
                hasMore!!,
                friendUid as String?
            )

            chatRecyclerView.scrollToPosition(messageList.size - 1)

            chatAdapter.notifyDataSetChanged()
        }
    }

    private fun sendChatMessage(
        loginUid: String?,
        currentDate: String?,
        statusMessage: String,
        hasMore: Boolean,
        friendUid: String?
    ) {
        val message = messageBox.text.toString()
        val messageObject = Message(message, loginUid, currentDate, statusMessage)
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

    private fun seeMessage(receiveUid: String?) {
        val reference = FirebaseDatabase.getInstance().getReference("chats")

    }

    private fun status(chatRecentUid: String?) {
        val chatRecentUid = chatRecentUid
        chatAdapter.addStatus(chatRecentUid)
    }

    override fun onResume() {
        super.onResume()
        chatAdapter.addStatus("online")
        chatRecyclerView.scrollToPosition(messageList.size - 1)
        chatAdapter.notifyDataSetChanged()
    }

    override fun onPause() {
        super.onPause()
        chatRecyclerView.scrollToPosition(messageList.size - 1)
        chatAdapter.addStatus("offline")
        chatAdapter.notifyDataSetChanged()
    }
}