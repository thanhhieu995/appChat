package com.example.chatapp

import android.util.Log
import androidx.recyclerview.widget.DiffUtil

class MessageDiffUtil(private val oldList: ArrayList<Message>, private val newList: ArrayList<Message>) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
       return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val same = oldList[oldItemPosition] == newList[newItemPosition]
        if (!same) {
            Log.d("same", same.toString())
        }
        return same
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        val compare = oldList[oldItemPosition] == newList[newItemPosition]
        if (!compare) {
            Log.d("compare", compare.toString())
        }
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

}