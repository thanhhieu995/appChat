package com.example.chatapp.chat

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.*
import com.example.chatapp.R
import com.example.chatapp.main.MainActivity
import com.example.chatapp.notificationTest.NotificationData
import com.example.chatapp.notificationTest.Notification
import com.example.chatapp.notificationTest.PushNotification
import com.example.chatapp.notificationTest.RetrofitInstance
import com.google.firebase.database.*
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

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

//    var friendUid: String? = ""
//
//    var loginUid: String? = ""

    var nameFriend: String = ""

    lateinit var date: Date

    private var noAvatarMessage: Boolean = false

    var avatarSendUrl: String? = ""
    var avatarReceiveUrl: String? = ""

    var listToken: ArrayList<String> = ArrayList()

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

//        val bundle = intent.extras
//
//        if (bundle != null) {
//            bundle.get("data")
//            Toast.makeText(this, "had data", Toast.LENGTH_LONG).show()
//        }
//        var remoteMessage = intent.getSerializableExtra("remoteMessage")

//        friendUid = intent.getSerializableExtra("uidFriend") as String?
//
//        loginUid = intent.getSerializableExtra("uidLogin") as String?
        val userLoginTemp = intent.extras?.get("userLogin")
        val userFriendTemp = intent.extras?.get("userFriend")

        if (userFriendTemp is JsonObject || userFriendTemp is JsonArray || userFriendTemp is String) {
            userLogin = parseJSON(userLoginTemp as String)
            userFriend = parseJSON(userFriendTemp as String)
        } else {
            userLogin = intent.getSerializableExtra("userLogin") as User

            userFriend = intent.getSerializableExtra("userFriend") as User
        }


        hasMore = intent.getBooleanExtra("hasMore", false)


        mDbRef = FirebaseDatabase.getInstance().reference

        roomReceiver = userFriend.uid.toString() + userLogin.uid
        roomSender =  userLogin.uid.toString() + userFriend.uid

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
                userLogin.uid.toString(),
                currentDate,
                userFriend.uid ,
                seen,
                noAvatarMessage,
                avatarSendUrl.toString(),
                avatarReceiveUrl.toString()
            )



            chatRecyclerView.scrollToPosition(chatAdapter.itemCount - 1)

           // chatAdapter.notifyDataSetChanged()
        }

//        FirebaseMessaging.getInstance().subscribeToTopic("simple test")


