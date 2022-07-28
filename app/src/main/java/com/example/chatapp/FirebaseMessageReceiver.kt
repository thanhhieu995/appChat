package com.example.chatapp

import android.R
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

    private fun sendNotification(title: String? , body: String?) {
        var intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val builder: NotificationCompat.Builder? = NotificationCompat.Builder(this)
            .setSmallIcon(R.drawable.stat_notify_chat) //set icon for notification
            .setContentTitle(title) //set title of notification
            .setContentText(body) //this is notification message
            .setAutoCancel(true) // makes auto cancel of notification
            .setPriority(Notification.PRIORITY_DEFAULT) //set priority of notification
        val notificationIntent = Intent(this, MainActivity::class.java)
        notificationIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        //notification message will get at NotificationView
        notificationIntent.putExtra("message", body)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, notificationIntent,
            PendingIntent.FLAG_UPDATE_CURRENT
        )
        builder?.setContentIntent(pendingIntent)
        val manager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        manager.notify(0, builder?.build())

        val channelId: String = getString(R.string.status_bar_notification_info_overflow)
        var defaultSoundUri: Uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
        val notificationManager: NotificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            var channel : NotificationChannel = NotificationChannel(channelId, "Channel human readable title", NotificationManager.IMPORTANCE_DEFAULT)
            notificationManager.createNotificationChannel(channel)
        }
        if (builder != null) {
            notificationManager.notify(0, builder.build())
        }
    }
}