package vnd.hieuUpdate.chitChat.chat

import android.annotation.SuppressLint
import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import vnd.hieuUpdate.chitChat.R
import vnd.hieuUpdate.chitChat.User

class ChatAdapter(val context: Context) :
    RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    val ITEM_RECEIVE = 1
    val ITEM_SENT = 2

    var loginUid: String = ""

    var status: String? = ""

    private var friendUid: String? = ""
    private val messageList = ArrayList<vnd.hieuUpdate.chitChat.Message>()

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
//            if (currentMessage.time != null) {
//                viewHolder.time_sent.text = currentMessage.time
//            }

            if (currentMessage.noAvatarMessage) {
                viewHolder.img_avatarSent.visibility = View.GONE
                viewHolder.time_sent.visibility = View.GONE
            } else {
                viewHolder.img_avatarSent.visibility = View.VISIBLE
                viewHolder.time_sent.visibility = View.VISIBLE
                viewHolder.time_sent.text = currentMessage.time
                FirebaseStorage.getInstance().reference.child("images")
                    .child(loginUid).downloadUrl.addOnSuccessListener { it ->
                        Picasso.get().load(it).into(viewHolder.img_avatarSent)
                    }
            }

            if (currentMessage.seen) {
//                viewHolder.img_Avatar_Status.visibility  = View.GONE
                viewHolder.status_Sent.visibility = View.GONE
                if (position == messageList.size - 1) {
                    viewHolder.img_Avatar_Status.visibility = View.VISIBLE
                    friendUid?.let { it ->
                        FirebaseStorage.getInstance().reference.child("images")
                            .child(it).downloadUrl.addOnSuccessListener {
                                Picasso.get().load(it).into(viewHolder.img_Avatar_Status)
                            }
                    }
                } else {
                    viewHolder.img_Avatar_Status.visibility = View.GONE
                }
            } else {
                viewHolder.status_Sent.visibility = View.VISIBLE
                viewHolder.img_Avatar_Status.visibility = View.GONE
                viewHolder.status_Sent.text = "sent"
            }

//            if (position != messageList.size - 1) {
//                viewHolder.img_Avatar_Status.visibility = View.GONE
//                viewHolder.status_Sent.visibility = View.GONE
//            } else {
//                viewHolder.img_Avatar_Status.visibility = View.VISIBLE
//                viewHolder.status_Sent.visibility = View.VISIBLE
//            }




//            if (currentMessage.seen) {
//                viewHolder.status_Sent.visibility = View.GONE
//                viewHolder.img_Avatar_Status.visibility = View.GONE
//                if (position == messageList.size - 1) {
//                    viewHolder.img_Avatar_Status.visibility = View.VISIBLE
//                    friendUid?.let {
//                        FirebaseStorage.getInstance().reference.child("images")
//                            .child(it).downloadUrl.addOnSuccessListener { it ->
//                                Picasso.get().load(it).into(viewHolder.img_Avatar_Status)
//                            }
//                    }
//
//                } else {
//                    viewHolder.img_Avatar_Status.visibility = View.GONE
//                }
//            } else {
//                viewHolder.status_Sent.visibility = View.VISIBLE
//                viewHolder.status_Sent.text = "sent"
//                viewHolder.img_Avatar_Status.visibility = View.GONE
//            }

        } else {
            val viewHolder = holder as ReceiveViewHolder

            viewHolder.img_avatar.setOnClickListener {
                val intent = Intent(context, vnd.hieuUpdate.chitChat.ProfileActivity::class.java)
                intent.putExtra("uidLogin", userLogin.uid)
                intent.putExtra("uidFriend", userFriend.uid)
                intent.putExtra("hasMore", hasMore)
                intent.putExtra("userLogin", userLogin)
                intent.putExtra("userFriend", userFriend)
                context.startActivity(intent)
            }


            Log.d("Hieu", "$viewHolder - bind ${currentMessage.message}")

            viewHolder.receiveMessage.text = currentMessage.message


            if (currentMessage.noAvatarMessage) {
                viewHolder.img_avatar.visibility = View.GONE
                viewHolder.time_receive.visibility = View.GONE
            } else {
                viewHolder.img_avatar.visibility = View.VISIBLE
                viewHolder.time_receive.visibility = View.VISIBLE
                viewHolder.time_receive.text = currentMessage.time
                val friendUid = friendUid
                if (friendUid != null) {
                    FirebaseStorage.getInstance().reference.child("images")
                        .child(friendUid).downloadUrl.addOnSuccessListener { it ->
                            Picasso.get().load(it).into(viewHolder.img_avatar)
                        }
                }
            }

//            if (userFriend.isTyping) {
//                viewHolder.typing.text = "Typing..."
//            } else {
//                viewHolder.typing.text = ""
//            }
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
        val typing = itemView.findViewById<TextView>(R.id.textTyping)
    }

    class SentViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val img_avatarSent = itemView.findViewById<ImageView>(R.id.img_Avatar_Send)
        val sentMessage = itemView.findViewById<TextView>(R.id.txt_sent_message)
        val time_sent = itemView.findViewById<TextView>(R.id.time_sent)
        val status_Sent = itemView.findViewById<TextView>(R.id.status_messageSent)
        val img_Avatar_Status = itemView.findViewById<ImageView>(R.id.img_Avatar_Status)
    }


    fun addUid(loginUid: String, friendUid: String) {
        this.loginUid = loginUid
        this.friendUid = friendUid
    }

    fun updateData (newList: ArrayList<vnd.hieuUpdate.chitChat.Message>) {
        val diffCallBack = vnd.hieuUpdate.chitChat.MessageDiffUtil(messageList, newList)
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

    fun typingChange(typing: String) {

    }
}

