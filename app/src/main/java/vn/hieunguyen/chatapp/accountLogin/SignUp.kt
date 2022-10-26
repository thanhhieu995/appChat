package vn.hieunguyen.chatapp.accountLogin

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Patterns
import android.view.View
import android.view.View.OnClickListener
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

    private var hasTransfer: Boolean = false

    var status: String? = ""

    var avatar: String? = null

    var isCalling: Boolean = false

    var isTyping: Boolean = false

    var showTyping: Boolean = false

    var count: Int = 0

    var fromUid: String = ""

    var lastMsg: String = ""

    var acceptCall: Boolean = false

    var listToken: ArrayList<String>? = ArrayList()

    var room: String? = ""


    private val blockCharacter: String = "!@#$%^&*()_=+?/:;{}1234567890 "

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_sign_up)

        supportActionBar?.hide()

        edtEmail = findViewById(R.id.su_edt_email)
        edtPassword = findViewById(R.id.su_edt_password)
        edtName = findViewById(R.id.su_edt_name)
        btnSigUp = findViewById(R.id.su_btn_signup)
        imgEye = findViewById(R.id.su_eye_pass)

        mDbRef = FirebaseDatabase.getInstance().reference
        mAuth = FirebaseAuth.getInstance()

        getToken()
    }

    private fun getToken() {
        FirebaseInstanceId.getInstance().instanceId.addOnSuccessListener { instanceIdResult ->
            val newToken = instanceIdResult.token
            if (listToken?.contains(newToken) == false || listToken!!.isEmpty()) {
                listToken!!.add(newToken)
            }
        }
    }

    private fun clickEyePass() {
        imgEye.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                if (isClicked) {
                    imgEye.setImageResource(R.drawable.ic_baseline_visibility_24)
                    edtPassword.transformationMethod =
                        HideReturnsTransformationMethod.getInstance()
                } else {
                    imgEye.setImageResource(R.drawable.ic_baseline_visibility_off_24)
                    edtPassword.transformationMethod =
                        PasswordTransformationMethod.getInstance()
                }
                isClicked = !isClicked
            }

        })
    }

    private fun createAccount() {
        btnSigUp.setOnClickListener(object : OnClickListener{
            override fun onClick(v: View?) {
                if (checkEmpty()) {
                    countCharacterAndCreateAccount()
                } else {
                    Toast.makeText(this@SignUp, "Please fill in all the information to register for an account", Toast.LENGTH_LONG).show()
                }
            }

        })
    }

    fun countCharacterAndCreateAccount() {
        var hasMore: Boolean = false
        val user = User()
        for (i in edtName.text) {
            if (blockCharacter.contains(i)) {
                Toast.makeText(
                    this@SignUp,
                    "Your name is not valid, your name don't contain number and specific character",
                    Toast.LENGTH_LONG
                ).show()
                hasMore = true
                break
            }
        }

        if (!hasMore) {
            val email = edtEmail.text.toString().trim()
            val password = edtPassword.text.toString()
            if (isValidEmail(edtEmail.text.toString().trim()) && findCharacterEmail() && !checkUpperCaseEmail()) {
                mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener { it
                    if (it.isSuccessful) {
                        addUserToData(user)
                        addUnreadToDataBase()
                        Toast.makeText(
                            this@SignUp,
                            "Sign up success",
                            Toast.LENGTH_LONG
                        ).show()
                        val intent = Intent(this@SignUp, SetUpActivity::class.java)
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(
                            this@SignUp,
                            "Sign up fail: " + (it.exception!!.localizedMessage?.toString() ?: toString()),
                            Toast.LENGTH_LONG
                        ).show()
                    }
                }
            } else {
                Toast.makeText(this@SignUp, "Your email is not valid, please try again", Toast.LENGTH_LONG).show()
            }
        }
    }
    private fun addUserToData(user: User) {
//        getToken()
        mDbRef.child("user").child(mAuth.uid.toString())
            .setValue(listToken?.let {
                room?.let { it1 ->
                    User(edtName.text.toString(), edtEmail.text.toString().trim(), mAuth.uid, status, avatar, isCalling, acceptCall, isTyping, showTyping, count, fromUid, lastMsg,
                        it,
                        it1
                    )
                }
            })
            .addOnSuccessListener {
                Toast.makeText(this@SignUp, "Account add to data", Toast.LENGTH_LONG).show()
            }
            .addOnFailureListener {
                Toast.makeText(this@SignUp, it.localizedMessage?.toString(), Toast.LENGTH_LONG).show()
            }
    }

    override fun onResume() {
        super.onResume()
        hasTransfer = true
        createAccount()
        clickEyePass()
    }


    private fun addUnreadToDataBase() {
        mDbRef.child("unRead").child(mAuth.uid.toString()).setValue(Unread(0, "", ""))
    }

    private fun checkEmpty(): Boolean {
        return !(edtName.text.isNullOrEmpty() || edtEmail.text.isNullOrEmpty() || edtPassword.text.isNullOrEmpty())
    }

    private fun findCharacterEmail(): Boolean {
        val alphabet: String = "qwertyuiopasdfghjklzxcvbnm"
        for (i in edtEmail.text) {
            if (blockCharacter.contains(Char(edtEmail.text[0].toInt()))) {
                return false
            }
            if (i.toString() == "@") {
                break
            } else {
                if (alphabet.contains(i)) {
                    return true
                }
            }
        }
        return false
    }

    private fun checkUpperCaseEmail(): Boolean {
        val upperCase: String = "QWERTYUIOPASDFGHJKLZXCVBNM"
        for (i in edtEmail.text) {
            if (upperCase.contains(i)) {
                return true
            }
        }
        return false
    }

    private fun isValidEmail(email: String): Boolean {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches()
    }
}