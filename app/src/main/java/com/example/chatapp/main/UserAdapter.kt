package com.example.chatapp.main

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.chatapp.chat.ChatActivity
import com.example.chatapp.R
import com.example.chatapp.Status
import com.example.chatapp.User

class UserAdapter(val context: Context, private val userList: ArrayList<User>): RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    private val dataList = ArrayList<User>(userList)

    var uidLogin: String? = null

    var statusLogin: String? = ""

    var statusFriend: String? = ""

    var hasMore: Boolean = false

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.user_layout, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        //val currentUser = dataList[position]
        val currentUser = userList[position]
        //val statusList = statusList[position]
       // if (FirebaseAuth.getInstance().currentUser?.uid != currentUser.uid ) {
            holder.textName.text = currentUser.name
        holder.textStatus.text = currentUser.status
        if (holder.textStatus.text == "online") {
            holder.textStatus.setTextColor(Color.GREEN)
        }

        holder.itemView.setOnClickListener(object : View.OnClickListener{
            override fun onClick(v: View?) {
                val intent = Intent(context, ChatActivity::class.java)

                intent.putExtra("name", currentUser.name)
                intent.putExtra("uidFriend", currentUser.uid)

                hasMore = true
                intent.putExtra("hasMore", hasMore)
                //intent.putExtra("statusFriend", currentUser.status.toString())

                intent.putExtra("uidLogin", uidLogin)

                context.startActivity(intent)
                notifyDataSetChanged()
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
}