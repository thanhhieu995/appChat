package vnd.hieuUpdate.chitChat

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.view.View.OnClickListener
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.auth.FirebaseAuth
import vnd.hieuUpdate.chitChat.accountLogin.LogIn

class ResetActivity : AppCompatActivity() {

    lateinit var edtEmail: EditText
    lateinit var btnReset: Button
    lateinit var mAuth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_reset)

        edtEmail = findViewById(R.id.rs_edt_email)
        btnReset = findViewById(R.id.rs_btn_reset)
        mAuth = FirebaseAuth.getInstance()

        btnReset.setOnClickListener(object : OnClickListener{
            override fun onClick(p0: View?) {
                if (!edtEmail.text.isNullOrEmpty()) {
                    beginRecoverPass(edtEmail.text.toString())
                } else {
                    Toast.makeText(this@ResetActivity, "Please, enter your email for reset password", Toast.LENGTH_LONG).show()
                }
            }

        })
    }

    fun beginRecoverPass(email: String) {
        mAuth.sendPasswordResetEmail(email).addOnCompleteListener {
            if (it.isSuccessful) {
                backLogin()
                Toast.makeText(this@ResetActivity, "Successfully, please check your email (check spam if not see) to reset", Toast.LENGTH_LONG).show()
            } else {
                Toast.makeText(this@ResetActivity, it.exception?.localizedMessage, Toast.LENGTH_LONG).show()
            }
        }
            .addOnFailureListener {
                Toast.makeText(this@ResetActivity, it.localizedMessage?.toString(), Toast.LENGTH_LONG).show()
            }
    }

    override fun onBackPressed() {
        super.onBackPressed()
        backLogin()
    }

    fun backLogin() {
        val intent = Intent(this@ResetActivity, LogIn::class.java)
        startActivity(intent)
        finish()
    }
}