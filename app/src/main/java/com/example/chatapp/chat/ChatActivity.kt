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
import com.example.chatapp.*
import com.example.chatapp.R
import com.example.chatapp.main.MainActivity
import com.google.firebase.database.*
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.time.Duration.Companion.minutes
import kotlin.time.Duration.Companion.seconds


class ChatActivity : AppCompatActivity() {

    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageBox: EditText
    private lateinit var sentButton: ImageView
    private lateinit var chatAdapter: ChatAdapter
    private lateinit var mDbRef: DatabaseReference

    private var roomSender: String? = null
    private var roomReceiver: String? = null

    var tmp1: String? = null
    var tmp2: String? = null

    private var statusFriend: String? = "1"

    private lateinit var userLogin: User

    private lateinit var userFriend: User

    private var seen: Boolean = false

    var hasMore: Boolean = false

    var friendUid: String? = ""

    var loginUid: String? = ""

    var nameFriend: String = ""

    lateinit var date: Date

    private var noAvatarMessage: Boolean = false

    var avatarSendUrl: String? = ""
    var avatarReceiveUrl: String? = ""

//    var messageEXReceive: Message? = Message("", "", "", null,
//        seen = false,
//        noAvatarMessage = false, "", ""
//    )
//
//    var messageSender: Message? = Message("", "", "", "2020-06-06 10:10:10",
//        seen = false,
//        noAvatarMessage = false, "", ""
//    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        friendUid = intent.getSerializableExtra("uidFriend") as String?

        loginUid = intent.getSerializableExtra("uidLogin") as String?

        hasMore = intent.getBooleanExtra("hasMore", false)

        userLogin = intent.getSerializableExtra("userLogin") as User

        userFriend = intent.getSerializableExtra("userFriend") as User


        mDbRef = FirebaseDatabase.getInstance().reference

        roomReceiver = friendUid.toString() + loginUid
        roomSender =  loginUid.toString() + friendUid

        //supportActionBar?.title = name.toString()  + " " + statusFriend

        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        messageBox = findViewById(R.id.messageBox)
        sentButton = findViewById(R.id.img_sent)

        chatAdapter = ChatAdapter(this)


        chatRecyclerView.layoutManager = LinearLayoutManager(this)


        chatRecyclerView.adapter = chatAdapter

        chatRecyclerView.scrollToPosition(chatAdapter.itemCount - 1)


        date = Calendar.getInstance().time

        //val now = Calendar.getInstance()

        //loadDataRoomSend()
        //loadDataRoomReceive()

        //getLegacyDateDifference("2022-06-29 08:02:11", "2022-06-29 08:05:11")


        //lam ham lay hoat dong cua user tai chatActivity

