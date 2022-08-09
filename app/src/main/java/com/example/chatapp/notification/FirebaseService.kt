package com.example.chatapp.notification

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import com.example.chatapp.R
import android.app.PendingIntent
import android.app.PendingIntent.FLAG_ONE_SHOT
import android.content.ContentValues.TAG
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import com.example.chatapp.chat.ChatActivity
import com.example.chatapp.main.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import com.squareup.okhttp.Dispatcher
import java.lang.Exception
import kotlin.coroutines.CoroutineContext
import kotlin.random.Random

private const val CHANNEL_ID = "my_channel"

class FirebaseService : FirebaseMessagingService() {

    val channelId = "notification_channel"
    val channelName = "com.example.chatapp"



    var tag: String = "FirebaseMessageReceiver"
    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        super.onMessageReceived(remoteMessage)

        val intent = Intent(this, ChatActivity::class.java)
        val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationID = Random.nextInt()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this, 0,
            intent, FLAG_ONE_SHOT)

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setContentTitle(remoteMessage.data["title"])
            .setContentText(remoteMessage.data["message"])
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

    private fun getRemoteView(title: String?, message: String?): RemoteViews? {
        val remoteView = RemoteViews("com.example.chatapp", R.layout.notification)

        remoteView.setTextViewText(R.id.title_00, title)
        remoteView.setTextViewText(R.id.message, message)
        remoteView.setImageViewResource(R.id.app_logo, R.drawable.chatlogo)

        return remoteView
    }
}