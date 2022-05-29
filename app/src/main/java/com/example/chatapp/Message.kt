package com.example.chatapp

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
    var noAvatarMessage: Boolean = false

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
        noAvatarMessage: Boolean
    ) {
        this.message = message
        this.senderId = senderId
        this.receiveId = receiveId
        this.time = time
        this.seen = seen
        this.date = date
        this.month = month
        this.year = year
        this.noAvatarMessage = noAvatarMessage
    }
}