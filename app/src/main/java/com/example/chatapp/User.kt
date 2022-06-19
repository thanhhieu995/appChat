package com.example.chatapp

import java.io.Serializable

class User: Serializable {
    var name: String? = null
    var email: String? = null
    var uid: String? = null
    var status: String? = ""
    var avatar: String? = ""
    var calling: Boolean = false
    var acceptCall: Boolean = false

    constructor() {}
    constructor(name: String?, email: String?, uid: String?, status: String?, avatar: String?, calling: Boolean, acceptCall: Boolean) {
        this.name = name
        this.email = email
        this.uid = uid
        this.status = status
        this.avatar = avatar
        this.calling = calling
        this.acceptCall = acceptCall
    }
}