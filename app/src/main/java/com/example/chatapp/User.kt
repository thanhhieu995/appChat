package com.example.chatapp

class User {
    var name: String? = null
    var email: String? = null
    var uid: String? = null
    var status: String? = ""
    var avatar: String? = ""

    constructor() {}
    constructor(name: String?, email: String?, uid: String?, status: String?, avatar: String?) {
        this.name = name
        this.email = email
        this.uid = uid
        this.status = status
        this.avatar = avatar
    }
}