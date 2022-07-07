package com.example.chatapp.chat

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.graphics.drawable.Drawable
import android.net.Uri
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.*
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.receive.view.*

class ChatAdapter(val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val ITEM_RECEIVE = 1
    val ITEM_SENT = 2

    var loginUid: String = ""

    var status: String? = ""

    private var friendUid: String? = ""
    private val messageList = ArrayList<Message>()

    var tmpSeen: Boolean = false

    lateinit var userLogin: User
    lateinit var userFriend: User
    var hasMore: Boolean = false

    lateinit var old_Avatar: ImageView
    var new_Avatar: String? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return if (viewType == 1) {
            val view: View = LayoutInflater.from(context).inflate(R.layout.receive, parent, false)
            ReceiveViewHolder(view)
        } else {
            val view: View = LayoutInflater.from(context).inflate(R.layout.sent, parent, false)
            SentViewHolder(view)
        }
    }

    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val currentMessage = messageList[position]

        if (holder is SentViewHolder) {

            val viewHolder = holder
            viewHolder.sentMessage.text = currentMessage.message
            if (currentMessage.time != null) {
                viewHolder.time_sent.text = currentMessage.time
            }

            if (position == messageList.size - 1) {
                //viewHolder.status_Sent.visibility = View.VISIBLE
                if (currentMessage.seen) {
                    viewHolder.status_Sent.text = "Seen"
                } else {
                    viewHolder.status_Sent.text = "Delivered"
                }
            } else {
                viewHolder.status_Sent.visibility = View.GONE
            }

        } else {
            val viewHolder = holder as ReceiveViewHolder

            viewHolder.img_avatar.setOnClickListener {
                val intent = Intent(context, ProfileActivity::class.java)
                intent.putExtra("uidLogin", userLogin.uid)
                intent.putExtra("uidFriend", userFriend.uid)
                intent.putExtra("hasMore", hasMore)
                intent.putExtra("userLogin", userLogin)
                intent.putExtra("userFriend", userFriend)
                context.startActivity(intent)
            }


            Log.d("Hieu", "$viewHolder - bind ${currentMessage.message}")

            viewHolder.receiveMessage.text = currentMessage.message
            viewHolder.time_receive.text = currentMessage.time

            if (currentMessage.noAvatarMessage) {
                viewHolder.img_avatar.visibility = View.GONE
            } else {
                viewHolder.img_avatar.visibility = View.VISIBLE
                val friendUid = friendUid
                if (friendUid != null) {
                    FirebaseStorage.getInstance().reference.child("images")
                        .child(friendUid).downloadUrl.addOnSuccessListener { it ->
                            Picasso.get().load(it).into(viewHolder.img_avatar)
                        }
                }
            }
        }

    }

    override fun getItemViewType(position: Int): Int {

        val currentMessage = messageList[position]

        return if (loginUid == currentMessage.senderId) {
            ITEM_SENT
        } else {
            ITEM_RECEIVE
        }
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    class ReceiveViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val receiveMessage = itemView.findViewById<TextView>(R.id.txt_receive_message)
        val time_receive = itemView.findViewById<TextView>(R.id.time_receive)
        val img_avatar = itemView.findViewById<ImageView>(R.id.img_receiveProfile)
    }

    class SentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val sentMessage = itemView.findViewById<TextView>(R.id.txt_sent_message)
        val time_sent = itemView.findViewById<TextView>(R.id.time_sent)
        val status_Sent = itemView.findViewById<TextView>(R.id.status_messageSent)
    }

//    fun addMessage(messageObject: Message, loginUid: String, friendUid: String) {
//        this.friendUid = friendUid
//        this.loginUid = loginUid
//        messageList.add(messageObject)
//        notifyDataSetChanged()
//    }
//
//    fun addStatus(status: String?) {
//        this.status = status
//        notifyDataSetChanged()
//    }
//
//    fun addSeen(seen: Boolean) {
//        this.tmpSeen = seen
//        notifyDataSetChanged()
//    }

    fun addUid(loginUid: String, friendUid: String) {
        this.loginUid = loginUid
        this.friendUid = friendUid
    }

//    fun addUid(uid: String?) {
//        uidActing = uid
//        notifyDataSetChanged()
//    }

//    class MessageDiffCallBack: DiffUtil.ItemCallback<Message>() {
//        override fun areItemsTheSame(oldItem: Message, newItem: Message): Boolean {
//            return oldItem.senderId == newItem.senderId
//        }
//
//        @SuppressLint("DiffUtilEquals")
//        override fun areContentsTheSame(oldItem: Message, newItem: Message): Boolean {
//            return oldItem == newItem
//        }
//
//    }

    fun updateData (newList: ArrayList<Message>) {
        val diffCallBack = MessageDiffUtil(messageList, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallBack)

        messageList.clear()
        messageList.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
    }

    fun setValueUser(userLogin: User, userFriend: User, hasMore: Boolean) {
        this.userLogin = userLogin
        this.userFriend = userFriend
        this.hasMore = hasMore
    }
}

