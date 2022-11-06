package vnd.hieuUpdate.chitChat.notificationTest

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.app.PendingIntent
import android.app.TaskStackBuilder
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import vnd.hieuUpdate.chitChat.R
import vnd.hieuUpdate.chitChat.chat.ChatActivity
import kotlin.random.Random


private const val CHANNEL_ID = "my_channel"

class FirebaseService : FirebaseMessagingService() {

    val channelId = "notification_channel"
    val channelName = "vnd.hieuUpdate.chitChat"

    var tag: String = "FirebaseMessageReceiver"
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        //super.onMessageReceived(remoteMessage)
        val intent = Intent(this, ChatActivity::class.java)

//        intent.putExtra("remoteMessage", remoteMessage)

        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationID = Random.nextInt()

        //val userLogin = remoteMessage.data["userLogin"]

        intent.putExtra("userLogin", remoteMessage.data["userLogin"])
        intent.putExtra("userFriend", remoteMessage.data["userFriend"])
        intent.putExtra("hasMore", remoteMessage.data["hasMore"].toBoolean())

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel(notificationManager)
        }

        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

//        val pendingIntent = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//            PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)
//        } else {
//            TODO("VERSION.SDK_INT < M")
//        }
        val pendingIntent: PendingIntent? = TaskStackBuilder.create(this).run {
            addNextIntentWithParentStack(intent)
            getPendingIntent(0, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE)
        }

        val notification = NotificationCompat.Builder(this, CHANNEL_ID).apply {
//            .setContentTitle(remoteMessage.data["title"])
//            .setContentText(remoteMessage.data["message"])
            setContentIntent(pendingIntent)
            .setContentTitle(remoteMessage.data["title"])
            .setContentText(remoteMessage.data["body"])
            .setSmallIcon(R.mipmap.ic_launcher_foreground)
            .setColor(ContextCompat.getColor(this@FirebaseService, R.color.green))
//            .setChannelId(channelId)
            .setAutoCancel(true)
            .build()
    }
        with(NotificationManagerCompat.from(this)) {
            notify(notificationID, notification.build())
        }

//        notificationManager.notify(notificationID, notification)
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