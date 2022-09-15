package com.example.chatapp.notification

import com.example.chatapp.notification.Constants.Companion.CONTENT_TYPE
import com.example.chatapp.notification.Constants.Companion.SERVER_KEY
import okhttp3.ResponseBody
import retrofit.Response
import retrofit.http.Body
import retrofit.http.Headers
import retrofit.http.POST

interface NotificationAPI {

    @Headers("Authorization: key=$SERVER_KEY", "Content-Type:$CONTENT_TYPE")
    @POST("fcm/send")
    fun postNotification(@Body notification: PushNotification): Response<ResponseBody>
}