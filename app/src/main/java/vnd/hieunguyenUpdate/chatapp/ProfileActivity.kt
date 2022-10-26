package vnd.hieunguyenUpdate.chatapp

import android.annotation.SuppressLint
import android.content.DialogInterface
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import com.google.firebase.storage.FirebaseStorage
import com.squareup.picasso.Picasso
import vnd.hieunguyenUpdate.chatapp.accountLogin.SetUpActivity
import vnd.hieunguyenUpdate.chatapp.chat.ChatActivity

class ProfileActivity : AppCompatActivity() {

    private val storeRef = FirebaseStorage.getInstance().reference
    private lateinit var imageDialog: AlertDialog.Builder
    private lateinit var imgProfile: ImageView
    private var loginUid: String? = ""
    var friendUid: String? = ""
    var hasMore: Boolean = false
    lateinit var userLogin: vnd.hieunguyenUpdate.chatapp.User
    lateinit var userFriend: vnd.hieunguyenUpdate.chatapp.User

    lateinit var userName: TextView

    @SuppressLint("ResourceType")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)

        imgProfile = findViewById(R.id.profile_image)
        userName = findViewById(R.id.username)
        //val user: Serializable? = intent.getSerializableExtra("user")
        loginUid = intent.getSerializableExtra("uidLogin") as String?
        friendUid = intent.getSerializableExtra("uidFriend") as String?
        hasMore = intent.getSerializableExtra("hasMore") as Boolean
        userLogin = intent.getSerializableExtra("userLogin") as vnd.hieunguyenUpdate.chatapp.User
        userFriend = intent.getSerializableExtra("userFriend") as vnd.hieunguyenUpdate.chatapp.User

        userName.text = userFriend.name

        if (friendUid != null) {
            storeRef.child("images").child(friendUid!!).downloadUrl.addOnSuccessListener {
                Picasso.get().load(it).into(imgProfile)
            }
        }
        imgProfile.setOnClickListener(View.OnClickListener {
            //popUp()
            val intent = Intent(this@ProfileActivity, vnd.hieunguyenUpdate.chatapp.ViewProfileActivity::class.java)
            intent.putExtra("uid", friendUid)
            startActivity(intent)
        })
    }

    override fun onResume() {
        super.onResume()
        storeRef.child("images").child(friendUid!!).downloadUrl.addOnSuccessListener {
            Picasso.get().load(it).into(imgProfile)
        }
    }

    private fun popUp() {
        imageDialog = AlertDialog.Builder(this@ProfileActivity)
        //val layout: View = View.inflate(this, R.layout.popup, null)
        //imageDialog.setTitle("Choose an item")
        //imageDialog.setMessage("What will you choose?")
        //imageDialog.setView(layout)

        val listItems = arrayOf("show image profile", "change avatar")
        var selected = 0

        imageDialog.setPositiveButton("view image profile", DialogInterface.OnClickListener { dialog, which ->
            //Toast.makeText(this, "success", Toast.LENGTH_LONG).show()
            val intent = Intent(this@ProfileActivity, vnd.hieunguyenUpdate.chatapp.ViewProfileActivity::class.java)
            intent.putExtra("uid", friendUid)
            startActivity(intent)
        })

        imageDialog.setNegativeButton("Edit image profile", DialogInterface.OnClickListener { dialog, which ->
            //Toast.makeText(this, "edit", Toast.LENGTH_LONG).show()
            val intent = Intent(this@ProfileActivity, SetUpActivity::class.java)
            intent.putExtra("uid", friendUid)
            startActivity(intent)
        })

        //imageDialog.create()
        imageDialog.show()
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val intent = Intent(this@ProfileActivity, ChatActivity::class.java)
        intent.putExtra("uidLogin", loginUid)
        intent.putExtra("uidFriend", friendUid)
        intent.putExtra("hasMore", hasMore)
        intent.putExtra("userLogin", userLogin)
        intent.putExtra("userFriend", userFriend)
        startActivity(intent)
        finish()
    }
}