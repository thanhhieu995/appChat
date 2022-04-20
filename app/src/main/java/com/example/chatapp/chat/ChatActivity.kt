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
import com.google.firebase.auth.FirebaseAuth
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

    private  var statusMessage: String = ""


    private var roomReceive: String? = null
    private var roomSent: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val name = intent.getSerializableExtra("name")
        val receiverUid = intent.getSerializableExtra("uid")

        val senderUid = FirebaseAuth.getInstance().currentUser?.uid // lay uid tu account???
        //???????????????


        mDbRef = FirebaseDatabase.getInstance().reference

//        roomSent = senderUid + receiverUid
//        romReceive = receiverUid.toString() + senderUid

        roomSent = senderUid
        roomReceive = receiverUid.toString()

        supportActionBar?.title = name.toString()

        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        messageBox = findViewById(R.id.messageBox)
        sentButton = findViewById(R.id.img_sent)

        messageList = ArrayList()
        chatAdapter = ChatAdapter(this, messageList)


        chatRecyclerView.layoutManager = LinearLayoutManager(this)


        chatAdapter.addUid(receiverUid.toString())

        chatRecyclerView.adapter = chatAdapter


        val sdf = SimpleDateFormat("dd/M/yyyy  hh:mm:ss aaa")
        val currentDate = sdf.format(Date())


        mDbRef.child("chats").child(roomSent!!).child("message_sent")
            .addValueEventListener(object : ValueEventListener {
                @SuppressLint("SetTextI18n")
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (postSnapshot in snapshot.children) {

                        val message = postSnapshot.getValue(Message::class.java)

                        messageList.add(message!!)

                        chatAdapter.notifyDataSetChanged()
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

        mDbRef.child("chats").child(roomReceive!!).child("message_receive")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (postSnapshot in snapshot.children) {
                        val message = postSnapshot.getValue(Message::class.java)

                        messageList.add(message!!)

                    }

                    chatRecyclerView.scrollToPosition(messageList.size - 1)

                    chatAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })


        sentButton.setOnClickListener {
            sendChatMessage(senderUid, currentDate, statusMessage)

            chatRecyclerView.scrollToPosition(messageList.size - 1)

            chatAdapter.notifyDataSetChanged()
        }
    }

    private fun sendChatMessage(senderUid: String?, currenDate:String?, statusMessage: String) {
        val message = messageBox.text.toString()
        val messageObject = Message(message, senderUid, currenDate, statusMessage)
        if (senderUid != null && message.trim().isNotEmpty()) {
            chatAdapter.addMessage(messageObject, senderUid)


            mDbRef.child("chats").child(roomSent!!).child("message_sent").push()
                .setValue(messageObject)


            mDbRef.child("chats").child(roomReceive!!).child("message_receive").push()
                .setValue(messageObject)
        } else {
            Toast.makeText(this@ChatActivity, "Please enter the character!!!!", Toast.LENGTH_LONG)
                .show()
        }
        messageBox.setText("")
    }

    private fun seeMessage(receiveUid: String?) {
        val reference = FirebaseDatabase.getInstance().getReference("chats")

    }

    private fun status (chatRecentUid: String?) {
        val chatRecentUid = chatRecentUid
        chatAdapter.addStastus(chatRecentUid)
    }

    override fun onResume() {
        super.onResume()
        chatAdapter.addStastus("online")
        chatAdapter.notifyDataSetChanged()
    }

    override fun onPause() {
        super.onPause()
        chatAdapter.addStastus("offline")
        chatAdapter.notifyDataSetChanged()
    }
}