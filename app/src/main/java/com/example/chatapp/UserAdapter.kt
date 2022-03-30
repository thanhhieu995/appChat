package com.example.chatapp

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class UserAdapter(val context: Context, val userList: ArrayList<User>): RecyclerView.Adapter<UserAdapter.UserViewHolder>() {

      val dataList = ArrayList<User>(userList)

//    private fun init() {
//        this.dataList.addAll(userList)
//        notifyDataSetChanged()
//    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UserViewHolder {
        val view: View = LayoutInflater.from(context).inflate(R.layout.user_layout, parent, false)
        return UserViewHolder(view)
    }

    override fun onBindViewHolder(holder: UserViewHolder, position: Int) {
//        for (user in userList) {
//            //val currentUser = userList[position]
//            holder.textName.text = user.name
//        }
//        init()
        val currentUser = dataList[position]
        holder.textName.text = currentUser.name
    }

    override fun getItemCount(): Int {
        return dataList.size
    }

    class UserViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textName = itemView.findViewById<TextView>(R.id.txt_name)
    }

    fun addItems(item: User) {
        dataList.addAll(listOf(item))
        notifyDataSetChanged()
    }
}