package com.example.chatapp.main

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.Message
import com.example.chatapp.chat.ChatActivity
import com.example.chatapp.R
import com.example.chatapp.Unread
import com.example.chatapp.User
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class UserAdapter(val context: Context, private var userList: ArrayList<User>): RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    private var dataList = ArrayList<User>(userList)

    var uidLogin: String? = null

    var statusLogin: String? = ""

    var statusFriend: String? = ""

    var hasMore: Boolean = false

    var userLogin: User = User()

    var userFriend: User = User()
    //private lateinit var mAuth: FirebaseAuth
    lateinit var message: Message

    private var count: Int = 0

    var lastMsg: String? = null

    private val storageRef = FirebaseStorage.getInstance().reference
//    val islandRef = storageRef.child("images").child(mAuth.uid.toString())
    var unRead: Unread = Unread(0, "", "")


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.user_layout, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        //val currentUser = dataList[position]
        val currentUser = userList[position]
        //val statusList = statusList[position]
        //holder.imgAvatar.setImageURI(islandRef)
        if (holder.imgAvatar.drawable == null) {
            currentUser.uid?.let {
                storageRef.child("images").child(it)
                    .downloadUrl.addOnSuccessListener {
                        //holder.imgAvatar.setImageURI(it)
                        Picasso.get().load(it).into(holder.imgAvatar)
                    }
            }
        }

        if (currentUser.isTyping && userLogin.showTyping) {
            holder.typing.text = "Typing..."
        } else {
            holder.typing.text = ""
        }

        if (!currentUser.lastMsg.isNullOrEmpty() && currentUser.uid == currentUser.receiveUid || currentUser.uid == currentUser.sendToUid) {
            holder.recentMessage.text = lastMsg.toString()
        } else {
            holder.recentMessage.text = ""
        }

//        if (unRead != 0) {
//            holder.numberNotification.text = unRead.toString()
//        } else {
//            holder.numberNotification.text = ""
//        }

//        if ( currentUser.uid == userLogin.receiveUid) {
//            holder.numberNotification.text = userLogin.unRead.toString()
//        } else {
//            holder.numberNotification.text = ""
//        }

//        if (userLogin.unRead != 0 && currentUser.uid == userLogin.receiveUid) {
//            holder.numberNotification.text = userLogin.unRead.toString()
//        } else {
//            holder.numberNotification.text = ""
//        }

//        if (currentUser.uid == userLogin.receiveUid) {
//            if (userLogin.unRead != 0) {
//                holder.numberNotification.text = userLogin.unRead.toString()
//            } else {
//                holder.numberNotification.text = ""
//            }
//        }

        if (currentUser.uid == unRead.fromUid && userLogin.uid == unRead.toUid) {
            holder.numberNotification.text = unRead.unread.toString()
            if (unRead.unread == 0) {
                holder.numberNotification.text = ""
            }
        }

//        if (unRead != "0") {
//            holder.numberNotification.text = unRead
//        } else {
//            holder.numberNotification.text = ""
//        }

//        if (message.senderId == userLogin.uid) {
//            holder.recentMessage.text = "you: " + message.message.toString()
//        } else {
//            holder.recentMessage.text = message.message.toString()
//        }
        //islandRef.getResult()

        //Glide.with(context).load(islandRef).into(holder.imgAvatar)
        //Picasso.get().load("https://firebasestorage.googleapis.com/v0/b/chat-app-c2e09.appspot.com/o/images%2FZWs9udNel9cbPsInO5iwvZqWzZY2?alt=media&token=e28522cb-86fd-45d9-acd6-1f86a072d7b2").into(holder.imgAvatar)
       // if (FirebaseAuth.getInstance().currentUser?.uid != currentUser.uid ) {
            holder.textName.text = currentUser.name
        holder.textStatus.text = currentUser.status
        if (holder.textStatus.text == "online") {
            holder.textStatus.setTextColor(Color.GREEN)
        } else {
            holder.textStatus.setTextColor(Color.BLACK)
        }



        holder.itemView.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                val intent = Intent(context, ChatActivity::class.java)

//                intent.putExtra("name", currentUser.name)
//                intent.putExtra("uidFriend", currentUser.uid)

                hasMore = true
                intent.putExtra("hasMore", hasMore)
                //intent.putExtra("statusFriend", currentUser.status.toString())

//                intent.putExtra("uidLogin", uidLogin)

                intent.putExtra("userLogin", userLogin)

                intent.putExtra("userFriend", currentUser)

                context.startActivity(intent)
//                notifyDataSetChanged()
            }
        })
    }

    override fun getItemCount(): Int {
        //return dataList.size
        return userList.size
    }

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textName = itemView.findViewById<TextView>(R.id.txt_name)
        val textStatus = itemView.findViewById<TextView>(R.id.txt_statusMain)
        var imgAvatar = itemView.findViewById<ImageView>(R.id.imgAva_main)
        var recentMessage = itemView.findViewById<TextView>(R.id.msg_recently)
        var typing = itemView.findViewById<TextView>(R.id.main_typing)
        val numberNotification = itemView.findViewById<TextView>(R.id.countNumber)
    }

    fun addItems(item: User?) {
        if (item != null) {
            dataList.add(item)
        }
        notifyDataSetChanged()
    }

    fun addUidLogin(Uid: String?) {
        this.uidLogin = Uid
        notifyDataSetChanged()
    }

    fun addStatusAccountLogin(status: String?) {
        this.statusLogin = status
        notifyDataSetChanged()
    }

    fun addStatusFriend(status: String?) {
        this.statusFriend = status
        notifyDataSetChanged()
    }


    fun addUserLogin(userLogin: User) {
        this.userLogin = userLogin
    }

    fun addUserList(userList: ArrayList<User>) {
        this.userList = userList
    }

//    fun addLastMessage(message: Message) {
//        this.message = message
//    }
//
//    fun addNumberNotification(unRead : Int) {
//        this.unRead = unRead.toString()
//    }
    fun addCount(count: Int) {
        this.count = count
    }

    fun unRead(unRead: Unread) {
        this.unRead = unRead
    }

    fun addLastMsg(lastMsg: String) {
        this.lastMsg = lastMsg
    }
}