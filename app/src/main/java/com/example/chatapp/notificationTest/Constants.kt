package com.example.chatapp.notificationTest

import com.example.chatapp.BuildConfig
import java.util.*

class Constants {

    companion object {
        const val BASE_URL = "https://fcm.googleapis.com"
        const val SERVER_KEY = BuildConfig.API_KEY
        const val CONTENT_TYPE = "application/json"
    }
}