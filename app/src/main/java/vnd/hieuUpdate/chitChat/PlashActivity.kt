package vnd.hieuUpdate.chitChat

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import vnd.hieuUpdate.chitChat.R
import vnd.hieuUpdate.chitChat.accountLogin.LogIn

class PlashActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_plash)

        val handler = Handler(Looper.getMainLooper())
        handler.postDelayed(Runnable {
            LogIn.start(this) }, 500)
    }
}