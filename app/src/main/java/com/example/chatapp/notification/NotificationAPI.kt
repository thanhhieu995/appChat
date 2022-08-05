package com.example.chatapp.notification

import okhttp3.ResponseBody
import retrofit.Response
import retrofit.http.Body
import retrofit.http.Headers
import retrofit.http.POST

abstract class NotificationAPI {

//    suspend fun postNotification(
//        @Body notification: PushNotification
//    )
//    suspend fun postNotification()

//    @Headers("Autorization: key=$SERVER_KEY", "ContentType: $CONTENT_TYPE")
    @POST("fcm/send")

    abstract suspend fun postNotification(@Body notification: PushNotification)
    : Response<ResponseBody>
}