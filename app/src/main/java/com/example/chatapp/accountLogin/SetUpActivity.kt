package com.example.chatapp.accountLogin

import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.util.Base64
import android.util.Base64.encode
import android.util.Base64.encodeToString
import android.widget.Button
import android.widget.ImageView
import androidx.activity.result.ActivityResultLauncher
import androidx.appcompat.app.AppCompatActivity
import com.example.chatapp.R
import com.example.chatapp.User
import com.example.chatapp.main.MainActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase
import com.google.firebase.storage.StorageReference
import com.google.firebase.storage.ktx.storage
import java.io.ByteArrayOutputStream
import java.util.*


class SetUpActivity : AppCompatActivity() {

    private lateinit var btn_AddPhotto: Button
    private lateinit var btn_TakePhoto: Button
    private lateinit var btn_Continue : Button
    lateinit var img_Avatar: ImageView

    lateinit var mDbRef: DatabaseReference

    private lateinit var selectedImg: Uri

    private lateinit var user: User

    private lateinit var mAuth: FirebaseAuth

    private lateinit var uid: String

    lateinit var resultLauncher: ActivityResultLauncher<Intent>
    private val PICK_IMAGE: Int = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_set_up)


        btn_AddPhotto = findViewById(R.id.btn_AddPicture)
        btn_TakePhoto = findViewById(R.id.btn_TakePhoto)
        btn_Continue = findViewById(R.id.btn_Continue_Setup)
        img_Avatar = findViewById(R.id.img_setUpAvatar)

        uid = intent.getStringExtra("uid").toString()

        btn_Continue.setOnClickListener {
            val intent = Intent(this@SetUpActivity, MainActivity::class.java)
            //intent.putExtra("name", mAuth.currentUser?.displayName)
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
        if (resultCode == Activity.RESULT_OK && requestCode == 1 && data != null) {
            if (data.data != null) {
                selectedImg = data.data!!

                img_Avatar.setImageURI(selectedImg)

                uploadImageTest(selectedImg)
            }
        }

        if (resultCode == Activity.RESULT_OK && requestCode == 2 && data != null) {
            img_Avatar.setImageBitmap(data.extras?.get("data") as Bitmap?)

            val takePhoto: Bitmap? = data.extras?.get("data") as Bitmap?

            if (takePhoto != null) {
                onCaptureImageResult(takePhoto)
            }
        }
    }

    fun onCaptureImageResult(takePhoto: Bitmap) {
        val bytes : ByteArrayOutputStream = ByteArrayOutputStream()
        takePhoto.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
        val bb: ByteArray = bytes.toByteArray()
        val file: String = Base64.encodeToString(bb, Base64.DEFAULT)

        val storageRef: StorageReference = storage.reference
        val imagesRef = storageRef.child("images")
            .child(uid)
        imagesRef.putBytes(bb)
    }


    val storage = Firebase.storage
    private fun uploadImageTest(imageURI: Uri) {
        val storageRef = storage.reference
        val imagesRef = storageRef.child("images")
            .child(uid)
        val uploadTask = imagesRef.putFile(imageURI)

        uploadTask.addOnFailureListener {

        }.addOnSuccessListener {

        }
    }

    private fun upTakePhoto(bitmap: Bitmap) {
        val storageRef = storage.reference
        val imagesRef = storageRef.child("images/${UUID.randomUUID()}")
        img_Avatar.isDrawingCacheEnabled
        img_Avatar.buildDrawingCache()
        val bitmap1: Bitmap = img_Avatar.getDrawingCache()
        val baos: ByteArrayOutputStream = ByteArrayOutputStream()
        bitmap1.setHasMipMap(true)
    }
}