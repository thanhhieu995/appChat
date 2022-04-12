package com.example.chatapp.chat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.Message
import com.example.chatapp.R
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*

class ChatActivity : AppCompatActivity() {

    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageBox: EditText
    private lateinit var sentButton: ImageView
    private lateinit var messageAdapter: MessageAdapter
    private lateinit var messageList: ArrayList<Message>
    private lateinit var mDbRef: DatabaseReference

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


        mDbRef = FirebaseDatabase.getInstance().getReference()

        senderRoom = receiverUid.toString() //+ senderUid
        receiverRoom = senderUid //+ receiverUid

        supportActionBar?.title = name.toString()

        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        messageBox = findViewById(R.id.messageBox)
        sentButton = findViewById(R.id.img_sent)

        messageList = ArrayList()
        messageAdapter = MessageAdapter(this, messageList)


        chatRecyclerView.layoutManager = LinearLayoutManager(this)

        chatRecyclerView.scrollToPosition(messageList.size - 1)

        (chatRecyclerView.layoutManager as LinearLayoutManager).reverseLayout = true
        (chatRecyclerView.layoutManager as LinearLayoutManager).stackFromEnd = true


        chatRecyclerView.adapter = messageAdapter


        mDbRef.child("chats").child(senderRoom!!).child("message")
            .addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for(postSnapshot in snapshot.children) {

                        //messageList.clear()

                        val message = postSnapshot.getValue(Message::class.java)
                        messageList.add(message!!)
                    }
                    //chatRecyclerView.scrollToPosition(messageList.size)
                    messageAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

        mDbRef.child("chats").child(receiverRoom!!).child("message")
            .addValueEventListener(object: ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (postSnapshot in snapshot.children) {
                        val message = postSnapshot.getValue(Message::class.java)
                        messageList.add(message!!)
                    }
                    messageAdapter.notifyDataSetChanged()
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

        sentButton.setOnClickListener {
            sendChatMessage(receiverUid as String?)

            chatRecyclerView.scrollToPosition(messageList.size - 1)
            (chatRecyclerView.layoutManager as LinearLayoutManager).reverseLayout = true
            (chatRecyclerView.layoutManager as LinearLayoutManager).stackFromEnd = true
        }
    }

    private fun sendChatMessage(receiveUid: String?) {
        val message = messageBox.text.toString()
        val messageObject = Message(message, receiveUid)
        if (receiveUid != null) {
            messageAdapter.addMessage(messageObject, receiveUid)
        }

        mDbRef.child("chats").child(senderRoom!!).child("message").push().setValue(messageObject)
//        mDbRef.child("chats").child(senderRoom!!).child("message").push()
//            .setValue(messageObject)
//            .addOnSuccessListener(object: OnSuccessListener<Void> {
//                override fun onSuccess(p0: Void?) {
//                    mDbRef.child("chats").child(receiverRoom!!).child("message").push()
//                        .setValue(messageObject)
//                }
//            })
        messageBox.setText("")
    }
}