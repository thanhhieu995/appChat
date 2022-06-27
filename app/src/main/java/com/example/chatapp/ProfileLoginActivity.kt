package com.example.chatapp

import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.example.chatapp.accountLogin.SetUpActivity
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso

class ProfileLoginActivity : AppCompatActivity() {

    lateinit var imgAvatar: ImageView
    lateinit var txtName: TextView
    lateinit var userLogin: User
    
    lateinit var imgDialog: AlertDialog.Builder

    private val storeRef = FirebaseStorage.getInstance().reference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile_login)

        imgAvatar = findViewById(R.id.profile_login_imgAvatar)
        txtName = findViewById(R.id.profile_login_txtName)

        userLogin = intent.getSerializableExtra("userLogin") as User
        txtName.text = userLogin.name

        if (userLogin.uid != null) {
            storeRef.child("images").child(userLogin.uid.toString()).downloadUrl.addOnSuccessListener {
                Picasso.get().load(it).into(imgAvatar)
            }
        }
        
        imgAvatar.setOnClickListener {
            popUp()
        }
    }
    
    private fun popUp() {
        imgDialog = AlertDialog.Builder(this@ProfileLoginActivity)
        imgDialog.setPositiveButton("view image profile", DialogInterface.OnClickListener{dialog, which ->  
            val intent = Intent(this@ProfileLoginActivity, ViewProfileActivity::class.java)
            intent.putExtra("uid", userLogin.uid)
            startActivity(intent)
        })

        imgDialog.setNegativeButton("Edit image profile", DialogInterface.OnClickListener{dialog, which ->
            val intent = Intent(this@ProfileLoginActivity, SetUpActivity::class.java)
            intent.putExtra("uid", userLogin.uid)
            startActivity(intent)
        })

        imgDialog.show()
    }
}