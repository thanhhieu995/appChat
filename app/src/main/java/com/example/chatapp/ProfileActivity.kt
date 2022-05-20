package com.example.chatapp

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.example.chatapp.main.MainActivity
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.io.Serializable

class ProfileActivity : AppCompatActivity() {

    val storeRef = FirebaseStorage.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        val imgProfile: ImageView = findViewById(R.id.profile_image)
        //val user: Serializable? = intent.getSerializableExtra("user")
        val uid: Serializable? = intent.getSerializableExtra("uid")
        if (uid != null) {
            storeRef.child("images").child(uid as String).downloadUrl.addOnSuccessListener {
                Picasso.get().load(it).into(imgProfile)
            }
        }
    }

    override fun onBackPressed() {
        val intent = Intent(this@ProfileActivity, MainActivity::class.java)
        startActivity(intent)
        super.onBackPressed()
    }
}