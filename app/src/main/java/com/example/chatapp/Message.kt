package com.example.chatapp

import android.icu.text.DateFormat
import java.util.*

class Message {
    var message: String? = null
    var senderId: String? = null
    var receiveId: String? = null
    var time: String?= null
    var seen: Boolean = false
    var noAvatarMessage: Boolean = false

    constructor() { }
    constructor(
        message: String?,
        senderId: String?,
        receiveId: String?,
        time: String?,
        seen: Boolean,
        noAvatarMessage: Boolean
    ) {
        this.message = message
        this.senderId = senderId
        this.receiveId = receiveId
        this.time = time
        this.seen = seen
        this.noAvatarMessage = noAvatarMessage
    }
}