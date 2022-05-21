package com.example.chatapp

import android.app.Dialog
import android.app.SearchManager
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import com.example.chatapp.main.MainActivity
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import java.io.Serializable

class ProfileActivity : AppCompatActivity() {

    val storeRef = FirebaseStorage.getInstance().reference
    private lateinit var imageDialog: AlertDialog.Builder
    private lateinit var imgProfile: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        imgProfile = findViewById(R.id.profile_image)
        //val user: Serializable? = intent.getSerializableExtra("user")
        val uid: Serializable? = intent.getSerializableExtra("uid")
        if (uid != null) {
            storeRef.child("images").child(uid as String).downloadUrl.addOnSuccessListener {
                Picasso.get().load(it).into(imgProfile)
            }
        }
        imgProfile.setOnClickListener(View.OnClickListener {
            popUp()
        })


    }

    private fun popUp() {
        imageDialog = AlertDialog.Builder(this)
        val layout: View = View.inflate(this, R.layout.popup, null)
        imageDialog.setView(layout)
        imageDialog.create()
        imageDialog.show()
    }

    override fun onBackPressed() {
        val intent = Intent(this@ProfileActivity, MainActivity::class.java)
        startActivity(intent)
        super.onBackPressed()
    }
}