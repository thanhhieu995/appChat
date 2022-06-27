package com.example.chatapp.accountLogin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.chatapp.R
import com.example.chatapp.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import kotlinx.android.synthetic.main.activity_log_in.*

class SignUp : AppCompatActivity() {

    private lateinit var edtName: EditText
    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnSigUp: Button

    private lateinit var mAuth: FirebaseAuth

    private lateinit var mDbRef: DatabaseReference

    var status: String? = ""

    var avatar: String? = null

    var hasMore: Boolean = false

    var isCalling: Boolean = false

    var acceptCall: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        supportActionBar?.hide()

        edtEmail = findViewById(R.id.edt_email_signUp)
        edtPassword = findViewById(R.id.edt_password_signUp)
        edtName = findViewById(R.id.edt_name_signUp)
       // avatar = findViewById(R.id.imgAva_main)

        mAuth = FirebaseAuth.getInstance()

        btn_signup.setOnClickListener {
            val email = edtEmail.text.toString().trim()
            val password = edtPassword.text.toString()
            val name = edtName.text.toString().trim()

            signUp(email, password, name, status.toString(), avatar, isCalling, acceptCall)
            hasMore = true
            checkUserExist(email)
        }
    }

    override fun onResume() {
        super.onResume()
        hasMore = true
    }

    private fun signUp(email: String, password: String, name: String, status: String, avatar: String?, isCalling: Boolean, acceptCall: Boolean) {
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(name)) {
            Toast.makeText(this@SignUp, "please fill all the fields", Toast.LENGTH_SHORT).show()
        } else {
            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        addUserToDatabase(name, email, mAuth.currentUser?.uid!!, status, avatar, isCalling, acceptCall)
                        val intent = Intent(this@SignUp, SetUpActivity::class.java)
                        intent.putExtra("uid", mAuth.currentUser?.uid)
                        startActivity(intent)
                        finish()
                    }
                }
        }
    }

    private fun addUserToDatabase(name: String, email: String, uid: String, status: String, avatar: String?, isCalling: Boolean, acceptCall: Boolean) {
        mDbRef = FirebaseDatabase.getInstance().reference

        mDbRef.child("user").child(uid).setValue(User(name, email, uid, status, avatar, isCalling, acceptCall))
    }

    private fun checkUserExist(email: String?) {
        mDbRef = FirebaseDatabase.getInstance().reference

        mDbRef.child("user").addValueEventListener(object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                for (postSnapshot in snapshot.children) {
                    val user = postSnapshot.getValue(User::class.java)
                    if (user != null) {
                        if (user.email == email && hasMore) {
                            Toast.makeText(this@SignUp, "Registered account", Toast.LENGTH_LONG).show()
                            hasMore = false
                        }
                    }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}