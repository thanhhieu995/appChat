package com.example.chatapp

import com.example.chatapp.R
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.media.RingtoneManager
import android.net.Uri
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.chatapp.main.MainActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FirebaseMessageReceiver : FirebaseMessagingService() {

    val channelId = "notification_channel"
    val channelName = "com.example.chatapp"


    var tag: String = "FirebaseMessageReceiver"
    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        if (remoteMessage.notification != null) {
            val title : String = remoteMessage.notification?.title!!
            val body : String = remoteMessage.notification?.body!!
            Log.d(tag, "Message Notification Title: $title")
            Log.d(tag, "Message Notification Body: $body")
            sendNotification(title, body)
        }
    }

    private fun getRemoteView(title: String?, message: String?): RemoteViews? {
        val remoteView = RemoteViews("com.example.chatapp", R.layout.notification)

        remoteView.setTextViewText(R.id.title_00, title)
        remoteView.setTextViewText(R.id.message, message)
        remoteView.setImageViewResource(R.id.app_logo, R.drawable.chatlogo)

        return remoteView
    }

    private fun sendNotification(title: String? , message: String?) {
        var intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        var builder: NotificationCompat.Builder = NotificationCompat.Builder(applicationContext, channelId)
            .setSmallIcon(R.drawable.chatlogo)
            .setAutoCancel(true)
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000))
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)
        
        builder = builder.setContent(message?.let { getRemoteView(title, it) })
    }
    
}