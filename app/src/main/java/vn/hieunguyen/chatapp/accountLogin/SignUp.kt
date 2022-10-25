package vn.hieunguyen.chatapp.accountLogin

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import com.google.firebase.iid.FirebaseInstanceId
import kotlinx.android.synthetic.main.activity_log_in.*
import vn.hieunguyen.chatapp.R
import vn.hieunguyen.chatapp.Unread
import vn.hieunguyen.chatapp.User


class SignUp : AppCompatActivity() {

    private lateinit var edtName: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnSigUp: Button
    lateinit var imgEye: ImageView
    var isClicked: Boolean = true

    private lateinit var mAuth: FirebaseAuth

    private lateinit var mDbRef: DatabaseReference

    var status: String? = ""

    var avatar: String? = null

    var hasMore: Boolean = false

    var isCalling: Boolean = false

    var isTyping: Boolean = false

    var showTyping: Boolean = false

    var count: Int = 0

    var fromUid: String = ""

    var lastMsg: String = ""

    var acceptCall: Boolean = false

    var listToken: ArrayList<String>? = ArrayList()

    var room: String? = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        supportActionBar?.hide()

        edtEmail = findViewById(R.id.su_edt_email)
        edtPassword = findViewById(R.id.su_edt_password)
        edtName = findViewById(R.id.su_edt_name)
        btnSigUp = findViewById(R.id.su_btn_signup)
        imgEye = findViewById(R.id.su_eye_pass)
        // avatar = findViewById(R.id.imgAva_main)

//        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener(this
//        ) { instanceIdResult ->
//           val newToken = instanceIdResult.token
//            if (listToken?.contains(newToken) == false|| listToken!!.isEmpty()) {
//                listToken!!.add(newToken)
//            }
//        }

        mAuth = FirebaseAuth.getInstance()

        btnSigUp.setOnClickListener {
            val email = edtEmail.text.toString().trim()
            val password = edtPassword.text.toString()
            val name = edtName.text.toString().trim()

            FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener { instanceIdResult ->
                val newToken = instanceIdResult.token
                if (listToken?.contains(newToken) == false || listToken!!.isEmpty()) {
                    listToken!!.add(newToken)
                }
            }

            listToken?.let { it1 ->
                room?.let { it2 ->
                    signUp(
                        email, password, name, status.toString(), avatar, isCalling, acceptCall, isTyping, showTyping, count, fromUid, lastMsg,
                        it1,
                        it2
                    )
                }
            }
            hasMore = true
            checkUserExist(email)
        }

        imgEye.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if (isClicked) {
                    imgEye.setImageResource(R.drawable.ic_baseline_visibility_24)
                    edtPassword.transformationMethod = HideReturnsTransformationMethod.getInstance()
                } else {
                    imgEye.setImageResource(R.drawable.ic_baseline_visibility_off_24)
                    edtPassword.transformationMethod = PasswordTransformationMethod.getInstance()
                }
                isClicked = !isClicked
            }

        })
    }

    override fun onResume() {
        super.onResume()
        hasMore = true
    }

    private fun signUp(
        email: String,
        password: String,
        name: String,
        status: String,
        avatar: String?,
        isCalling: Boolean,
        acceptCall: Boolean,
        isTyping: Boolean,
        showTyping: Boolean,
        count: Int,
        fromUid: String,
        lastMsg: String,
        listToken: ArrayList<String>,
        room: String
    ) {
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(name)) {
            Toast.makeText(this@SignUp, "please fill all the fields", Toast.LENGTH_SHORT).show()
        } else {
            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        addUserToDatabase(
                            name,
                            email,
                            mAuth.currentUser?.uid!!,
                            status,
                            avatar,
                            isCalling,
                            acceptCall,
                            isTyping,
                            showTyping,
                            count,
                            fromUid,
                            lastMsg,
                            listToken,
                            room
                        )
                        addUnreadToDataBase()
                        val intent = Intent(this@SignUp, SetUpActivity::class.java)
                        intent.putExtra("uid", mAuth.currentUser?.uid)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(this@SignUp, task.exception.toString(), Toast.LENGTH_LONG).show()
                    }
                }
        }
    }

    private fun addUserToDatabase(
        name: String,
        email: String,
        uid: String,
        status: String,
        avatar: String?,
        isCalling: Boolean,
        acceptCall: Boolean,
        isTyping: Boolean,
        showTyping: Boolean,
        count: Int,
        fromUid: String,
        lastMsg: String,
        listToken: ArrayList<String>,
        room: String
    ) {
        mDbRef = FirebaseDatabase.getInstance().reference

        mDbRef.child("user").child(uid)
            .setValue(User(name, email, uid, status, avatar, isCalling, acceptCall, isTyping, showTyping, count, fromUid, lastMsg,listToken, room))
    }

    private fun checkUserExist(email: String?) {
        mDbRef = FirebaseDatabase.getInstance().reference

        mDbRef.child("user").addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                for (postSnapshot in snapshot.children) {
                    val user = postSnapshot.getValue(User::class.java)
                    if (user != null) {
                        if (user.email == email && hasMore) {
                            Toast.makeText(this@SignUp, "Account is registered", Toast.LENGTH_LONG)
                                .show()
                            hasMore = false
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@SignUp, error.message, Toast.LENGTH_LONG).show()
            }
        })
    }

    fun addUnreadToDataBase() {
        mDbRef.child("unRead").child(mAuth.uid.toString()).setValue(Unread(0, "", ""))
    }
}