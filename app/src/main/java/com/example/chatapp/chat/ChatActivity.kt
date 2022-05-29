package com.example.chatapp.chat

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.Message
import com.example.chatapp.ProfileActivity
import com.example.chatapp.R
import com.example.chatapp.User
import com.example.chatapp.main.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.rpc.context.AttributeContext
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
    lateinit var mAuth: FirebaseAuth

    private lateinit var statusMessage: String


    private var roomSender: String? = null
    private var roomReceiver: String? = null

    private var statusFriend: String? = "1"

    private lateinit var userLogin: User

    private lateinit var userFriend: User


    var seen: Boolean = false

    var hasMore: Boolean = false

    var friendUid: String? = ""

    var loginUid: String? = ""

    var nameFriend: String = ""

    lateinit var date: Date

    var noAvatarMessage: Boolean = false

    var timeExam: Int = -2

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        val name = intent.getSerializableExtra("name")
        friendUid = intent.getSerializableExtra("uidFriend") as String?

        loginUid = intent.getSerializableExtra("uidLogin") as String?

        hasMore = intent.getBooleanExtra("hasMore", false)

        userLogin = intent.getSerializableExtra("userLogin") as User

        userFriend = intent.getSerializableExtra("userFriend") as User

//        val loginUid = FirebaseAuth.getInstance().currentUser?.uid // lay uid tu account???
//        //???????????????

        mDbRef = FirebaseDatabase.getInstance().reference

        roomReceiver = friendUid.toString() + loginUid
        roomSender =  loginUid.toString() + friendUid

        //supportActionBar?.title = name.toString()  + " " + statusFriend

        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        messageBox = findViewById(R.id.messageBox)
        sentButton = findViewById(R.id.img_sent)

        messageList = ArrayList()
        chatAdapter = ChatAdapter(this, messageList)


        chatRecyclerView.layoutManager = LinearLayoutManager(this)


        chatRecyclerView.adapter = chatAdapter

        chatRecyclerView.scrollToPosition(messageList.size - 1)


        date = Calendar.getInstance().time

        //val now = Calendar.getInstance()

        //loadDataRoomSend()
        //loadDataRoomReceive()

        //lam ham lay hoat dong cua user tai chatActivity
        mDbRef.child("user").child(friendUid.toString())
            .addValueEventListener(object : ValueEventListener {
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

        sentButton.setOnClickListener {
            date = Calendar.getInstance().time
            val calender = Calendar.getInstance()
            val year = calender.get(Calendar.YEAR)
            val month = SimpleDateFormat("MM").format(Date()).toInt()

            val sdf = SimpleDateFormat("dd/M/yyyy  hh:mm:ss aaa")
            val currentDate = sdf.format(Date())

            timeExam = -2

            sendChatMessage(
                loginUid.toString(),
                currentDate,
                friendUid as String?,
                seen,
                date,
                month,
                year,
                noAvatarMessage
            )
            chatRecyclerView.scrollToPosition(messageList.size - 1)

            chatAdapter.notifyDataSetChanged()
        }
    }

    override fun onResume() {
        super.onResume()
        hasMore = intent.getBooleanExtra("hasMore", false)

        if (hasMore) {
            loadDataRoomSend()
            timeExam = -2
            loadDataRoomReceive()

        }
    }

    private fun addStatusFriend(status: String?) {
        this.statusFriend = status
        //supportActionBar?.title = intent.getSerializableExtra("name").toString()

        supportActionBar?.subtitle = statusFriend

    }

    private fun sendChatMessage(
        loginUid: String?,
        currentDate: String?,
        friendUid: String?,
        seen: Boolean,
        date: Date,
        month: Int,
        year: Int,
        noAvatarMessage: Boolean
    ) {
        val message = messageBox.text.toString()
        val messageObject = Message(message, loginUid, friendUid, currentDate, seen, date, month, year, noAvatarMessage)
        if (loginUid != null && message.trim().isNotEmpty()) {
            if (friendUid != null) {
                chatAdapter.addMessage(messageObject, loginUid, friendUid)
            }
            mDbRef.child("chats").child(roomSender!!).child("messages").push()
                .setValue(messageObject).addOnSuccessListener {
                    mDbRef.child("chats").child(roomReceiver!!).child("messages").push()
                        .setValue(messageObject) }
        } else {
            Toast.makeText(this@ChatActivity, "Please enter the character!!!!", Toast.LENGTH_LONG)
                .show()
        }
        messageBox.setText("")
        chatRecyclerView.scrollToPosition(messageList.size - 1)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        hasMore = false
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("hasMore", hasMore)
    }

    private fun loadDataRoomSend() {
        mDbRef.child("chats").child(roomSender!!).child("messages")
            .addValueEventListener(object : ValueEventListener {
                @SuppressLint("SetTextI18n")
                override fun onDataChange(snapshot: DataSnapshot) {

                    messageList.clear()

                    for (postSnapshot in snapshot.children) {

                        val message = postSnapshot.getValue(Message::class.java)

//                        messageList.add(message!!)

                        if (message != null && hasMore) {
                            if ((message.receiveId?.equals(loginUid) == true) && (message.senderId?.equals(
                                    friendUid
                                ) == true)
                            ) {
                                var hashMap: HashMap<String, Boolean> = HashMap()
                                hashMap.put("seen", true)
                                postSnapshot.ref.updateChildren(hashMap as Map<String, Any>)
                            }
                        }

                        if (message != null && message.receiveId == loginUid) {
                            if (message.date.minutes - timeExam <= 1) {
                                var hashMap: HashMap<String, Boolean> = HashMap()
                                hashMap.put("noAvatarMessage", true)
                                postSnapshot.ref.updateChildren(hashMap as Map<String, Any>)
                            }
                        }
                        timeExam = message!!.date.minutes
                        chatAdapter.addMessage(message, loginUid!!, friendUid!!)
                        chatRecyclerView.adapter = chatAdapter
                    }
                    chatAdapter.notifyDataSetChanged()
                    chatRecyclerView.scrollToPosition(messageList.size - 1)
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }

    private fun loadDataRoomReceive() {
        mDbRef.child("chats").child(roomReceiver!!).child("messages")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    messageList.clear()

                    for (postSnapshot in snapshot.children) {
                        val message = postSnapshot.getValue(Message::class.java)

                        if (message != null  && hasMore) {
                            if (message.receiveId?.equals(loginUid) == true && message.senderId?.equals(
                                    friendUid
                                ) == true
                            ) {
                                var hashMap: HashMap<String, Boolean> = HashMap()
                                hashMap.put("seen", true)
                                postSnapshot.ref.updateChildren(hashMap as Map<String, Any>)
                            }
                        }
                        if (message != null && message.receiveId == loginUid) {
                            if (message.date.minutes - timeExam <= 1) {
                                var hashMap: HashMap<String, Boolean> = HashMap()
                                hashMap.put("noAvatarMessage", true)
                                postSnapshot.ref.updateChildren(hashMap as Map<String, Any>)
                            }
                        }
                        timeExam = message!!.date.minutes
                        chatAdapter.addMessage(message, loginUid!!, friendUid!!)
                        chatRecyclerView.adapter = chatAdapter
                    }
                    chatAdapter.notifyDataSetChanged()
                    chatRecyclerView.scrollToPosition(messageList.size - 1)
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.chatbar, menu)
        menuInflater.inflate(R.menu.actionbar_title, menu)
        var menuItem = menu?.findItem(R.id.action_name_title)
        mDbRef.child("user").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (postSnapshot in snapshot.children) {
                    if (postSnapshot.getValue(User::class.java)?.uid == friendUid) {
                        if (menuItem != null) {
                            menuItem.title = postSnapshot.getValue(User::class.java)!!.name
                            nameFriend = postSnapshot.getValue(User::class.java)!!.name.toString()
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
//        if (statusFriend == "online") {
//            menuInflater.inflate(R.menu.icon_status, menu)
//        }
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.phone_bar) {
            Toast.makeText(this, "ready phone call", Toast.LENGTH_SHORT).show()
            makePhoneCall()
            return true
        }

        if (item.itemId == R.id.videoCall_bar) {
            Toast.makeText(this, "video call is ready", Toast.LENGTH_SHORT).show()
            return true
        }

        if (item.itemId == R.id.action_name_title) {
            val intent = Intent(this@ChatActivity, ProfileActivity::class.java)
            intent.putExtra("uid", friendUid)
            intent.putExtra("name", nameFriend)
            startActivity(intent)
        }
        return false
    }


    private fun makePhoneCall() : Boolean {
        return try {
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("tel: ")
            startActivity(intent)
            true
        } catch (e: Exception) {
            e.printStackTrace()
            false
        }
    }
}

