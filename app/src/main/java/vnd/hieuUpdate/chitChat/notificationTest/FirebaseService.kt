package vnd.hieuUpdate.chitChat.notificationTest

import android.app.*
import android.app.NotificationManager.IMPORTANCE_HIGH
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.Color
import android.os.Build
import android.preference.PreferenceManager
import android.util.Log
import androidx.annotation.RequiresApi
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.google.gson.Gson
import retrofit2.Retrofit
import vnd.hieuUpdate.chitChat.R
import vnd.hieuUpdate.chitChat.User
import vnd.hieuUpdate.chitChat.chat.ChatActivity
import kotlin.random.Random


private const val CHANNEL_ID = "my_channel"

class FirebaseService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        Log.d(tag, "token: $token")
        super.onNewToken(token)
    }

    val channelId = "notification_channel"
    val channelName = "vnd.hieuUpdate.chitChat"

    var tag: String = "FirebaseMessageReceiver"
    lateinit var sharedPreferences: SharedPreferences
    @RequiresApi(Build.VERSION_CODES.M)
    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        //super.onMessageReceived(remoteMessage)
        val KEY_TEXT_REPLY = "Key text"
//        val remoteInput: RemoteInput = Retrofit.Builder(KEY_TEXT_REPLY).setLabel(replyLabel).build()
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        val intent = Intent(this, ChatActivity::class.java)

//        intent.putExtra("remoteMessage", remoteMessage)

        val notificationManager: NotificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        val notificationID = Random.nextInt()

        //val userLogin = remoteMessage.data["userLogin"]

        intent.putExtra("userLogin", remoteMessage.data["userLogin"])
        Log.d("userLoginRemote", remoteMessage.data["userLogin"].toString())
        intent.putExtra("userFriend", remoteMessage.data["userFriend"])
        intent.putExtra("hasMore", remoteMessage.data["hasMore"].toBoolean())
        intent.putExtra("roomSenderService", remoteMessage.data["roomSender"])

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
//                .addAction(R.drawable.send, "send", pendingIntent)
            .setAutoCancel(true)
            .build()
    }
        with(NotificationManagerCompat.from(this)) {
            val isActive = sharedPreferences.getBoolean("isActive", false)
            Log.d("isActive", isActive.toString())
            val roomSender = sharedPreferences.getString("roomSender", "")
            Log.d("roomSender", roomSender.toString())
            val userLogin = parseJSON(remoteMessage.data["userLogin"] as String)
            val userFriend = parseJSON(remoteMessage.data["userFriend"] as String)
            val roomRemoteLogin = userLogin.uid + userFriend.uid
            Log.d("roomRemoteLogin", roomRemoteLogin)
            val roomRemoteReceive = userFriend.uid + userLogin.uid
            Log.d("roomRemoteReceive", roomRemoteReceive)
            if (roomRemoteReceive != roomSender && roomRemoteLogin != roomSender) {
                Log.d("room different", "true")
//                if (!isActive) {
                    Log.d("notify here", "true")
                    notify(notificationID, notification.build())
//                }
            }
        }

//        notificationManager.notify(notificationID, notification)
    }

    private fun parseJSON(jsonResponse: String): User {
        return Gson().fromJson(jsonResponse, User::class.java)
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