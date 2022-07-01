package com.example.chatapp

import android.icu.text.DateFormat
import java.util.*

data class Message (
    var message: String? = null,
    var senderId: String? = null,
    var receiveId: String? = null,
    var time: String?= null,
    var seen: Boolean = false,
    var noAvatarMessage: Boolean = false,
    var avatarSendUrl: String? = null,
    var avatarReceiveUrl: String? = null
) {
}