//        messageBox.setOnKeyListener(object : View.OnKeyListener{
//            override fun onKey(v: View?, keyCode: Int, event: KeyEvent?): Boolean {
//                if (event != null) {
//                    if (event.action == KeyEvent.KEYCODE_ENTER) {
//                        date = Calendar.getInstance().time
//                        val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
//                        val currentDate = sdf.format(Date())
//
//                        sendChatMessage(
//                            loginUid.toString(),
//                            currentDate,
//                            friendUid as String?,
//                            seen,
//                            noAvatarMessage,
//                            avatarSendUrl.toString(),
//                            avatarReceiveUrl.toString()
//                        )
//                    }
//                    return true
//                }
//                return  false
//            }
//        })
    }

    override fun onResume() {
        super.onResume()
        hasMore = intent.getBooleanExtra("hasMore", false)

//        friendUid = intent.getSerializableExtra("uidFriend") as String?
//
//        loginUid = intent.getSerializableExtra("uidLogin") as String?

        val userLoginTemp = intent.extras?.get("userLogin")
        val userFriendTemp = intent.extras?.get("userFriend")

        if (userFriendTemp is JsonObject || userFriendTemp is JsonArray || userFriendTemp is String) {
            userLogin = parseJSON(userLoginTemp as String)
            userFriend = parseJSON(userFriendTemp as String)
        } else {
            userLogin = intent.getSerializableExtra("userLogin") as User

            userFriend = intent.getSerializableExtra("userFriend") as User
        }

        tmp1 = roomSender
        tmp2 = roomReceiver

        if (hasMore) {

            loadDataRoomSend()
            //loadDataRoomReceive()
            //room()

        }

        statusAndCall()
        //chatAdapter.notifyDataSetChanged()
        chatAdapter.setValueUser(userLogin, userFriend, hasMore)
    }

    private fun addStatusFriend(status: String?) {
        this.statusFriend = status

        supportActionBar?.subtitle = statusFriend

    }

    private fun sendChatMessage(loginUid: String?, currentDate: String?, friendUid: String?, seen: Boolean, noAvatarMessage: Boolean, avatarSendUrl: String, avatarReceiveUrl: String) {
        val message = messageBox.text.toString()
        val messageObject = Message(message, loginUid, friendUid, currentDate, noAvatarMessage, avatarSendUrl, avatarReceiveUrl)
        if (loginUid != null && message.trim().isNotEmpty()) {
            mDbRef.child("chats").child(roomSender!!).child("messages").push()
                .setValue(messageObject).addOnSuccessListener {
                    mDbRef.child("chats").child(roomReceiver!!).child("messages").push()
                        .setValue(messageObject) }
        } else {
            Toast.makeText(this@ChatActivity, "Please enter the character!!!!", Toast.LENGTH_LONG)
                .show()
        }
       // hideOrShowAvatarMess(messageObject)

        val title: String? = userLogin.name

        if (title != null) {
            if (title.isNotEmpty() && message.isNotEmpty()) {
                for (token in listToken) {
                    PushNotification(NotificationData(userLogin, userFriend, hasMore, title, message), Notification(title, message, "View"), token, "high")
                        .also {
                            sendNotification(it)
                        }
                }
            }
        }
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

//    private fun  room() {
//        mDbRef.child("chats").child(roomSender!!).child("messages")
//            .addValueEventListener(
//                mDbRef.child("chats").child(roomReceiver!!).child("messages")
//                    .addValueEventListener(object : ValueEventListener {
//                        override fun onDataChange(snapshot: DataSnapshot) {
//                            val messageList = ArrayList<Message>()
////                    messageList.clear()
////                    newList.clear()
//                            var messageEXReceive: Message? = Message("", "", "", null,
//                                seen = false,
//                                noAvatarMessage = false, "", ""
//                            )
//
//                            var messageSender: Message? = Message("", "", "", "2020-06-06 10:10:10",
//                                seen = false,
//                                noAvatarMessage = false, "", ""
//                            )
//
//                            for (postSnapshot in snapshot.children) {
//
//                                val message = postSnapshot.getValue(Message::class.java)
//
////                        messageList.add(message!!)
//                                if (message != null && hasMore) {
//                                    //message.seen = false
//                                    if ((message.receiveId?.equals(loginUid) == true) && (message.senderId?.equals(
//                                            friendUid
//                                        ) == true)
//                                    ) {
//                                        val hashMap: HashMap<String, Boolean> = HashMap()
//                                        hashMap.put("seen", true)
//                                        postSnapshot.ref.updateChildren(hashMap as Map<String, Any>)
//                                    }
//                                }
//
////                        if (message != null) {
////                            var tmpSeen = false
////                            if (message.receiveId?.let { roomSender!!.startsWith(it, false) } == true) {
////                                tmpSeen = true
////                            }
////                            message.seen = tmpSeen
////                        }
//
//                                if (message != null && message.receiveId == loginUid) {
//                                    val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
//                                    val exMessTime = messageEXReceive?.time?.let { sdf.parse(it) }
//                                    val messageTime = message.time?.let { sdf.parse(it) }
//                                    val messageSendTime = messageSender?.time?.let { sdf.parse(it) }
//
//                                    if (messageTime != null && exMessTime != null && messageSendTime != null && message.time != messageEXReceive?.time) {
//                                        if ((messageTime.time - exMessTime.time)/ 1000 / 60 <= 1 && messageSendTime.before(exMessTime) || messageSendTime.after(messageTime)) {
//                                            var hashMap: HashMap<String, Boolean> = HashMap()
//                                            hashMap.put("noAvatarMessage", true)
//                                            postSnapshot.ref.updateChildren(hashMap as Map<String, Any>)
//                                        }
//                                    }
//                                    messageEXReceive = message
//
//                                } else {
//                                    messageSender = message
//                                }
//
//                                if (message != null) {
//
//                                    // chatAdapter.addMessage(message, loginUid!!, friendUid!!)
//                                    messageList.add(message)
////                            newList.add(message)
//                                }
//                            }
//                            loginUid?.let { friendUid?.let { it1 -> chatAdapter.addUid(it, it1) } }
//                            chatAdapter.updateData(messageList)
////                    chatAdapter.notifyDataSetChanged()
//                            chatRecyclerView.scrollToPosition(chatAdapter.itemCount - 1)
//                        }
//
//                        override fun onCancelled(error: DatabaseError) {
//
//                        }
//
//                    })
//            )
//    }

    private fun loadDataRoomSend() {
        mDbRef.child("chats").child(roomSender!!).child("messages")
            .addValueEventListener(object : ValueEventListener {
                @SuppressLint("SetTextI18n")
                override fun onDataChange(snapshot: DataSnapshot) {

                    val messageList = ArrayList<Message>()
//                    messageList.clear()
//                    newList.clear()
                    var messageEXReceive: Message? = Message("", "", "", "2019-06-06 10:10:10", noAvatarMessage = false ,"", "")

                    var messageSender: Message? = Message("", "", "", "2020-06-06 10:10:10", noAvatarMessage = false, "", "")

                    var messageExSend: Message? = Message("", "", "", "2019-06-06 10:10:10", noAvatarMessage, "", "")

                    var messageReceive: Message? = Message("", "", "", "2020-06-06 10:10:10", noAvatarMessage, "", "")

                    for (postSnapshot in snapshot.children) {

                        val message = postSnapshot.getValue(Message::class.java)

//                        messageList.add(message!!)
                        if (message != null && hasMore) {
                            //message.seen = false
                            if ((message.receiveId?.equals(userLogin.uid) == true) && (message.senderId?.equals(
                                    userFriend.uid
                                ) == true)
                            ) {
                                val hashMap: HashMap<String, Boolean> = HashMap()
                                hashMap.put("seen", true)
                                postSnapshot.ref.updateChildren(hashMap as Map<String, Any>)
                            }
                        }

                        if (message != null && message.senderId == userLogin.uid) {
                            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
                            val exSendTime = messageExSend?.time.let { sdf.parse(it) }
                            val messageTime = message.time.let { sdf.parse(it) }
                            val messageReceiveTime = messageReceive?.time.let { sdf.parse(it) }

                            if (messageTime != null && exSendTime != null && messageReceiveTime != null && message.time != messageEXReceive?.time) {
                                if ((messageTime.time - exSendTime.time)/ 1000 / 60 <= 1 && messageReceiveTime.before(exSendTime) || messageReceiveTime.after(messageTime)) {
                                    var hashMap: HashMap<String, Boolean> = HashMap()
                                    hashMap.put("noAvatarMessage", true)
                                    postSnapshot.ref.updateChildren(hashMap as Map<String, Any>)
                                }
                            }
                            messageExSend = message
                        } else {
                            messageReceive = message
                        }

                        if (message != null && message.receiveId == userLogin.uid) {
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

                            messageList.add(message)

                        }
                    }
                    userLogin.uid?.let { userFriend.uid?.let { it1 -> chatAdapter.addUid(it, it1) } }
                    chatAdapter.updateData(messageList)
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
                    if (postSnapshot.getValue(User::class.java)?.uid == userFriend.uid) {
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

            FirebaseDatabase.getInstance().reference.child("user").child(userLogin.uid.toString()).child("calling").setValue(true)
            //mDbRef.child("user").child(loginUid.toString())

            //mDbRef.ref.updateChildren(hashMap as Map<String, Any>)
            //FirebaseDatabase.getInstance().reference.child("chats")
            Toast.makeText(this, "video call is ready", Toast.LENGTH_SHORT).show()
            val intent = Intent(this@ChatActivity, VideoCallOutgoing::class.java)
            intent.putExtra("uidLogin", userLogin.uid)
            intent.putExtra("uidFriend", userFriend.uid)
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
            intent.putExtra("uidLogin", userLogin.uid)
            intent.putExtra("uidFriend", userFriend.uid)
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


    private fun statusAndCall() {
        mDbRef.child("user").child(userFriend.uid.toString())
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    val user: User? = snapshot.getValue(User::class.java)
                    if ((user != null) && (user.uid == userFriend.uid)) {
                        //statusFriend = user.status
                        addStatusFriend(user.status)
                        listToken.clear()

                        listToken = user.listToken!!
                        //isCalled = user.calling
                        if (user.calling) {
                            val intent = Intent(this@ChatActivity, VideoCallIncoming::class.java)
                            intent.putExtra("loginUid", userLogin.uid)
                            intent.putExtra("friendUid", userFriend.uid)
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

    private val coroutineExceptionHandler = CoroutineExceptionHandler { _, throwable ->
        throwable.printStackTrace()
    }

    private fun sendNotification(notification: PushNotification) = CoroutineScope(Dispatchers.IO + coroutineExceptionHandler).launch {
        try {
            val response = RetrofitInstance.api.postNotification(notification)
//            Log.d("Hieu", "Response: ${Gson().toJson(response)}")
//            val resTest = Gson().toJson(response)
            if (response.isSuccessful) {
                Log.d("Hieu", "Response: ${Gson().toJson(response)}")
            } else {
                Log.e("Hieu", response.errorBody().toString())
            }
        }catch (e: java.lang.Exception) {
            Log.e("Hieu", e.toString())
        }
    }

    fun parseJSON(jsonResponse: String): User {
        return Gson().fromJson(jsonResponse, User::class.java)
    }
}

