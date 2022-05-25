package com.example.chatapp

import android.widget.TextView
import java.util.*

class Message {
    var message: String? = null
    var senderId: String? = null
    var receiveId: String? = null
    var time: String?= null
    //var status_message: String? = null
    var seen: Boolean = false
    lateinit var date: Date

    constructor() { }
    constructor(
        message: String?,
        senderId: String?,
        receiveId: String?,
        time: String?,
        seen: Boolean,
        date: Date
    ) {
        this.message = message
        this.senderId = senderId
        this.receiveId = receiveId
        this.time = time
        this.seen = seen
        this.date = date
    }
}