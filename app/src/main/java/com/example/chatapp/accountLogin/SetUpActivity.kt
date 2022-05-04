package com.example.chatapp.accountLogin

import android.content.Intent
import android.net.Uri
import android.os.Binder
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity
import com.example.chatapp.R
import com.example.chatapp.main.MainActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase


class SetUpActivity : AppCompatActivity() {

    private lateinit var btn_AddPhotto: Button
    private lateinit var btn_TakePhoto: Button
    private lateinit var btn_Continue : Button
    lateinit var img_Avatar: ImageView

    lateinit var mDbRef: DatabaseReference

    private lateinit var selectedImg: Uri

    //private lateinit var binding: ActivityProfileBinding
    //private lateinit var binding: Binder
    private val PICK_IMAGE: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_up)


        btn_AddPhotto = findViewById(R.id.btn_AddPicture)
        btn_TakePhoto = findViewById(R.id.btn_TakePhoto)
        btn_Continue = findViewById(R.id.btn_Continue_Setup)
        img_Avatar = findViewById(R.id.img_setUpAvatar)

        btn_Continue.setOnClickListener {
            val intent = Intent(this@SetUpActivity, MainActivity::class.java)
            startActivity(intent)
        }

        btn_AddPhotto.setOnClickListener {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            startActivityForResult(Intent.createChooser(intent, "Select picture"), 1)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (data != null) {
            if (data.data != null) {
                selectedImg = data.data!!

                img_Avatar.setImageURI(selectedImg)

                val uid = intent.getStringExtra("uid")
                mDbRef = FirebaseDatabase.getInstance().reference
                if (uid != null) {
                   // mDbRef.child("user").child(uid).child("avatar").setValue(img_Avatar.toString())
                    mDbRef.child("user").child(uid).child("avatar").setValue(selectedImg.toString())
                }
            }
        }
    }
}