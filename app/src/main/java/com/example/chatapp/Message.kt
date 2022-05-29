package com.example.chatapp

import android.widget.TextView
import java.time.Month
import java.time.Year
import java.util.*

class Message {
    var message: String? = null
    var senderId: String? = null
    var receiveId: String? = null
    var time: String?= null
    var seen: Boolean = false
    lateinit var date: Date

    var month: Int = 0
    var year: Int = 0
    var hasAvatar: Boolean = false

    constructor() { }
    constructor(
        message: String?,
        senderId: String?,
        receiveId: String?,
        time: String?,
        seen: Boolean,
        date: Date,
        month: Int,
        year: Int,
        hasAvatar: Boolean
    ) {
        this.message = message
        this.senderId = senderId
        this.receiveId = receiveId
        this.time = time
        this.seen = seen
        this.date = date
        this.month = month
        this.year = year
        this.hasAvatar = hasAvatar
    }
}