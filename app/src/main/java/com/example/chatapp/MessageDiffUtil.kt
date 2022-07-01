package com.example.chatapp

import androidx.recyclerview.widget.DiffUtil

class MessageDiffUtil(private val oldList: ArrayList<Message>, private val newList: ArrayList<Message>) : DiffUtil.Callback() {
    override fun getOldListSize(): Int {
       return oldList.size
    }

    override fun getNewListSize(): Int {
        return newList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldList[oldItemPosition] == newList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return when {
            oldList[oldItemPosition].senderId != newList[newItemPosition].senderId ->
                false
            oldList[oldItemPosition].receiveId != newList[newItemPosition].receiveId ->
                false
            oldList[oldItemPosition].noAvatarMessage != newList[newItemPosition].noAvatarMessage ->
                false
            oldList[oldItemPosition].message != newList[newItemPosition].message ->
                false
            oldList[oldItemPosition].seen != newList[newItemPosition].seen ->
                false
            oldList[oldItemPosition].time != newList[newItemPosition].time ->
                false
            oldList[oldItemPosition].avatarSendUrl != newList[newItemPosition].avatarSendUrl ->
                false
            oldList[oldItemPosition].avatarReceiveUrl != newList[newItemPosition].avatarReceiveUrl ->
                false
            else -> {true}
        }
    }


    override fun getChangePayload(oldItemPosition: Int, newItemPosition: Int): Any? {
        oldList[oldItemPosition] = newList[newItemPosition]
        return super.getChangePayload(oldItemPosition, newItemPosition)
    }
}