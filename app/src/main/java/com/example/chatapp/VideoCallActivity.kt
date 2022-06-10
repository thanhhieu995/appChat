package com.example.chatapp

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.MediaController
import android.widget.VideoView
import io.grpc.Context
import java.io.File

class VideoCallActivity : AppCompatActivity() {

    lateinit var btnOffVideo: Button
    lateinit var viewVideo: VideoView
    private var mediaController: MediaController? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_call)

        btnOffVideo = findViewById(R.id.btnTurnOffCall)
        viewVideo = findViewById(R.id.videoView)
        mediaController?.setAnchorView(viewVideo)
        viewVideo.setMediaController(mediaController)
        viewVideo.start()

    }

    
}