package com.example.chatapp.notification

import retrofit.BaseUrl
import retrofit.GsonConverterFactory
import retrofit.Retrofit

class RetrofitInstance {

    companion object {
        private val retrofit by lazy {
            Retrofit.Builder()
                .addConverterFactory(GsonConverterFactory.create())
                .build()
        }

        val api by lazy {
            retrofit.create(NotificationAPI:: class.java)
        }
    }
}