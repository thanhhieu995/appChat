package com.example.chatapp

class Status {
    var sender: String? = null
    var receiver: String? = null
    var message: String? = null
    private var isSeen: Boolean? = false

    constructor(sender: String?, receiver: String?, message: String?, isSeen: Boolean?) {
        this.sender = sender
        this.receiver = receiver
        this.message = message
        this.isSeen = isSeen
    }

}