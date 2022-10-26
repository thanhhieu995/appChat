package vnd.hieuUpdate.chitChat

import java.io.Serializable

class User: Serializable {
    var name: String? = null
    var email: String? = null
    var uid: String? = null
    var status: String? = null
    var avatar: String? = null
    var calling: Boolean = false
    var acceptCall: Boolean = false
    var isTyping: Boolean = false
    var showTyping: Boolean = false
    var unRead: Int = 0
    var receiveUid: String? = null
    var lastMsg: String? = null
    var listToken: ArrayList<String>? = ArrayList()
    var sendToUid: String? = null

    constructor() {}
    constructor(name: String?, email: String?, uid: String?, status: String?, avatar: String?, calling: Boolean, acceptCall: Boolean, isTyping: Boolean, showTyping: Boolean, count: Int, fromUid: String, lastMsg: String, listToken: ArrayList<String>, room: String) {
        this.name = name
        this.email = email
        this.uid = uid
        this.status = status
        this.avatar = avatar
        this.calling = calling
        this.acceptCall = acceptCall
        this.isTyping = isTyping
        this.showTyping = showTyping
        this.unRead = count
        this.receiveUid = fromUid
        this.lastMsg = lastMsg
        this.listToken = listToken
        this.sendToUid = room
    }
}