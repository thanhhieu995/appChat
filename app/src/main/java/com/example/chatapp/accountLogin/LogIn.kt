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
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import kotlinx.android.synthetic.main.activity_log_in.*

class LogIn : AppCompatActivity() {

    var hasMore : Boolean = false

    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var edtLogin: Button
    private lateinit var btnSigUp: Button

    private lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        supportActionBar?.hide()

        edtEmail = findViewById(R.id.edt_email)
        edtPassword = findViewById(R.id.edt_password)

        mAuth = FirebaseAuth.getInstance()

        btn_signup.setOnClickListener {
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }

        btn_login.setOnClickListener {
            val email = edtEmail.text.toString().trim()
            val password = edtPassword.text.toString()

            //hasMore = false

            login(email, password)
        }

        hasMore = true
    }

    override fun onResume() {
        super.onResume()
        hasMore = true
        if (mAuth.uid != null) {
            //statusAccount(mAuth.uid)
            FirebaseDatabase.getInstance().getReference("user").child(mAuth.uid!!).child("status").setValue("offline")
        }
    }

//    override fun onRestart() {
//        super.onRestart()
//        hasMore = true
//        if (mAuth.uid != null) {
//            //statusAccount(mAuth.uid)
//            FirebaseDatabase.getInstance().getReference("user").child(mAuth.uid.toString()).child("status").setValue("offline")
//        }
//    }

//    override fun onStart() {
//        super.onStart()
//        hasMore = true
//        //statusAccount(mAuth.uid)
//    }

    override fun onPause() {
        super.onPause()
        hasMore = true
        if (mAuth.uid != null) {
            //statusAccount(mAuth.uid!!)
            //FirebaseDatabase.getInstance().getReference("user").child(mAuth.uid!!).child("status").setValue("offline")
        }
    }

//    override fun onStop() {
//        super.onStop()
//        hasMore = true
//        statusAccount(mAuth.uid)
//    }

//    override fun onDestroy() {
//        super.onDestroy()
//        hasMore = true
//        statusAccount(mAuth.uid)
//    }

    private fun login(email: String, password: String) {

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this@LogIn, "please fill all the fields", Toast.LENGTH_LONG).show()
        } else {
            mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, OnCompleteListener { task ->
                    if (task.isSuccessful) {

                        FirebaseDatabase.getInstance().getReference("user").child(mAuth.uid.toString())
                            .child("status").setValue("online")

                        Toast.makeText(this, "Successfully Logged In", Toast.LENGTH_LONG).show()
                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                       Toast.makeText(this, "Login Failed", Toast.LENGTH_LONG).show()
                    }
                })
        }

        //statusAccount(mAuth.uid)
    }

    private fun statusAccount(loginUid: String? ) {
        val studentRef = FirebaseDatabase.getInstance().getReference("user").child(loginUid!!)
        val connectedRef = FirebaseDatabase.getInstance().getReference(".info/connected")

        connectedRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val connected = snapshot.getValue(Boolean::class.java)!!
                if (connected) {
                    if (hasMore) {
                        studentRef.child("status").setValue("offline!!!")
                    } else {
                        studentRef.child("status").onDisconnect().setValue("Offline!")
                        studentRef.child("status").setValue("Online")
                    }
                } else {
                    studentRef.child("status").setValue("offline")
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }
        })
    }
}