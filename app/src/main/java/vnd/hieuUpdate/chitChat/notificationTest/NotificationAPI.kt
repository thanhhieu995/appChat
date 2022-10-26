package vnd.hieuUpdate.chitChat.notificationTest

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.Headers
import retrofit2.http.POST
import vnd.hieuUpdate.chitChat.notificationTest.Constants.Companion.CONTENT_TYPE
import vnd.hieuUpdate.chitChat.notificationTest.Constants.Companion.SERVER_KEY

interface NotificationAPI {

    @Headers("Authorization: key=$SERVER_KEY", "Content-Type:$CONTENT_TYPE")
    @POST("fcm/send")
    suspend fun postNotification(@Body notification: PushNotification): Response<ResponseBody>
}