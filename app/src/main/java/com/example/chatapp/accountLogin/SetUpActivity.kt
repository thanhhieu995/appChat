package com.example.chatapp.accountLogin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import com.example.chatapp.R
import com.example.chatapp.main.MainActivity

class SetUpActivity : AppCompatActivity() {

    private lateinit var btn_AddPhotto: Button
    private lateinit var btn_TakePhoto: Button
    private lateinit var btn_Continue : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_up)

        btn_AddPhotto = findViewById(R.id.btn_AddPicture)
        btn_TakePhoto = findViewById(R.id.btn_TakePhoto)
        btn_Continue = findViewById(R.id.btn_Continue_Setup)

        btn_Continue.setOnClickListener {
            val intent = Intent(this@SetUpActivity, MainActivity::class.java)
            startActivity(intent)
        }
    }
}