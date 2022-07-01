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
    var avatarSendUrl: String? = null
    var avatarReceiveUrl: String? = null

    constructor() { }
    constructor(
        message: String?,
        senderId: String?,
        receiveId: String?,
        time: String?,
        seen: Boolean,
        noAvatarMessage: Boolean,
        avatarSendUrl: String,
        avatarReceiveUrl: String
    ) {
        this.message = message
        this.senderId = senderId
        this.receiveId = receiveId
        this.time = time
        this.seen = seen
        this.noAvatarMessage = noAvatarMessage
        this.avatarSendUrl = avatarSendUrl
        this.avatarReceiveUrl = avatarReceiveUrl
    }
}