package com.example.chatapp

import androidx.recyclerview.widget.DiffUtil

class MessageDiffUtil(val oldList: List<Message>, val newList: List<Message>) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
       return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition].senderId == newList[newItemPosition].senderId
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return when {
            oldList[oldItemPosition].senderId != newList[newItemPosition].senderId ->
                false
            oldList[oldItemPosition].receiveId != newList[newItemPosition].receiveId ->
                false
            oldList[oldItemPosition].noAvatarMessage != newList[newItemPosition].noAvatarMessage ->
                false
            oldList[oldItemPosition].date != newList[newItemPosition].date ->
                false
            oldList[oldItemPosition].message != newList[newItemPosition].message ->
                false
            oldList[oldItemPosition].seen != newList[newItemPosition].seen ->
                false
            else -> {true}
        }
    }
}