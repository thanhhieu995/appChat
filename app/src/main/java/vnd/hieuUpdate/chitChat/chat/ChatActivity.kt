package vnd.hieuUpdate.chitChat.chat

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.view.View.OnClickListener
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.widget.addTextChangedListener
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.ktx.storage
import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import vnd.hieuUpdate.chitChat.Message
import vnd.hieuUpdate.chitChat.User
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.HashMap
import vnd.hieuUpdate.chitChat.Unread
import vnd.hieuUpdate.chitChat.main.MainActivity
import vnd.hieuUpdate.chitChat.notificationTest.NotificationData
import vnd.hieuUpdate.chitChat.notificationTest.PushNotification
import vnd.hieuUpdate.chitChat.notificationTest.RetrofitInstance
import vnd.hieuUpdate.chitChat.R


class ChatActivity : AppCompatActivity() {

    private lateinit var chatRecyclerView: RecyclerView
    private lateinit var messageBox: EditText
    private lateinit var sentButton: ImageView

     lateinit var sendImage: ImageView

    private lateinit var chatAdapter: ChatAdapter
    private lateinit var mDbRef: DatabaseReference
    lateinit var firebaseStorage: FirebaseStorage

    lateinit var textTyping: TextView

    private var roomSender: String? = null
    private var roomReceiver: String? = null

    var tmp1: String? = null
    var tmp2: String? = null

    private var statusFriend: String? = "1"

    private lateinit var userLogin: User

    private lateinit var userFriend: User

    private var seen: Boolean = false

    var hasMore: Boolean = false

    var nameFriend: String = ""

    lateinit var date: Date

    private var noAvatarMessage: Boolean = false

    var avatarSendUrl: String? = ""
    var avatarReceiveUrl: String? = ""

    var listToken: ArrayList<String> = ArrayList()

    lateinit var mAuth: FirebaseAuth

    var count: Int = 0
    var fromUid: String = ""

    var lastMsg: String? = null
    var room: String? = null

    var senderUid: String? = null
    var receiveUid: String? = null

    var imageUriTemp: Uri? = null
    var hadImage: Boolean = false

    var isActive: Boolean = false

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        mAuth = FirebaseAuth.getInstance()

        val hasMoreTemp = intent.extras?.get("hasMore")
        hasMore = if (hasMoreTemp is String) {
            hasMoreTemp.toBoolean()
        } else {
            intent.getBooleanExtra("hasMore", false)
        }

        val userLoginTemp = intent.extras?.get("userLogin")
        val userFriendTemp = intent.extras?.get("userFriend")

        if (userFriendTemp is JsonObject || userFriendTemp is JsonArray || userFriendTemp is String) {
            userLogin = parseJSON(userFriendTemp as String)
            userFriend = parseJSON(userLoginTemp as String)
        } else {
            userLogin = intent.getSerializableExtra("userLogin") as User

            userFriend = intent.getSerializableExtra("userFriend") as User
        }


        mDbRef = FirebaseDatabase.getInstance().reference

        roomReceiver = userFriend.uid.toString() + userLogin.uid
        roomSender =  userLogin.uid.toString() + userFriend.uid


        chatRecyclerView = findViewById(R.id.chatRecyclerView)
        messageBox = findViewById(R.id.messageBox)

        textTyping = findViewById(R.id.textTyping)

        sentButton = findViewById(R.id.img_sent_chat)

        sendImage = findViewById(R.id.img_attach)

        chatAdapter = ChatAdapter(this)


        chatRecyclerView.layoutManager = LinearLayoutManager(this)


        chatRecyclerView.adapter = chatAdapter

        chatRecyclerView.scrollToPosition(chatAdapter.itemCount - 1)


        date = Calendar.getInstance().time

        sendImage.setOnClickListener(object : OnClickListener{
            override fun onClick(p0: View?) {
                val intent = Intent()
                intent.type = "image/*"
                intent.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(Intent.createChooser(intent, "Select picture"), 1)
            }
        })

//       val pic = getDrawable(R.drawable.ic_baseline_image_24)
//        if (sendImage.equals(pic)) {
//            Toast.makeText(this@ChatActivity, "same", Toast.LENGTH_LONG).show()
//        } else {
//            Toast.makeText(this@ChatActivity, "different", Toast.LENGTH_LONG).show()
//        }


