package com.example.chatapp

import android.annotation.SuppressLint
import android.app.Dialog
import android.app.SearchManager
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.graphics.toColorInt
import com.example.chatapp.main.MainActivity
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.popup.*
import java.io.Serializable

class ProfileActivity : AppCompatActivity() {

    private val storeRef = FirebaseStorage.getInstance().reference
    private lateinit var imageDialog: AlertDialog.Builder
    private lateinit var imgProfile: ImageView
    private var uid: String? = ""

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        imgProfile = findViewById(R.id.profile_image)
        //val user: Serializable? = intent.getSerializableExtra("user")
        uid = intent.getSerializableExtra("uid") as String?
        if (uid != null) {
            storeRef.child("images").child(uid!!).downloadUrl.addOnSuccessListener {
                Picasso.get().load(it).into(imgProfile)
            }
        }
        imgProfile.setOnClickListener(View.OnClickListener {
            popUp()
        })
    }

    private fun popUp() {
        imageDialog = AlertDialog.Builder(this@ProfileActivity)
        val layout: View = View.inflate(this, R.layout.popup, null)
        //imageDialog.setTitle("Choose an item")
        //imageDialog.setMessage("What will you choose?")
        //imageDialog.setView(layout)

        val listItems = arrayOf("show image profile", "change avatar")
        var selected = 0

        imageDialog.setPositiveButton("view image profile", DialogInterface.OnClickListener { dialog, which ->
            //Toast.makeText(this, "success", Toast.LENGTH_LONG).show()
            val intent = Intent(this@ProfileActivity, ViewProfileActivity::class.java)
            intent.putExtra("uid", uid)
            startActivity(intent)
        })

        imageDialog.setNegativeButton("Edit image profile", DialogInterface.OnClickListener { dialog, which ->
            Toast.makeText(this, "edit", Toast.LENGTH_LONG).show()
        })

        //imageDialog.create()
        imageDialog.show()
    }

    override fun onBackPressed() {
        val intent = Intent(this@ProfileActivity, MainActivity::class.java)
        startActivity(intent)
        super.onBackPressed()
    }
}