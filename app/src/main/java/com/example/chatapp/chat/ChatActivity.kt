package com.example.chatapp.chat

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.Message
import com.example.chatapp.R
import com.example.chatapp.User
import com.example.chatapp.main.MainActivity
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.sent.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap


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

    private var statusFriend: String? = "1"


    var seen: Boolean = false

    var hasMore: Boolean = false

    var friendUid: String? = ""

    var loginUid: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val name = intent.getSerializableExtra("name")
        friendUid = intent.getSerializableExtra("uidFriend") as String?

        loginUid = intent.getSerializableExtra("uidLogin") as String?

        hasMore = intent.getBooleanExtra("hasMore", false)

       // val statusFriend = intent.getSerializableExtra("statusFriend")

        //status = intent.getSerializableExtra("status").toString()


//        val loginUid = FirebaseAuth.getInstance().currentUser?.uid // lay uid tu account???
//        //???????????????


        mDbRef = FirebaseDatabase.getInstance().reference


        roomReceiver = loginUid.toString() + friendUid
        roomSender = friendUid.toString() + loginUid

        //supportActionBar?.title = name.toString()  + " " + statusFriend

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

//                        messageList.add(message!!)

                        if (message != null && statusFriend == "Online" && hasMore) {
                            if (message.receiveId?.equals(loginUid) == true && message.senderId?.equals(friendUid) == true) {
                                var hashMap: HashMap<String, Boolean> = HashMap()
                                hashMap.put("seen", true)
                                postSnapshot.ref.updateChildren(hashMap as Map<String, Any>)
                                //chatAdapter.addSeen(message.seen)
                               chatRecyclerView.adapter = chatAdapter
                            }
                        }
                        if (message != null) {
                            chatAdapter.addMessage(message, loginUid as String, friendUid as String)
                        }
                    }
                    chatAdapter.notifyDataSetChanged()
                    chatRecyclerView.scrollToPosition(messageList.size - 1)
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

        mDbRef.child("chats").child(roomReceiver!!).child("messages")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    for (postSnapshot in snapshot.children) {
                        val message = postSnapshot.getValue(Message::class.java)

                        if (message != null && statusFriend == "Online" && hasMore) {
                            if (message.receiveId?.equals(loginUid) == true && message.senderId?.equals(friendUid) == true) {
                                var hashMap: HashMap<String, Boolean> = HashMap()
                                hashMap.put("seen", true)
                                postSnapshot.ref.updateChildren(hashMap as Map<String, Any>)
                                //chatAdapter.addSeen(message.seen)
                               chatRecyclerView.adapter = chatAdapter
                            }
                        }
                    }
                    chatAdapter.notifyDataSetChanged()
                    chatRecyclerView.scrollToPosition(messageList.size - 1)
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

        //lam ham lay hoat dong cua user tai chatActivity
        mDbRef.child("user").child(friendUid.toString()).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {

                    val user: User? = snapshot.getValue(User::class.java)
                    if (user != null && user.uid == friendUid) {
                        //statusFriend = user.status
                            addStatusFriend(user.status)
                        chatAdapter.notifyDataSetChanged()
                    }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })

       // supportActionBar?.title = name.toString()  + " " + status

        //statusAccount(loginUid as String?)


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
                friendUid as String?,
                seen
            )
            //statusAccount(loginUid as String?)
            chatRecyclerView.scrollToPosition(messageList.size - 1)

            chatAdapter.notifyDataSetChanged()
        }
    }

    override fun onResume() {
        super.onResume()
        hasMore = intent.getBooleanExtra("hasMore", false)

        mDbRef.child("chats").child(roomSender!!).child("messages")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    for (postSnapshot in snapshot.children) {
                        val message = postSnapshot.getValue(Message::class.java)

                        if (message != null && statusFriend == "Online" && hasMore) {
                            if (message.receiveId?.equals(loginUid) == true && message.senderId?.equals(friendUid) == true) {
                                var hashMap: HashMap<String, Boolean> = HashMap()
                                hashMap.put("seen", true)
                                postSnapshot.ref.updateChildren(hashMap as Map<String, Any>)
                                //chatAdapter.addSeen(message.seen)
                                chatRecyclerView.adapter = chatAdapter
                            }
                        }
                    }
                    chatAdapter.notifyDataSetChanged()
                    chatRecyclerView.scrollToPosition(messageList.size - 1)
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })

        mDbRef.child("chats").child(roomReceiver!!).child("messages")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {

                    for (postSnapshot in snapshot.children) {
                        val message = postSnapshot.getValue(Message::class.java)

                        if (message != null && statusFriend == "Online" && hasMore) {
                            if (message.receiveId?.equals(loginUid) == true && message.senderId?.equals(friendUid) == true) {
                                var hashMap: HashMap<String, Boolean> = HashMap()
                                hashMap.put("seen", true)
                                postSnapshot.ref.updateChildren(hashMap as Map<String, Any>)
                                //chatAdapter.addSeen(message.seen)
                                chatRecyclerView.adapter = chatAdapter
                            }
                        }
                    }
                    chatAdapter.notifyDataSetChanged()
                    chatRecyclerView.scrollToPosition(messageList.size - 1)
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }

    private fun addStatusFriend(status: String?) {
        this.statusFriend = status
        supportActionBar?.title = intent.getSerializableExtra("name").toString()  + " " + statusFriend
    }

    private fun sendChatMessage(
        loginUid: String?,
        currentDate: String?,
        friendUid: String?,
        seen: Boolean
    ) {
        val message = messageBox.text.toString()
        val messageObject = Message(message, loginUid, friendUid, currentDate, seen)
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

    override fun onBackPressed() {
        super.onBackPressed()
        hasMore = false
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("hasMore", hasMore)
    }
}