        sentButton.setOnClickListener {
            date = Calendar.getInstance().time
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
            val currentDate = sdf.format(Date())
            val message = messageBox.text.toString().trim()

//            pushImageToStorage(currentDate)

//            sendChatMessage(userLogin.uid.toString(), currentDate, userFriend.uid , seen, noAvatarMessage, avatarSendUrl.toString(), avatarReceiveUrl.toString(), hadImage)

            if (!messageBox.text.isNullOrEmpty()) {
                if (imageUriTemp != null) {
                    pushImageToStorage(currentDate, message)
                } else {
                    sendChatMessage(message,userLogin.uid.toString(), currentDate, userFriend.uid , seen, noAvatarMessage, avatarSendUrl.toString(), avatarReceiveUrl.toString(), hadImage)
                }
            } else {
                if (imageUriTemp != null){
                    pushImageToStorage(currentDate, message)
                } else {
                    Toast.makeText(this@ChatActivity, "Please, enter message...", Toast.LENGTH_LONG).show()
                }
            }

            chatRecyclerView.scrollToPosition(chatAdapter.itemCount - 1)

        }

//        checkTyping(messageBox)
    }

    private fun pushImageToStorage(currentDate: String, message: String) {

            val storeRef = Firebase.storage.reference.child("chats").child(roomSender.toString())
                .child(currentDate)
            val uploadTask = storeRef.putFile(imageUriTemp!!)

            uploadTask.addOnSuccessListener {
                Toast.makeText(this@ChatActivity, "Image sent", Toast.LENGTH_LONG).show()

                val messageOb  = Message(
                    message,
                    userLogin.uid,
                    userFriend.uid,
                    currentDate,
                    noAvatarMessage,
                    seen,
                    avatarSendUrl,
                    avatarReceiveUrl,
                    true
                )
                mDbRef.child("chats").child(roomSender!!).child("messages").push()
                    .setValue(messageOb).addOnSuccessListener {
                        mDbRef.child("chats").child(roomReceiver!!).child("messages").push()
                            .setValue(messageOb) }.addOnSuccessListener {
                        messageBox.setText("")
                        imageUriTemp = null
                    }
//                    sendImage.setImageDrawable(getDrawable(R.drawable.ic_baseline_image_24))
                sendImage.setImageResource(R.drawable.ic_baseline_image_24)
            } .addOnFailureListener{
                Toast.makeText(this@ChatActivity, it.localizedMessage, Toast.LENGTH_LONG).show()
            }

        val title: String? = userLogin.name

        if (!title.isNullOrEmpty() && message.isNotEmpty()) {
            for (token in userFriend.listToken!!) {
                PushNotification(NotificationData(userLogin, userFriend, hasMore, title,
                    "$message... image"
                ), token, "high")
                    .also {
                        sendNotification(it)
                    }
            }
        }

        if (!title.isNullOrEmpty() && message.isEmpty()) {
            for (token in userFriend.listToken!!) {
                PushNotification(NotificationData(userLogin, userFriend, hasMore, title, "send you image"), token, "high")
                    .also {
                        sendNotification(it)
                    }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        isActive = true

        val hasMoreTemp = intent.extras?.get("hasMore")
        hasMore = if (hasMoreTemp is String) {
            hasMoreTemp.toBoolean()
        } else {
            intent.getBooleanExtra("hasMore", false)
        }
        val userLoginTemp = intent.extras?.get("userLogin")
        val userFriendTemp = intent.extras?.get("userFriend")

        if (userFriendTemp is JsonObject || userFriendTemp is JsonArray || userFriendTemp is String) {
            userLogin = parseJSON(userFriendTemp as String)
            userFriend = parseJSON(userLoginTemp as String)
        } else {
            userLogin = intent.getSerializableExtra("userLogin") as User

            userFriend = intent.getSerializableExtra("userFriend") as User
        }

        tmp1 = roomSender
        tmp2 = roomReceiver

        if (hasMore) {

            loadDataRoomSend()

            isSeen()

        } else {
            Toast.makeText(this@ChatActivity, "hasMore is false, please check", Toast.LENGTH_LONG).show()
        }

        userChangeTyping()

        statusAndCall()
        statusAccount(userLogin.uid)
        chatAdapter.setValueUser(userLogin, userFriend, hasMore)

//        isSeen()
    }

    override fun onPause() {
        super.onPause()
        isActive = false
    }

    private fun addStatusFriend(status: String?) {
        this.statusFriend = status

//        supportActionBar?.title = userLogin.name
        supportActionBar?.subtitle = statusFriend

    }

    private fun sendChatMessage(message: String,loginUid: String?, currentDate: String?, friendUid: String?, seen: Boolean, noAvatarMessage: Boolean, avatarSendUrl: String, avatarReceiveUrl: String, hadImage: Boolean) {
//        showTyping(messageBox)
        val messageObject = Message(
            message,
            loginUid,
            friendUid,
            currentDate,
            noAvatarMessage,
            seen,
            avatarSendUrl,
            avatarReceiveUrl,
            hadImage
        )
        if (loginUid != null && message.trim().isNotEmpty()) {
            mDbRef.child("chats").child(roomSender!!).child("messages").push()
                .setValue(messageObject).addOnSuccessListener {
                    mDbRef.child("chats").child(roomReceiver!!).child("messages").push()
                        .setValue(messageObject) }
        }
//        else {
//            Toast.makeText(this@ChatActivity, "Please enter the character!!!!", Toast.LENGTH_LONG).show()
//        }

        val title: String? = userLogin.name

        if (title != null) {
            if (title.isNotEmpty() && message.isNotEmpty()) {
                for (token in userFriend.listToken!!) {
                    PushNotification(NotificationData(userLogin, userFriend, hasMore, title, message), token, "high")
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

        val hashMap: HashMap<String, Boolean> = HashMap()
        val hashMap1 : HashMap<String, Boolean> = HashMap()
        hashMap.put("typing", false)
        hashMap1.put("showTyping", false)
        mDbRef.child("user").child(userLogin.uid.toString()).updateChildren(hashMap as Map<String, Any>)
        mDbRef.child("user").child(userFriend.uid.toString()).updateChildren(hashMap1 as Map<String, Any>)

        val intent = Intent(this, MainActivity::class.java)
        intent.putExtra("hasMore", hasMore)
        startActivity(intent)
    }


    private fun loadDataRoomSend() {

        var messageTemp: vnd.hieuUpdate.chitChat.Message = vnd.hieuUpdate.chitChat.Message()

        mDbRef.child("chats").child(roomSender!!).child("messages")
            .addValueEventListener(object : ValueEventListener {
                @SuppressLint("SetTextI18n")
                override fun onDataChange(snapshot: DataSnapshot) {

                    count = 0

                    val messageList = ArrayList<vnd.hieuUpdate.chitChat.Message>()

                    var messageEXReceive: vnd.hieuUpdate.chitChat.Message? = vnd.hieuUpdate.chitChat.Message(
                        "",
                        "",
                        "",
                        "2019-06-06 10:10:10",
                        noAvatarMessage = false,
                        false,
                        "",
                        ""
                    )

                    var messageSender: vnd.hieuUpdate.chitChat.Message? = vnd.hieuUpdate.chitChat.Message(
                        "",
                        "",
                        "",
                        "2020-06-06 10:10:10",
                        noAvatarMessage = false,
                        false,
                        "",
                        ""
                    )

                    var messageExSend: vnd.hieuUpdate.chitChat.Message? = vnd.hieuUpdate.chitChat.Message(
                        "",
                        "",
                        "",
                        "2019-06-06 10:10:10",
                        noAvatarMessage,
                        false,
                        "",
                        ""
                    )

                    var messageReceive: vnd.hieuUpdate.chitChat.Message? = vnd.hieuUpdate.chitChat.Message(
                        "",
                        "",
                        "",
                        "2020-06-06 10:10:10",
                        noAvatarMessage,
                        false,
                        "",
                        ""
                    )

                    for (postSnapshot in snapshot.children) {

                        val message = postSnapshot.getValue(vnd.hieuUpdate.chitChat.Message::class.java)

//                        if (message != null && hasMore && message.receiveId == userLogin.uid && !seen) {
////                            if (!seen && message.senderId == userFriend.uid) {
//                                val hashMap: HashMap<String, Boolean> = HashMap()
//                                hashMap.put("seen", true)
//                                postSnapshot.ref.updateChildren(hashMap as Map<String, Any>)
////                            }
//                        }

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

                        if (message != null) {
                            if (!message.seen && message.senderId == userLogin.uid) {
                                count += 1
//                                receiveUid = message.senderId.toString()
                            }
                        }

                        if (message != null) {
                            lastMsg = message.message

                            senderUid = message.senderId
                            receiveUid = message.receiveId
//                            messageTemp = message
                            room = roomSender
                        }
                    }

                    if (lastMsg != null && senderUid != null && receiveUid != null) {
                        val hashMap: HashMap<String, String> = HashMap()
                        hashMap.put("lastMsg", lastMsg!!)
                        val hashMap1: HashMap<String, String> = HashMap()
                        hashMap1.put("sendToUid", senderUid.toString())

                        val hashMap2: HashMap<String, String> = HashMap()
                        hashMap2.put("receiveUid", receiveUid.toString())

                        mDbRef.child("user").child(userLogin.uid.toString()).updateChildren(hashMap as Map<String, Any>)
                        mDbRef.child("user").child(userLogin.uid.toString()).updateChildren(hashMap1 as Map<String, Any>)
                        mDbRef.child("user").child(userLogin.uid.toString()).updateChildren(hashMap2 as Map<String, Any>)
                    }

                    if (count != 0 ) {
                        val unread = Unread(count, userLogin.uid.toString(), userFriend.uid.toString())
                        val hashMap: HashMap<String, Unread> = HashMap()
                        hashMap.put(userLogin.uid.toString(), unread)
                        mDbRef.child("unRead").child(userFriend.uid.toString()).updateChildren(hashMap as Map<String, Any>)
                    } else {
                        val unread = Unread(count, "", "")
                        val hashMap: HashMap<String, Unread> = HashMap()
                        hashMap.put(userLogin.uid.toString(), unread)
                        mDbRef.child("unRead").child(userFriend.uid.toString()).updateChildren(hashMap as Map<String, Any>)
                    }

//                    if (count != 0) {
//                        val hashMap: HashMap<String, Int> = HashMap()
//                        val hashMap1: HashMap<String, String> = HashMap()
//                        hashMap.put("unRead", count)
//                        hashMap1.put("toUid", userFriend.uid.toString())
////                        mDbRef.child("user").child(userFriend.uid.toString()).updateChildren(hashMap as Map<String, Any>)
////                        mDbRef.child("user").child(userFriend.uid.toString()).updateChildren(hashMap1 as Map<String, Any>)
//                        mDbRef.child("unRead").child(userLogin.uid.toString()).child(userFriend.uid.toString()).updateChildren(hashMap as Map<String, Any>)
//                        mDbRef.child("unRead").child(userLogin.uid.toString()).child(userFriend.uid.toString()).updateChildren(hashMap1 as Map<String, Any>)
//                    } else {
//                        val hashMap: HashMap<String, Int> = HashMap()
//                        val hashMap1: HashMap<String, String> = HashMap()
//                        hashMap.put("unRead", count)
//                        hashMap1.put("toUid", userFriend.uid.toString())
//                        mDbRef.child("unRead").child(userLogin.uid.toString()).child(userFriend.uid.toString()).updateChildren(hashMap as Map<String, Any>)
//                        mDbRef.child("unRead").child(userLogin.uid.toString()).child(userFriend.uid.toString()).updateChildren(hashMap1 as Map<String, Any>)
//                    }

                    userLogin.uid?.let { userFriend.uid?.let { it1 -> chatAdapter.addUid(it, it1) } }
                    chatAdapter.updateData(messageList)
                    chatRecyclerView.scrollToPosition(chatAdapter.itemCount - 1)

//                    checkTyping(messageBox)
//                    if (userFriend.isTyping) {
//                        textTyping.text = "typing...."
//                    } else {
//                        textTyping.text = ""
//                    }

//                    if (userLogin.isTyping) {
//                        textTyping.text = "Typing........."
//                    } else {
//                        textTyping.text = ""
//                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }

            })
//        isSeen()
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

//            FirebaseDatabase.getInstance().reference.child("user").child(userLogin.uid.toString()).child("calling").setValue(true)
//            //mDbRef.child("user").child(loginUid.toString())
//
//            //mDbRef.ref.updateChildren(hashMap as Map<String, Any>)
//            //FirebaseDatabase.getInstance().reference.child("chats")
//            Toast.makeText(this, "video call is ready", Toast.LENGTH_SHORT).show()
//            val intent = Intent(this@ChatActivity, VideoCallOutgoing::class.java)
//            intent.putExtra("hasMore", hasMore)
//            intent.putExtra("userLogin", userLogin)
//            intent.putExtra("userFriend", userFriend)
//            startActivity(intent)
//            finish()
//            return true
            Toast.makeText(this, "This feature is not support right now", Toast.LENGTH_LONG).show()
        }

        if (item.itemId == R.id.action_name_title) {
            val intent = Intent(this@ChatActivity, vnd.hieuUpdate.chitChat.ProfileActivity::class.java)
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
//        showTyping(messageBox)
        mDbRef.child("user").child(userFriend.uid.toString())
            .addValueEventListener(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {

                    val user: User? = snapshot.getValue(User::class.java)
                    if ((user != null) && (user.uid == userFriend.uid)) {
                        addStatusFriend(user.status)
//                        listToken.clear()
//                        listToken = user.listToken!!
//                        if (user.calling) {
//                            val intent = Intent(this@ChatActivity, VideoCallIncoming::class.java)
//                            intent.putExtra("hasMore", hasMore)
//                            intent.putExtra("userLogin", userLogin)
//                            intent.putExtra("userFriend", userFriend)
//                            startActivity(intent)
//                            finish()
//                        }
//                        if (user.isTyping && userLogin.showTyping) {
//                            textTyping.text = userFriend.name +  " is typing..."
//                        } else {
//                            textTyping.text = ""
//                        }

                    }
//                    showTyping(messageBox)
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
                Toast.makeText(this@ChatActivity, "Check your internet connection!!!", Toast.LENGTH_LONG).show()
            }
        }catch (e: java.lang.Exception) {
            Log.e("Hieu", e.toString())
        }
    }

    private fun parseJSON(jsonResponse: String): User {
        return Gson().fromJson(jsonResponse, User::class.java)
    }

    private fun statusAccount(Uid: String? ) {
        val studentRef = FirebaseDatabase.getInstance().getReference("user").child(Uid!!)
        val connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected")

        connectedRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val connected = snapshot.getValue(Boolean::class.java)!!
                if (connected) {
                    studentRef.child("status").onDisconnect().setValue("offline")
                    studentRef.child("status").setValue("online")
                }else {
                    studentRef.child("status").setValue("offline")
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }

    private fun isSeen() {
        mDbRef.child("chats").child(roomReceiver!!).child("messages")
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    for (postSnapshot in snapshot.children) {
                        val message = postSnapshot.getValue(Message::class.java)
                        if (message != null && !message.seen && hasMore && message.receiveId == userLogin.uid && message.senderId == userFriend.uid && userFriend.status == "online" && isActive) {
                            val hashMap: HashMap<String, Boolean> = HashMap()
                            hashMap.put("seen", true)
                            postSnapshot.ref.updateChildren(hashMap as Map<String, Any>)
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@ChatActivity, "fail isSeen message!!!", Toast.LENGTH_LONG).show()
                }
            })
    }

    fun checkTyping(messageBox: EditText) {
        messageBox.addTextChangedListener {
            val hashMap: HashMap<String, Boolean> = HashMap()
            if (it.toString().trim().isNotEmpty()) {
                hashMap.put("isTyping", true)
//                mDbRef.child("user").child(userLogin.uid.toString()).updateChildren(hashMap as Map<String, Any>)
                mDbRef.child("user").child(userFriend.uid.toString()).updateChildren(hashMap as Map<String, Any>)
            } else {
                hashMap.put("isTyping", false)
                mDbRef.child("user").child(userFriend.uid.toString()).updateChildren(hashMap as Map<String, Any>)
            }
        }
    }

    private fun showTyping(messageBox: EditText) {

        messageBox.addTextChangedListener {
            val hashMap: HashMap<String, Boolean> = HashMap()
            val hashMap1: HashMap<String, Boolean> = HashMap()
            if (it.toString().trim().isNotEmpty() && hasMore) {
                hashMap.put("typing", true)
                hashMap1.put("showTyping", true)
//                mDbRef.child("user").child(userLogin.uid.toString()).updateChildren(hashMap as Map<String, Any>)
                mDbRef.child("user").child(userLogin.uid.toString()).updateChildren(hashMap as Map<String, Any>)
                mDbRef.child("user").child(userFriend.uid.toString()).updateChildren(hashMap1 as Map<String, Any>)
            } else {
                hashMap.put("typing", false)
                hashMap1.put("showTyping", false)
                mDbRef.child("user").child(userLogin.uid.toString()).updateChildren(hashMap as Map<String, Any>)
                mDbRef.child("user").child(userFriend.uid.toString()).updateChildren(hashMap1 as Map<String, Any>)
            }
        }

//        mDbRef.child("user").addValueEventListener(object: ValueEventListener{
//            override fun onDataChange(snapshot: DataSnapshot) {
//               for (postSnapshot in snapshot.children) {
//                   val postUser = postSnapshot.getValue(User::class.java)
//                   if (postUser != null ) {
//                       if (postUser.uid == userFriend.uid) {
//                           if (postUser.isTyping) {
//
//                           }
//                       }
//
//                   }
//               }
//            }
//
//            override fun onCancelled(error: DatabaseError) {
//
//            }
//        })
    }

    private fun userChangeTyping() {
        mDbRef.child("user").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {

                showTyping(messageBox)

//                val userTemp = snapshot.getValue(User::class.java)
//
//                for (postSnapshot in snapshot.children) {
//
//                }
                var boolean1 = false
                var boolean2 = false

                for (postSnapshot in snapshot.children) {
                    val userTemp = postSnapshot.getValue(User::class.java)

                    if (userTemp != null) {
                        if (userTemp.uid == userFriend.uid) {
                            if (userTemp.isTyping) {
//                                textTyping.text = userFriend.name +  " is typing..."
                                boolean1 = true
                            }
                        }

                        if (userTemp.uid == userLogin.uid) {
                            if (userTemp.showTyping) {
                                boolean2 = true
                            }
                        }
                    }
                }

                if (boolean1 && boolean2) {
                    textTyping.visibility = View.VISIBLE
                    textTyping.text = userFriend.name + " " + getString(R.string.show_typing_chat)
                } else {
                    textTyping.visibility = View.GONE
                }

//                if (userFriend.isTyping) {
//                    textTyping.text = "Typing......."
//                } else {
//                    textTyping.text = ""
//                }


            }
            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ChatActivity, "typing not run", Toast.LENGTH_LONG).show()
            }

        })
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (resultCode == Activity.RESULT_OK && requestCode == 1 && data != null) {
            if (data.data != null) {
//                val selectImage = data.data
//                val storage = Firebase.storage
//                val storageRef = storage.reference
                setImageUri(data.data!!)
                imageUriTemp = data!!.data
            }
        }
    }

    private fun setImageUri(imageUri: Uri) {

//        val storeRef = Firebase.storage.reference.child("chats").child(roomSender.toString())
//            .child(System.currentTimeMillis().toString())
//        val uploadTask = storeRef.putFile(imageUri)

        sendImage.setImageURI(imageUri)

//        imageUriTemp = imageUri


//        storeRef.downloadUrl.addOnSuccessListener {
//            Picasso.get().load(it).into(sendImage)
//        }.addOnFailureListener {
//            Toast.makeText(this@ChatActivity, it.localizedMessage, Toast.LENGTH_LONG).show()
//        }

//        uploadTask.addOnSuccessListener {
//            Toast.makeText(this@ChatActivity, "Image sent", Toast.LENGTH_LONG).show()
//            storeRef.downloadUrl.addOnSuccessListener {
//                Picasso.get().load(it).into(sendImage)
//            }.addOnFailureListener {
//                Toast.makeText(this@ChatActivity, it.localizedMessage, Toast.LENGTH_LONG).show()
//            }
//        }
//            .addOnFailureListener{
//                Toast.makeText(this@ChatActivity, it.localizedMessage?.toString(), Toast.LENGTH_LONG).show()
//            }
//            .addOnCanceledListener {
//                Toast.makeText(this@ChatActivity, "Image cancel", Toast.LENGTH_LONG).show()
//            }
    }
}

