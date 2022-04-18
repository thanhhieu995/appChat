package com.example.chatapp

import android.widget.TextView

class Message {
    var message: String? = null
    var senderId: String? = null
    var time: String?= null
    var status_message: String? = null

    constructor() { }
    constructor(message: String?, senderId: String?, time: String?, status_message: String) {
        this.message = message
        this.senderId = senderId
        this.time = time
        this.status_message = status_message
    }
}