package vnd.hieuUpdate.chitChat.main

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import vnd.hieuUpdate.chitChat.R
import vnd.hieuUpdate.chitChat.Unread
import vnd.hieuUpdate.chitChat.User
import vnd.hieuUpdate.chitChat.UserDiffUtil
import vnd.hieuUpdate.chitChat.chat.ChatActivity

class UserAdapter(val context: Context): RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    private var userList = ArrayList<User>()

    var uidLogin: String? = null

    var statusLogin: String? = ""

    var statusFriend: String? = ""

    var hasMore: Boolean = false

    var userLogin: User = User()

    var userFriend: User = User()
    //private lateinit var mAuth: FirebaseAuth
    lateinit var message: vnd.hieuUpdate.chitChat.Message

    private var count: Int = 0

    var lastMsg: String? = null

    var isGone: Boolean = false

    private val storageRef = FirebaseStorage.getInstance().reference
//    val islandRef = storageRef.child("images").child(mAuth.uid.toString())
    var unRead: Unread = Unread(0, "", "")

    lateinit var clickAddFriend: ClickAddFriend

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.user_layout, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        val currentUser = userList[position]
        currentUser.uid?.let {
            storageRef.child("images").child(it)
                .downloadUrl.addOnSuccessListener {
                    Picasso.get().load(it).into(holder.imgAvatar)
                }
        }

        if (currentUser.isTyping && userLogin.showTyping) {
            holder.typing.text = "Typing..."
        } else {
            holder.typing.text = ""
        }

        if (currentUser.uid == unRead.fromUid && userLogin.uid == unRead.toUid) {
            holder.numberNotification.text = unRead.unread.toString()
            if (unRead.unread == 0) {
                holder.numberNotification.text = ""
            }
        }

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

                hasMore = true
                intent.putExtra("hasMore", hasMore)


                intent.putExtra("userLogin", userLogin)

                intent.putExtra("userFriend", currentUser)

                context.startActivity(intent)
            }
        })

        if (isGone) {
            holder.addFriendButton.visibility = View.GONE
        } else {
            holder.addFriendButton.visibility = View.VISIBLE
        }

        holder.addFriendButton.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                clickAddFriend.onClick(currentUser)
            }
        })
    }

    override fun getItemCount(): Int {
        return userList.size
    }

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textName: TextView = itemView.findViewById(R.id.txt_name)
        val textStatus: TextView = itemView.findViewById(R.id.txt_statusMain)
        var imgAvatar: ImageView = itemView.findViewById(R.id.imgAva_main)
        var recentMessage = itemView.findViewById<TextView>(R.id.msg_recently)
        var typing: TextView = itemView.findViewById(R.id.main_typing)
        val numberNotification: TextView = itemView.findViewById(R.id.countNumber)
        val addFriendButton = itemView.findViewById<Button>(R.id.user_btn_addFriend)
    }


    fun addUidLogin(Uid: String?) {
        this.uidLogin = Uid
        notifyDataSetChanged()
    }

    fun addUserLogin(userLogin: User) {
        this.userLogin = userLogin
    }

    fun unRead(unRead: Unread) {
        this.unRead = unRead
    }

    fun addLastMsg(lastMsg: String) {
        this.lastMsg = lastMsg
    }

    interface ClickAddFriend{
        fun onClick(user: User)
    }

    fun setButtonAddFriendClick(clickAddFriend: ClickAddFriend) {
        this.clickAddFriend = clickAddFriend
    }

    fun setGoneButtonAdd(isGone: Boolean) {
        this.isGone = isGone
    }

    fun updateListFriend(newList: ArrayList<User>) {
        val diffCallBack = UserDiffUtil(userList, newList)
        val diffResult = DiffUtil.calculateDiff(diffCallBack)

        userList.clear()
        userList.addAll(newList)
        diffResult.dispatchUpdatesTo(this)
    }
}