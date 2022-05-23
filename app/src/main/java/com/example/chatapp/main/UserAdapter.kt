package com.example.chatapp.main

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.example.chatapp.chat.ChatActivity
import com.example.chatapp.R
import com.example.chatapp.Status
import com.example.chatapp.User
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.OnPausedListener
import com.google.firebase.storage.StorageReference
import com.squareup.picasso.Picasso

class UserAdapter(val context: Context, private var userList: ArrayList<User>): RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

    private var dataList = ArrayList<User>(userList)

    var uidLogin: String? = null

    var statusLogin: String? = ""

    var statusFriend: String? = ""

    var hasMore: Boolean = false
    //private lateinit var mAuth: FirebaseAuth

    private val storageRef = FirebaseStorage.getInstance().reference
//    val islandRef = storageRef.child("images").child(mAuth.uid.toString())


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.user_layout, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
        //val currentUser = dataList[position]
        val currentUser = userList[position]
        //val statusList = statusList[position]
        //holder.imgAvatar.setImageURI(islandRef)
        storageRef.child("images").child(currentUser.uid!!)
            .downloadUrl.addOnSuccessListener {
                //holder.imgAvatar.setImageURI(it)
                Picasso.get().load(it).into(holder.imgAvatar)
            }
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
        var imgAvatar = itemView.findViewById<ImageView>(R.id.imgAva_main)
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

    fun filter(text: String) {
        var filterList = ArrayList<User>()
        if (text.isEmpty()) {
            filterList = userList
        } else {
            for (data in userList) {
                if (data.name?.toLowerCase()?.contains(text.toLowerCase()) == true) {

                }
            }
            //userList = filterList
        }
        notifyDataSetChanged()
    }

    fun addAvatar() {

        val One_Megabyte: Long = 1024 * 1024

//        islandRef.downloadUrl.addOnSuccessListener {
//            Toast.makeText(context, "load success", Toast.LENGTH_LONG).show()
//        }
    }
}