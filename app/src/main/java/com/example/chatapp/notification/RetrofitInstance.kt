package com.example.chatapp.notification

import com.example.chatapp.notification.Constants.Companion.BASE_URL
import retrofit.BaseUrl
import retrofit.GsonConverterFactory
import retrofit.Retrofit

class RetrofitInstance {

    companion object {
        private val retrofit by lazy {
            Retrofit.Builder()
                .baseUrl(BASE_URL)
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        val api by lazy {
            retrofit.create(NotificationAPI:: class.java)
        }
    }
}