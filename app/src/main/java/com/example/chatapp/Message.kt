package com.example.chatapp

import android.widget.TextView

class Message {
    var message: String? = null
    var senderId: String? = null
    var receiveId: String? = null
    var time: String?= null
    //var status_message: String? = null
    var seen: Boolean = false

    constructor() { }
    constructor(
        message: String?,
        senderId: String?,
        receiveId: String?,
        time: String?,
        seen: Boolean
    ) {
        this.message = message
        this.senderId = senderId
        this.receiveId = receiveId
        this.time = time
        this.seen = seen
    }

}