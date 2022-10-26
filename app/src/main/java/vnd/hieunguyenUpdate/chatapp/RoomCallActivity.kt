package vnd.hieunguyenUpdate.chatapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import java.net.URL

class RoomCallActivity : AppCompatActivity() {
    var txtRoomCode: TextView? = null
    var btnJoinRoom: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_room_call)

//        txtRoomCode = findViewById(R.id.txt_roomCode)
//        btnJoinRoom = findViewById(R.id.btnJoinRoom)

        val serverUrl: URL = URL("http://meet.jit.si")

//        val options = PeerConnectionFactory.InitializationOptions.builder(context)
//            .setEnableInternalTracer(true)
//            .setFieldTrials("WebRTC-H264HighProfile/Enabled/")
//            .createInitializationOptions()
//        PeerConnectionFactory.initialize(options)
    }
}