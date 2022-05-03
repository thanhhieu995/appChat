package com.example.chatapp.accountLogin

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.chatapp.main.MainActivity
import com.example.chatapp.R
import com.example.chatapp.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        supportActionBar?.hide()

        edtEmail = findViewById(R.id.edt_email)
        edtPassword = findViewById(R.id.edt_password)
        edtName = findViewById(R.id.edt_name)
       // avatar = findViewById(R.id.imgAva_main)

        mAuth = FirebaseAuth.getInstance()

        btn_signup.setOnClickListener {
            val email = edtEmail.text.toString().trim()
            val password = edtPassword.text.toString()
            val name = edtName.text.toString().trim()

            signUp(email, password, name, status.toString(), avatar)
        }
    }

    private fun signUp(email: String, password: String, name: String, status: String, avatar: String?) {
        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(name)) {
            Toast.makeText(this@SignUp, "please fill all the fields", Toast.LENGTH_SHORT).show()
        } else {
            mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this) { task ->
                    if (task.isSuccessful) {
                        addUserToDatabase(name, email, mAuth.currentUser?.uid!!, status, avatar)
                        val intent = Intent(this@SignUp, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    }
                }
        }
    }

    private fun addUserToDatabase(name: String, email: String, uid: String, status: String, avatar: String?) {
        mDbRef = FirebaseDatabase.getInstance().getReference()

        mDbRef.child("user").child(uid).setValue(User(name, email, uid, status, avatar))
    }
}