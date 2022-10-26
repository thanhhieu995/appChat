package vnd.hieuUpdate.chitChat.accountLogin

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.preference.PreferenceManager
import android.text.TextUtils
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.OnFailureListener
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.*
import vnd.hieuUpdate.chitChat.main.MainActivity
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import vnd.hieuUpdate.chitChat.R


class LogIn : AppCompatActivity() {

    var hasMore: Boolean = false

    private lateinit var edtEmail: EditText
    private lateinit var edtPassword: EditText
    private lateinit var btnLogin: Button
    private lateinit var btnSigUp: Button
    lateinit var imgEye: ImageView
    var isClicked: Boolean = true

    private lateinit var mAuth: FirebaseAuth

    private lateinit var mDbRef: DatabaseReference

    private var isCheckbox: Boolean = false
    private lateinit var strEmail: String
    lateinit var strPassword: String

    private lateinit var checkBox: CheckBox
    private lateinit var sharedPreferences: SharedPreferences
    lateinit var editor: SharedPreferences.Editor

    var hadUer: Boolean = false


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_log_in)

        supportActionBar?.hide()

        edtEmail = findViewById(R.id.lg_edt_email)
        edtPassword = findViewById(R.id.lg_edt_password)
        checkBox = findViewById(R.id.lg_checkRemember)
        btnLogin = findViewById(R.id.lg_btn_login)
        btnSigUp = findViewById(R.id.lg_btn_signup)
        imgEye = findViewById(R.id.lg_eye_pass)

        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(this)
        editor = sharedPreferences.edit()

//        val password: TextInputLayout? = findViewById<TextInputLayout>(R.id.layout_password)
//        if (password != null) {
//            password.editText?.text.toString()
//        }

        mAuth = FirebaseAuth.getInstance()

        mDbRef = FirebaseDatabase.getInstance().reference

        val test1 = mAuth.uid

        if (sharedPreferences.getBoolean("logging_Success", false)) {
//            checkUserDatabase()
            if (mAuth.uid != null) {
                val intent = Intent(this, MainActivity::class.java)
                intent.putExtra("hasMore", true)
                startActivity(intent)
                finish()
            } else {
                Toast.makeText(this@LogIn, "Account is not exist!!!", Toast.LENGTH_LONG).show()
            }
        }

        checkSharedPreference()

        btnSigUp.setOnClickListener {
            val intent = Intent(this, SignUp::class.java)
            startActivity(intent)
        }

        btnLogin.setOnClickListener {
            val email = edtEmail.text.toString().trim()
            val password = edtPassword.text.toString()

            checkBoxChecked()
            login(email, password)
        }

        hasMore = true

        imgEye.setOnClickListener(object : OnClickListener {
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


    companion object {
        fun start(context: Context) {
            val intent = Intent(context, LogIn::class.java)
            context.startActivity(intent)
        }
    }

    override fun onResume() {
        super.onResume()
        hasMore = true

    }

    private fun login(email: String, password: String) {

        if (TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
            Toast.makeText(this@LogIn, "Please enter your name and password!!!", Toast.LENGTH_LONG)
                .show()
        } else {
            mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, OnCompleteListener { task ->
//                    if (task.isSuccessful) {
//
//                        FirebaseDatabase.getInstance().getReference("user")
//                            .child(mAuth.uid.toString())
//                            .child("status").setValue("online")
//
//                        Toast.makeText(this, "Successfully Logged In", Toast.LENGTH_LONG).show()
//
//                        editor.putBoolean("logging_Success", true)
//                        editor.commit()
//
//                        val intent = Intent(this, MainActivity::class.java)
//                        startActivity(intent)
//                        finish()
//                    } else {
//                        val e: Exception? = task.exception
//                        if (e != null) {
//                            Toast.makeText(this, "Login Failed" + e.message, Toast.LENGTH_LONG)
//                                .show()
//                        }
//                    }

                    try {
                        if (task.isSuccessful) {
                            val hashMap: HashMap<String, String> = HashMap()
                            hashMap.put("status", "online")
                            FirebaseDatabase.getInstance().getReference("user")
                                .child(mAuth.uid.toString())
                                .updateChildren(hashMap as Map<String, Any>)

                            Toast.makeText(this, "Successfully Logged In", Toast.LENGTH_LONG).show()

                            editor.putBoolean("logging_Success", true)
                            editor.commit()

                            val intent = Intent(this, MainActivity::class.java)
                            intent.putExtra("hasMore", true)
                            startActivity(intent)
                            finish()
                        }
                    } catch (e: FirebaseNetworkException) {

                        Toast.makeText(this@LogIn, "LogIn failed: " + e.localizedMessage, Toast.LENGTH_LONG)
                            .show()
                    }

                })
                .addOnFailureListener(this, OnFailureListener{
                    Toast.makeText(this@LogIn, it.localizedMessage, Toast.LENGTH_LONG).show()
                })
        }
    }

    private fun checkBoxChecked() {
        if (checkBox.isChecked) {
            editor.putBoolean("check_Remember", true)
            strEmail = edtEmail.text.toString().trim()
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
        edtEmail.setText(strEmail.trim())
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
        finish()
    }
}