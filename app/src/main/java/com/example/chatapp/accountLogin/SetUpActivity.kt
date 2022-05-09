package com.example.chatapp.accountLogin

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Binder
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.ActivityResult
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.chatapp.R
import com.example.chatapp.main.MainActivity
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.UploadTask
import java.net.URI
import java.util.*


class SetUpActivity : AppCompatActivity() {

    private lateinit var btn_AddPhotto: Button
    private lateinit var btn_TakePhoto: Button
    private lateinit var btn_Continue : Button
    lateinit var img_Avatar: ImageView

    lateinit var mDbRef: DatabaseReference

    private lateinit var selectedImg: Uri

    lateinit var resultLauncher: ActivityResultLauncher<Intent>
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


        btn_TakePhoto.setOnClickListener {
            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            startActivityForResult(intent, 2)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val uid = intent.getStringExtra("uid")
        mDbRef = FirebaseDatabase.getInstance().reference
        if (data != null) {
            if (data.data != null) {
                selectedImg = data.data!!

                img_Avatar.setImageURI(selectedImg)
                if (uid != null) {
                   // mDbRef.child("user").child(uid).child("avatar").setValue(img_Avatar.toString())
                    //mDbRef.child("user").child(uid).child("avatar").setValue(selectedImg.toString())
                }
                //uploadImageToFirebase(selectedImg)
            }
        }

        if (resultCode == Activity.RESULT_OK && requestCode == 2 && data?.data != null) {
            img_Avatar.setImageBitmap(data.extras?.get("data") as Bitmap?)

            if (uid != null) {
                mDbRef.child("user").child(uid).child("avatar").setValue(data.extras?.get("data").toString())
            }
            selectedImg = data.data!!
            uploadImageToFirebase(selectedImg)
        }
    }

    fun uploadImageToFirebase(fileUri: Uri) {
        if (fileUri != null) {
            val fileName = UUID.randomUUID().toString() + ".jpg"
            val database = FirebaseDatabase.getInstance()
            val refStorage = FirebaseStorage.getInstance().reference.child("images/$fileName")

            refStorage.putFile(fileUri)
                .addOnSuccessListener(OnSuccessListener<UploadTask.TaskSnapshot> { taskSnapshot ->
                    taskSnapshot.storage.downloadUrl.addOnSuccessListener {
                        val imageUrl = it.toString()
                    }
                })
                .addOnFailureListener(OnFailureListener { e->
                    print(e.message)
                })
        }
    }
}