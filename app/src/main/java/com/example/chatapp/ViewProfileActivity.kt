package com.example.chatapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class ViewProfileActivity : AppCompatActivity() {

    private val storeRef = FirebaseStorage.getInstance().reference
    var uid: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_profile)

        val imageProfile: ImageView = findViewById(R.id.imgProfileFull)
        uid = intent.getSerializableExtra("uid") as String?
        storeRef.child("images").child(uid!!).downloadUrl.addOnSuccessListener {
            Picasso.get().load(it).into(imageProfile)
        }
    }
}