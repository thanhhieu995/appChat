package com.example.chatapp

data class Unread(
    var unread: Int = 0,
    var fromUid: String = "",
    var toUid: String = ""
    ) {
}