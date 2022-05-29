package com.example.chatapp.chat

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.Message
import com.example.chatapp.R
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class ChatAdapter(val context: Context, val messageList: ArrayList<Message>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val ITEM_RECEIVE = 1
    val ITEM_SENT = 2

    var loginUid: String = ""

    var status: String? = ""

    private var friendUid: String? = ""

    var tmpSeen: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return if(viewType == 1) {
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

        if (holder.javaClass == SentViewHolder::class.java) {

            val viewHolder = holder as SentViewHolder
            viewHolder.sentMessage.text = currentMessage.message
            if (currentMessage.time != null) {
                viewHolder.time_sent.text = currentMessage.time
            }

            if (position == messageList.size - 1) {
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
            viewHolder.receiveMessage.text = currentMessage.message
            if (currentMessage.time != null) {
                viewHolder.time_receive.text = currentMessage.time
            }
            if (currentMessage.noAvatarMessage && position != 0) {
                if (currentMessage.year == messageList[position -1].year && currentMessage.month == messageList[position -1].month
                    && currentMessage.date.date == messageList[position - 1].date.date
                    && currentMessage.date.hours == messageList[position -1].date.hours) {
                    viewHolder.img_avatar.visibility = View.GONE
                }
            } else {
                FirebaseStorage.getInstance().reference.child("images").child(friendUid!!).downloadUrl.addOnSuccessListener {
                    Picasso.get().load(it).into(viewHolder.img_avatar)
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
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    class ReceiveViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val receiveMessage = itemView.findViewById<TextView>(R.id.txt_receive_message)
        val time_receive = itemView.findViewById<TextView>(R.id.time_receive)
        val img_avatar = itemView.findViewById<ImageView>(R.id.img_receiveProfile)
    }

    class SentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val sentMessage = itemView.findViewById<TextView>(R.id.txt_sent_message)
        val time_sent = itemView.findViewById<TextView>(R.id.time_sent)
        val status_Sent = itemView.findViewById<TextView>(R.id.status_messageSent)
    }

    fun addMessage(messageObject: Message, loginUid: String, friendUid: String) {
        this.friendUid = friendUid
        this.loginUid = loginUid
        messageList.add(messageObject)
        notifyDataSetChanged()
    }

    fun addStatus(status: String?) {
        this.status = status
        notifyDataSetChanged()
    }

    fun addSeen(seen: Boolean) {
        this.tmpSeen = seen
        notifyDataSetChanged()
    }

//    fun addUid(uid: String?) {
//        uidActing = uid
//        notifyDataSetChanged()
//    }
}

