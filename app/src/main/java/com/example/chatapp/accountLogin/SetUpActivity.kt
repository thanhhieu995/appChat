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
import com.example.chatapp.main.MainActivity
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
        if (resultCode == Activity.RESULT_OK && requestCode == 1 && data != null) {
            if (data.data != null) {
                selectedImg = data.data!!

                img_Avatar.setImageURI(selectedImg)

                uploadImageTest(selectedImg)
//                if (uid != null) {
//                   // mDbRef.child("user").child(uid).child("avatar").setValue(img_Avatar.toString())
//                    //mDbRef.child("user").child(uid).child("avatar").setValue(selectedImg.toString())
//                }
                //uploadImageToFirebase(selectedImg)
            }
        }

        if (resultCode == Activity.RESULT_OK && requestCode == 2 && data != null) {
            img_Avatar.setImageBitmap(data.extras?.get("data") as Bitmap?)

//            if (uid != null) {
//                //mDbRef.child("user").child(uid).child("avatar").setValue(data.extras?.get("data").toString())
//            }
//            if (data.data != null) {
//                selectedImg = data.data!!
//                uploadImageTest(selectedImg)
//                //img_Avatar.setImageBitmap(data.data)
//            } else {
//                Toast.makeText(this, "No record", Toast.LENGTH_LONG).show()
//            }
            val takePhoto: Bitmap? = data.extras?.get("data") as Bitmap?

//            if (takePhoto != null) {
//                upImageTakeToFirebase(takePhoto)
//            }

            if (takePhoto != null) {
                onCaptureImageResult(takePhoto)
            }
            //uploadImageToFirebase(selectedImg)

            //uploadImageTest(selectedImg)
        }
        //uploadImageTest(selectedImg)
    }

    fun onCaptureImageResult(takePhoto: Bitmap) {
        val bytes : ByteArrayOutputStream = ByteArrayOutputStream()
        takePhoto.compress(Bitmap.CompressFormat.JPEG, 90, bytes)
        val bb: ByteArray = bytes.toByteArray()
        val file: String = Base64.encodeToString(bb, Base64.DEFAULT)

        val storageRef: StorageReference = storage.reference
        val imagesRef = storageRef.child("images/${UUID.randomUUID()}")
        imagesRef.putBytes(bb)
    }

//    fun upImageTakeToFirebase(takePhoto: Bitmap) {
//        val baos : ByteArrayOutputStream = ByteArrayOutputStream()
//        takePhoto.compress(Bitmap.CompressFormat.PNG, 100, baos)
//        //val imageEncoded: String = Base64.(baos.toByteArray(), Base64.DEFAULT)
//        //val imageEncode: String =
//    }

//    fun upLoadImageCapture(takePhoto: Bitmap) {
//        val storageRef = storage.reference
//        val imagesRef = storageRef.child("images/${UUID.randomUUID()}")
//        //val uploadTask = imagesRef.putBytes(takePhoto.compress())
//    }


    val storage = Firebase.storage
    private fun uploadImageTest(imageURI: Uri) {
        val storageRef = storage.reference
        val imagesRef = storageRef.child("images/${UUID.randomUUID()}")
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



//    fun uploadImageToFirebase(fileUri: Uri) {
//        if (fileUri != null) {
//            val fileName = UUID.randomUUID().toString() + ".jpg"
//            val database = FirebaseDatabase.getInstance()
//            val refStorage = FirebaseStorage.getInstance().reference.child("images/$fileName")
//
//            refStorage.putFile(fileUri)
//                .addOnSuccessListener(OnSuccessListener<UploadTask.TaskSnapshot> { taskSnapshot ->
//                    taskSnapshot.storage.downloadUrl.addOnSuccessListener {
//                        //val imageUrl = it.toString()
//                    }
//                })
//                .addOnFailureListener(OnFailureListener { e->
//                    print(e.message)
//                })
//        }
//    }
}