        sentButton.setOnClickListener {
            date = Calendar.getInstance().time
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val currentDate = sdf.format(Date())

            sendChatMessage(
                loginUid.toString(),
                currentDate,
                friendUid as String?,
                seen,
                noAvatarMessage,
                avatarSendUrl.toString(),
                avatarReceiveUrl.toString()
            )

            chatRecyclerView.scrollToPosition(chatAdapter.itemCount - 1)

           // chatAdapter.notifyDataSetChanged()
        }
    }

    override fun onResume() {
        super.onResume()
        hasMore = intent.getBooleanExtra("hasMore", false)

        friendUid = intent.getSerializableExtra("uidFriend") as String?

        loginUid = intent.getSerializableExtra("uidLogin") as String?

        hasMore = intent.getBooleanExtra("hasMore", false)

        userLogin = intent.getSerializableExtra("userLogin") as User

        userFriend = intent.getSerializableExtra("userFriend") as User

        tmp1 = roomSender
        tmp2 = roomReceiver

        if (hasMore) {
//            if (loginUid?.let { roomSender?.startsWith(it, false) } == true) {
//                loadDataRoomSend()
//            }
//
//            loadDataRoomReceive()
            loadDataRoomSend()
            //loadDataRoomReceive()

//            val tmp1 : String = roomSender.toString()
//            val tmp2: String = roomReceiver.toString()

//            if (loginUid?.let { tmp1.startsWith(it, false) } == true) {
//                loadDataRoomSend()
//            }

//            if (loginUid?.let { tmp2.startsWith(it, false) } == true) {
//                loadDataRoomReceive()
//            }
        }

        statusAndCall()
        //chatAdapter.notifyDataSetChanged()
        chatAdapter.setValueUser(userLogin, userFriend, hasMore)
    }

    private fun addStatusFriend(status: String?) {
        this.statusFriend = status

        supportActionBar?.subtitle = statusFriend

    }

    private fun sendChatMessage(
        loginUid: String?,
        currentDate: String?,
        friendUid: String?,
        seen: Boolean,
        noAvatarMessage: Boolean,
        avatarSendUrl: String,
        avatarReceiveUrl: String
    ) {
        val message = messageBox.text.toString()
        val messageObject = Message(message, loginUid, friendUid, currentDate, seen, noAvatarMessage, avatarSendUrl, avatarReceiveUrl)
        if (loginUid != null && message.trim().isNotEmpty()) {
            if (friendUid != null) {
                //chatAdapter.addMessage(messageObject, loginUid, friendUid)
                //newList = messageList
//                newList.add(messageObject)
                chatAdapter.addUid(loginUid, friendUid)
            }
            mDbRef.child("chats").child(roomSender!!).child("messages").push()
                .setValue(messageObject).addOnSuccessListener {
                    mDbRef.child("chats").child(roomReceiver!!).child("messages").push()
                        .setValue(messageObject) }
        } else {
            Toast.makeText(this@ChatActivity, "Please enter the character!!!!", Toast.LENGTH_LONG)
                .show()
        }

       // hideOrShowAvatarMess(messageObject)

//        chatAdapter.updateData(newList)
        messageBox.setText("")
        chatRecyclerView.scrollToPosition(chatAdapter.itemCount - 1)
    }

    override fun onBackPressed() {
        super.onBackPressed()
        hasMore = false
        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("hasMore", hasMore)
        startActivity(intent)
    }

    private fun loadDataRoomSend() {
        mDbRef.child("chats").child(roomSender!!).child("messages")
            .addValueEventListener(object : ValueEventListener {
                @SuppressLint("SetTextI18n")
                override fun onDataChange(snapshot: DataSnapshot) {

                    val messageList = ArrayList<Message>()
//                    messageList.clear()
//                    newList.clear()
                    var messageEXReceive: Message? = Message("", "", "", null,
                        seen = false,
                        noAvatarMessage = false, "", ""
                    )

                    var messageSender: Message? = Message("", "", "", "2020-06-06 10:10:10",
                        seen = false,
                        noAvatarMessage = false, "", ""
                    )

                    for (postSnapshot in snapshot.children) {

                        val message = postSnapshot.getValue(Message::class.java)

//                        messageList.add(message!!)
                        if (message != null && hasMore) {
                            //message.seen = false
                            if ((message.receiveId?.equals(loginUid) == true) && (message.senderId?.equals(
                                    friendUid
                                ) == true)
                            ) {
                                val hashMap: HashMap<String, Boolean> = HashMap()
                                hashMap.put("seen", true)
                                postSnapshot.ref.updateChildren(hashMap as Map<String, Any>)
                            }
                        }

//                        if (message != null) {
//                            var tmpSeen = false
//                            if (message.receiveId?.let { roomSender!!.startsWith(it, false) } == true) {
//                                tmpSeen = true
//                            }
//                            message.seen = tmpSeen
//                        }

                        if (message != null && message.receiveId == loginUid) {
                            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                            val exMessTime = messageEXReceive?.time?.let { sdf.parse(it) }
                            val messageTime = message.time?.let { sdf.parse(it) }
                            val messageSendTime = messageSender?.time?.let { sdf.parse(it) }

                            if (messageTime != null && exMessTime != null && messageSendTime != null && message.time != messageEXReceive?.time) {
                                if ((messageTime.time - exMessTime.time)/ 1000 / 60 <= 1 && messageSendTime.before(exMessTime) || messageSendTime.after(messageTime)) {
                                    var hashMap: HashMap<String, Boolean> = HashMap()
                                    hashMap.put("noAvatarMessage", true)
                                    postSnapshot.ref.updateChildren(hashMap as Map<String, Any>)
                                }
                            }
                            messageEXReceive = message

                        } else {
                            messageSender = message
                        }

                        if (message != null) {

                            // chatAdapter.addMessage(message, loginUid!!, friendUid!!)
                            messageList.add(message)
//                            newList.add(message)
                        }
                    }
                    loginUid?.let { friendUid?.let { it1 -> chatAdapter.addUid(it, it1) } }
                    chatAdapter.updateData(messageList)
//                    chatAdapter.notifyDataSetChanged()
                    chatRecyclerView.scrollToPosition(chatAdapter.itemCount - 1)
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }

    private fun loadDataRoomReceive() {

        mDbRef.child("chats").child(roomReceiver!!).child("messages")
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    val messageList = ArrayList<Message>()
//                    messageList.clear()
//                    newList.clear()
                    var messageEXReceive: Message? = Message("", "", "", null,
                        seen = false,
                        noAvatarMessage = false, "", ""
                    )

                    var messageSender: Message? = Message("", "", "", "2020-06-06 10:10:10",
                        seen = false,
                        noAvatarMessage = false, "", ""
                    )

                    for (postSnapshot in snapshot.children) {

                        val message = postSnapshot.getValue(Message::class.java)

//                        messageList.add(message!!)
                        if (message != null && hasMore) {
                            //message.seen = false
                            if ((message.receiveId?.equals(loginUid) == true) && (message.senderId?.equals(
                                    friendUid
                                ) == true)
                            ) {
                                val hashMap: HashMap<String, Boolean> = HashMap()
                                hashMap.put("seen", true)
                                postSnapshot.ref.updateChildren(hashMap as Map<String, Any>)
                            }
                        }

                        if (message != null && message.receiveId == loginUid) {
                            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                            val exMessTime = messageEXReceive?.time?.let { sdf.parse(it) }
                            val messageTime = message.time?.let { sdf.parse(it) }
                            val messageSendTime = messageSender?.time?.let { sdf.parse(it) }

                            if (messageTime != null && exMessTime != null && messageSendTime != null && message.time != messageEXReceive?.time) {
                                if ((messageTime.time - exMessTime.time)/ 1000 / 60 <= 1 && messageSendTime.before(exMessTime) || messageSendTime.after(messageTime)) {
                                    var hashMap: HashMap<String, Boolean> = HashMap()
                                    hashMap.put("noAvatarMessage", true)
                                    postSnapshot.ref.updateChildren(hashMap as Map<String, Any>)
                                }
                            }
                            messageEXReceive = message

                        } else {
                            messageSender = message
                        }

                        if (message != null) {

                            // chatAdapter.addMessage(message, loginUid!!, friendUid!!)
                            messageList.add(message)
//                            newList.add(message)
                        }
                    }
                    loginUid?.let { friendUid?.let { it1 -> chatAdapter.addUid(it, it1) } }
                    chatAdapter.updateData(messageList)
//                    chatAdapter.notifyDataSetChanged()
                    chatRecyclerView.scrollToPosition(chatAdapter.itemCount - 1)
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
        return super.onCreateOptionsMenu(menu)
    }


    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.phone_bar) {
            Toast.makeText(this, "ready phone call", Toast.LENGTH_SHORT).show()
            makePhoneCall()
            return true
        }

        if (item.itemId == R.id.videoCall_bar) {
            //isCalled = true

            FirebaseDatabase.getInstance().reference.child("user").child(loginUid.toString()).child("calling").setValue(true)
            //mDbRef.child("user").child(loginUid.toString())

            //mDbRef.ref.updateChildren(hashMap as Map<String, Any>)
            //FirebaseDatabase.getInstance().reference.child("chats")
            Toast.makeText(this, "video call is ready", Toast.LENGTH_SHORT).show()
            val intent = Intent(this@ChatActivity, VideoCallOutgoing::class.java)
            intent.putExtra("uidLogin", loginUid)
            intent.putExtra("uidFriend", friendUid)
            intent.putExtra("hasMore", hasMore)
            intent.putExtra("userLogin", userLogin)
            intent.putExtra("userFriend", userFriend)
            startActivity(intent)
            return true
        }

        if (item.itemId == R.id.action_name_title) {
            val intent = Intent(this@ChatActivity, ProfileActivity::class.java)
//            intent.putExtra("uid", friendUid)
//            intent.putExtra("name", nameFriend)
            intent.putExtra("uidLogin", loginUid)
            intent.putExtra("uidFriend", friendUid)
            intent.putExtra("hasMore", hasMore)
            intent.putExtra("userLogin", userLogin)
            intent.putExtra("userFriend", userFriend)
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

//    private fun inComingCall() {
//        if (isCalled) {
//            val intent = Intent(this@ChatActivity, VideoCallIncoming::class.java)
//            startActivity(intent)
//        }
//    }

    private fun statusAndCall() {
        mDbRef.child("user").child(friendUid.toString())
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    val user: User? = snapshot.getValue(User::class.java)
                    if (user != null && user.uid == friendUid) {
                        //statusFriend = user.status
                        addStatusFriend(user.status)
                        //isCalled = user.calling
                        if (user.calling) {
                            val intent = Intent(this@ChatActivity, VideoCallIncoming::class.java)
                            intent.putExtra("loginUid", loginUid)
                            intent.putExtra("friendUid", friendUid)
                            intent.putExtra("hasMore", hasMore)
                            intent.putExtra("userLogin", userLogin)
                            intent.putExtra("userFriend", userFriend)
                            startActivity(intent)
                        }

//                        chatAdapter.notifyDataSetChanged()
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
    }

//    private fun hideOrShowAvatarMess(message: Message) {
//        val sdf = SimpleDateFormat("")
//        val exMessTime = sdf.parse(messageEXReceive?.time.toString())
//        val messageTime = sdf.parse(message.time.toString())
//        val messageSendTime = sdf.parse(messageSender?.time.toString())
//
//        if (messageTime != null && exMessTime != null && messageSendTime != null) {
//            if ((messageTime.time - exMessTime.time)/ 1000 / 60 <= 1 && messageSendTime.before(exMessTime) || messageSendTime.after(messageTime)) {
//                message.noAvatarMessage = true
//            }
//        }
//    }

    fun getLegacyDateDifference(fromDate: String, toDate: String, formatter: String= "yyyy-MM-dd HH:mm:ss" , locale: Locale = Locale.getDefault()): Map<String, Long> {

        val fmt = SimpleDateFormat(formatter, locale)
        val bgn = fmt.parse(fromDate)
        val end = fmt.parse(toDate)

        val milliseconds = end!!.time - bgn!!.time
        val days = milliseconds / 1000 / 3600 / 24
        val hours = milliseconds / 1000 / 3600
        val minutes = milliseconds / 1000 / 60
        val seconds = milliseconds / 1000
        val weeks = days.div(7)

        return mapOf("days" to days, "hours" to hours, "minutes" to minutes, "seconds" to seconds, "weeks" to weeks)
    }
}

