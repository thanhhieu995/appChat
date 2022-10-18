package vn.hieunguyen.chatapp.notificationTest

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import vn.hieunguyen.chatapp.R
import vn.hieunguyen.chatapp.chat.ChatActivity
import kotlin.random.Random


private const val CHANNEL_ID = "my_channel"

class FirebaseService : FirebaseMessagingService() {

    val channelId = "notification_channel"
    val channelName = "com.example.chatapp"

    var tag: String = "FirebaseMessageReceiver"
    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        //super.onMessageReceived(remoteMessage)
        val intent = Intent(this, ChatActivity::class.java)

//        intent.putExtra("remoteMessage", remoteMessage)

        val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationID = Random.nextInt()

        //val userLogin = remoteMessage.data["userLogin"]

        intent.putExtra("userLogin", remoteMessage.data["userLogin"])
        intent.putExtra("userFriend", remoteMessage.data["userFriend"])
        intent.putExtra("hasMore", remoteMessage.data["hasMore"].toBoolean())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
        } else {
            TODO("VERSION.SDK_INT < M")
        }
        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
//            .setContentTitle(remoteMessage.data["title"])
//            .setContentText(remoteMessage.data["message"])
            .setContentTitle(remoteMessage.data["title"])
            .setContentText(remoteMessage.data["body"])
            .setSmallIcon(R.drawable.chatlogo)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(notificationID, notification)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel(notificationManager: NotificationManager) {
        val channelName = "channelName"
        val channel = NotificationChannel(CHANNEL_ID, channelName, IMPORTANCE_HIGH).apply {
            description = "My channel description"
            enableLights(true)
            lightColor = Color.GREEN
        }
        notificationManager.createNotificationChannel(channel)
    }
}