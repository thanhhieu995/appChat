package com.example.chatapp.chat

import android.content.Context
import android.content.res.Configuration
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.WindowManager
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.Message
import com.example.chatapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.firestore.FirebaseFirestore
import com.squareup.okhttp.internal.Internal.instance
import io.grpc.util.TransmitStatusRuntimeExceptionInterceptor.instance
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap

class ChatActivity : AppCompatActivity() {

    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageBox: EditText
    private lateinit var sentButton: ImageView
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var messageList: ArrayList<Message>
    private lateinit var mDbRef: DatabaseReference

    private lateinit var timeSent: TextView
    private lateinit var timeReceive: TextView

    var receiverRoom: String? = null
    var senderRoom: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)


        //val intent = Intent()

        val name = intent.getSerializableExtra("name")
        var receiverUid = intent.getSerializableExtra("uid")

        val senderUid = FirebaseAuth.getInstance().currentUser?.uid // lay uid tu account???
        //???????????????


        mDbRef = FirebaseDatabase.getInstance().reference

        senderRoom = receiverUid.toString() //+ senderUid
        receiverRoom = senderUid //+ receiverUid

        supportActionBar?.title = name.toString()

        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        messageBox = findViewById(R.id.messageBox)
        sentButton = findViewById(R.id.img_sent)

//        timeSent = findViewById(R.id.txt_sent_message)
//        timeReceive = findViewById(R.id.txt_receive_message)

        messageList = ArrayList()
        chatAdapter = ChatAdapter(this, messageList)


        chatRecyclerView.layoutManager = LinearLayoutManager(this)


        chatRecyclerView.adapter = chatAdapter


        val sdf = SimpleDateFormat("dd/M/yyyy  hh:mm:ss aaa")
        val currentDate = sdf.format(Date())
        //System.out.println(" C DATE is  "+currentDate)


        mDbRef.child("chats").child(senderRoom!!).child("message")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (postSnapshot in snapshot.children) {

                        //messageList.clear()

                        val message = postSnapshot.getValue(Message::class.java)
                        messageList.add(message!!)
                    }
                    //chatRecyclerView.scrollToPosition(messageList.size)
                    chatAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

        mDbRef.child("chats").child(receiverRoom!!).child("messages")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (postSnapshot in snapshot.children) {
                        val message = postSnapshot.getValue(Message::class.java)
                        messageList.add(message!!)

                        val hashMap: HashMap<String, Boolean> = HashMap()
                        hashMap["isSeen"] = true
                        mDbRef.updateChildren(hashMap as Map<String, Any>)

                    }

                    chatRecyclerView.scrollToPosition(messageList.size - 1)

//                    (chatRecyclerView.layoutManager as LinearLayoutManager).reverseLayout = true
//                    (chatRecyclerView.layoutManager as LinearLayoutManager).stackFromEnd = true

                    chatAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })


        sentButton.setOnClickListener {
            sendChatMessage(receiverUid as String?, currentDate)

            chatRecyclerView.scrollToPosition(messageList.size - 1)
//            (chatRecyclerView.layoutManager as LinearLayoutManager).reverseLayout = true
//            (chatRecyclerView.layoutManager as LinearLayoutManager).stackFromEnd = true


//            chatRecyclerView.layoutManager = LinearLayoutManager(this)
//            chatRecyclerView.adapter = chatAdapter

            //chatAdapter.messageList
            chatAdapter.notifyDataSetChanged()
        }
    }

    private fun sendChatMessage(receiveUid: String?, currenDate:String?) {
        val message = messageBox.text.toString()
        val messageObject = Message(message, receiveUid, currenDate)
        if (receiveUid != null && message.trim().isNotEmpty()) {
            chatAdapter.addMessage(messageObject, receiveUid)


            mDbRef.child("chats").child(senderRoom!!).child("message").push()
                .setValue(messageObject)


            mDbRef.child("chats").child(receiverRoom!!).child("messages").push()
                .setValue(messageObject)
        } else {
            Toast.makeText(this@ChatActivity, "Please enter the character!!!!", Toast.LENGTH_LONG)
                .show()
        }
        messageBox.setText("")
    }

    private fun seeMsg(senderId: String?){
        val query = FirebaseFirestore.getInstance()
                .collection("chat")
                .document()
                .collection("Message")
                .whereEqualTo("senderId", senderId)
                .whereEqualTo("read", false)
                .get()
    }
}