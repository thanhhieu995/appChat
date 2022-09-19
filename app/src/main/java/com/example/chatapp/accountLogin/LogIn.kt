package com.example.chatapp.accountLogin

import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.TextUtils
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.chatapp.R
import com.example.chatapp.main.MainActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import kotlinx.android.synthetic.main.activity_log_in.*

class LogIn : AppCompatActivity() {

    var hasMore : Boolean = false

    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var edtLogin: Button
    private lateinit var btnSigUp: Button

    private lateinit var mAuth: FirebaseAuth

    private lateinit var mDbRef: DatabaseReference

    private var isCheckbox: Boolean = false
    private lateinit var strEmail: String
    lateinit var strPassword: String

    private lateinit var checkBox: CheckBox
    private lateinit var sharedPreferences: SharedPreferences
    lateinit var editor: SharedPreferences.Editor

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        supportActionBar?.hide()

        edtEmail = findViewById(R.id.edt_email)
        edtPassword = findViewById(R.id.edt_password)
        checkBox = findViewById(R.id.checkRemember)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        editor = sharedPreferences.edit()

        mAuth = FirebaseAuth.getInstance()

        if (sharedPreferences.getBoolean("logging_Success", false)) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        checkSharedPreference()

        btn_signup.setOnClickListener {
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }

        btn_login.setOnClickListener {
            val email = edtEmail.text.toString().trim()
            val password = edtPassword.text.toString()

            checkBoxChecked()
            login(email, password)
        }

        hasMore = true
    }

    override fun onResume() {
        super.onResume()
        hasMore = true

    }

    private fun login(email: String, password: String) {

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this@LogIn, "Please enter your name and password!!!", Toast.LENGTH_LONG).show()
        } else {
            mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, OnCompleteListener { task ->
                    if (task.isSuccessful) {

                        FirebaseDatabase.getInstance().getReference("user").child(mAuth.uid.toString())
                            .child("status").setValue("online")

                        Toast.makeText(this, "Successfully Logged In", Toast.LENGTH_LONG).show()

                        editor.putBoolean("logging_Success", true)
                        editor.commit()

                        val intent = Intent(this, MainActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                       Toast.makeText(this, "Login Failed", Toast.LENGTH_LONG).show()
                    }
                })
        }
    }

    private fun checkBoxChecked() {
        if (checkBox.isChecked) {
            editor.putBoolean("check_Remember", true)
            strEmail = edtEmail.text.toString()
            editor.putString("email", strEmail)
            strPassword = edtPassword.text.toString()
            editor.putString("password", strPassword)
        } else {
            editor.putBoolean("check_Remember", false)
            editor.putString("email", "")
            editor.putString("password", "")
        }
        editor.commit()
    }

    private fun checkSharedPreference() {
        //strCheckBox = sharedPreferences.getString(getString(R.id.checkRemember), "False").toString()
        isCheckbox = sharedPreferences.getBoolean("check_Remember", false)
        strEmail = sharedPreferences.getString("email", "").toString()
        strPassword = sharedPreferences.getString("password", "").toString()
        edtEmail.setText(strEmail)
        edtPassword.setText(strPassword)
        //checkBox.isChecked = strCheckbox == "True"

        checkBox.isChecked = isCheckbox
    }

    override fun onBackPressed() {
        super.onBackPressed()
        val a = Intent(Intent.ACTION_MAIN)
        a.addCategory(Intent.CATEGORY_HOME)
        a.flags = Intent.FLAG_ACTIVITY_NEW_TASK
        startActivity(a)
    }
}