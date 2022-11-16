package com.example.projemanaj.activities

import android.os.Bundle
import android.text.TextUtils
import android.view.WindowManager
import android.widget.Toast
import com.example.projemanaj.R
import com.example.projemanaj.databinding.ActivitySignUpBinding
import com.example.projemanaj.firebase.Firestore
import com.example.projemanaj.models.UserModel
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser

class SignUpActivity : BaseActivity() {
    private var binding: ActivitySignUpBinding? = null

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySignUpBinding.inflate(layoutInflater)
        setContentView(binding!!.root)

        auth = FirebaseAuth.getInstance()

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN
        )
        setUpActionBar()
        binding!!.btnSignUp.setOnClickListener {
            registerUser()
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

    private fun validateForm(name: String, email: String, password: String): Boolean {
        return when {
            TextUtils.isEmpty(name) -> {
                showErrorSnackBar("Please enter a name")
                false
            }
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

    private fun registerUser() {
        val name: String = binding!!.edtName.text.toString().trim { it <= ' ' }
        val email: String = binding!!.edtEmail.text.toString().trim { it <= ' ' }
        val password: String = binding!!.edtPassword.text.toString()

        if (validateForm(name, email, password)) {
            showProgressDialog(resources.getString(R.string.tv_dialog_progress_text))
            auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener { task ->
                    if (task.isSuccessful) {
                        val firebaseUser: FirebaseUser = task.result!!.user!!
                        val user = UserModel(firebaseUser.uid, name, email, "", 0, "")
                        Firestore().registerUser(this, user)
                    } else {
                        hideProgressDialog()
                        Toast.makeText(this, task.exception!!.message, Toast.LENGTH_SHORT).show()
                    }
                }
        }
    }

    fun userRegisterSuccess() {
        hideProgressDialog()
        Toast.makeText(this, "You have successfully registered your account", Toast.LENGTH_SHORT)
            .show()
        auth.signOut()
        finish()
    }

}