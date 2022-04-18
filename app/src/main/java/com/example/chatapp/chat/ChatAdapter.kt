package com.example.chatapp.chat

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.Message
import com.example.chatapp.R
import com.google.firebase.auth.FirebaseAuth
import org.w3c.dom.Text

class ChatAdapter(val context: Context, val messageList: ArrayList<Message>): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val ITEM_RECEIVE = 1
    val ITEM_SENT = 2

    var hasMore: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        if(viewType == 1) {
            val view: View = LayoutInflater.from(context).inflate(R.layout.receive, parent, false)
            return ReceiveViewHolder(view)
        } else {
            val view: View = LayoutInflater.from(context).inflate(R.layout.sent, parent, false)
            return SentViewHolder(view)
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {

        val currentMessage = messageList[position]

//        if (hasMore) {
//            currentMessage.status_message = "Delivery"
//        } else {
//
//            currentMessage.status_message = "Seen"
//        }

        if (holder.javaClass == SentViewHolder::class.java) {

            val viewHolder = holder as SentViewHolder
            //holder.sentMessage.text = currentMessage.message
            viewHolder.sentMessage.text = currentMessage.message
            if (currentMessage.time != null) {
                viewHolder.time_sent.text = currentMessage.time
            }


            viewHolder.status_Sent.text = currentMessage.status_message

        } else {
            val viewHolder = holder as ReceiveViewHolder
            //holder.receiveMessage.text = currentMessage.message
            viewHolder.receiveMessage.text = currentMessage.message
            if (currentMessage.time != null) {
                viewHolder.time_receive.text = currentMessage.time
            }
            viewHolder.status_receive.text = currentMessage.status_message
        }
    }

    override fun getItemViewType(position: Int): Int {

        val currentMessage = messageList[position]

        if (FirebaseAuth.getInstance().currentUser?.uid.equals(currentMessage.senderId)) {
                hasMore = true
            return ITEM_SENT
        } else {
            return ITEM_RECEIVE
        }
        notifyDataSetChanged()

    }

    override fun getItemCount(): Int {
        return messageList.size
    }

    class ReceiveViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val receiveMessage = itemView.findViewById<TextView>(R.id.txt_receive_message)
        val time_receive = itemView.findViewById<TextView>(R.id.time_receive)
        val status_receive = itemView.findViewById<TextView>(R.id.status_messageReceive)
    }

    class SentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView){
        val sentMessage = itemView.findViewById<TextView>(R.id.txt_sent_message)
        val time_sent = itemView.findViewById<TextView>(R.id.time_sent)
        val status_Sent = itemView.findViewById<TextView>(R.id.status_messageSent)
    }

    fun addMessage(messageObject: Message, receiveUid: String) {
        messageList.add(messageObject)
        notifyDataSetChanged()
    }
}

