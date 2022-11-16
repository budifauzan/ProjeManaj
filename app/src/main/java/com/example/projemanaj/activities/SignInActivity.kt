package com.example.projemanaj.activities

import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import android.widget.Toast
import com.example.projemanaj.R
import com.example.projemanaj.databinding.ActivitySignInBinding
import com.example.projemanaj.firebase.Firestore
import com.example.projemanaj.models.UserModel
import com.google.firebase.auth.FirebaseAuth

class SignInActivity : BaseActivity() {
    private var binding: ActivitySignInBinding? = null

    lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignInBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        auth = FirebaseAuth.getInstance()

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN,
            WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setUpActionBar()
        binding!!.btnSignIn.setOnClickListener {
            signIn()
        }
    }

    private fun setUpActionBar() {
        setSupportActionBar(binding!!.toolbar)
        val actionbar = supportActionBar
        actionbar!!.setDisplayHomeAsUpEnabled(true)
        actionbar.setDisplayShowTitleEnabled(false)
        actionbar.setHomeAsUpIndicator(R.drawable.ic_black_color_back_24dp)
        binding!!.toolbar.setNavigationOnClickListener {
            onBackPressed()
        }
    }

    private fun validateForm(email: String, password: String): Boolean {
        return when {
            TextUtils.isEmpty(email) -> {
                showErrorSnackBar("Please enter an email address")
                false
            }
            TextUtils.isEmpty(password) -> {
                showErrorSnackBar("Please enter a password")
                false
            }
            else -> true
        }
    }

    private fun signIn() {
        val email = binding!!.edtEmail.text.toString().trim() { it <= ' ' }
        val password = binding!!.edtPassword.text.toString().trim() { it <= ' ' }

        if (validateForm(email, password)) {
            showProgressDialog(resources.getString(R.string.tv_dialog_progress_text))
            auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        Firestore().signInUser(this)
                    } else {
                        hideProgressDialog()
                        Toast.makeText(this, task.exception!!.message, Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    fun signInSuccess(userModel: UserModel) {
        hideProgressDialog()
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }
}