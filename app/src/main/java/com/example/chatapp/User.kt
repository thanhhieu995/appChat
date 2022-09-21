package com.example.chatapp

import java.io.Serializable

class User: Serializable {
    var name: String? = ""
    var email: String? = ""
    var uid: String? = ""
    var status: String? = ""
    var avatar: String? = ""
    var calling: Boolean = false
    var acceptCall: Boolean = false
    var listToken: ArrayList<String>? = ArrayList()

    constructor() {}
    constructor(name: String?, email: String?, uid: String?, status: String?, avatar: String?, calling: Boolean, acceptCall: Boolean, listToken: ArrayList<String>) {
        this.name = name
        this.email = email
        this.uid = uid
        this.status = status
        this.avatar = avatar
        this.calling = calling
        this.acceptCall = acceptCall
        this.listToken = listToken
    }
}