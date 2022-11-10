package vnd.hieuUpdate.chitChat

import androidx.recyclerview.widget.DiffUtil

class UserDiffUtil(var oldUserList: ArrayList<User>, var newUserList : ArrayList<User>) :
    DiffUtil.Callback() {
    override fun getOldListSize(): Int {
        return oldUserList.size
    }

    override fun getNewListSize(): Int {
        return newUserList.size
    }

    override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldUserList[oldItemPosition] == newUserList[newItemPosition]
    }

    override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
        return oldUserList[oldItemPosition] == newUserList[newItemPosition]
    }
}