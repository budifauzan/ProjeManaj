package com.example.projemanaj.firebase

import android.widget.Toast
import com.example.projemanaj.activities.SignInActivity
import com.example.projemanaj.activities.SignUpActivity
import com.example.projemanaj.models.UserModel
import com.example.projemanaj.utils.Constants
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore

class Firestore {
    private val mFirestore = FirebaseFirestore.getInstance()

    fun registerUser(activity: SignUpActivity, userModel: UserModel) {
        mFirestore.collection(Constants.USERS_COLLECTION_REF).add(userModel).addOnSuccessListener {
            activity.userRegisterSuccess()
        }.addOnFailureListener {
            Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
        }
    }

    fun signInUser(activity: SignInActivity) {
        mFirestore.collection(Constants.USERS_COLLECTION_REF)
            .whereEqualTo("id", activity.auth.currentUser!!.uid).get()
            .addOnSuccessListener { documents ->
                if (!documents.isEmpty) {
                    for (document in documents) {
                        val loggedInUser = document.toObject(UserModel::class.java)
                        activity.signInSuccess(loggedInUser)
                    }
                }
            }.addOnFailureListener {
                Toast.makeText(activity, it.message, Toast.LENGTH_SHORT).show()
            }
    }

    fun getCurrentUserID(): String {
        val currentUser = FirebaseAuth.getInstance().currentUser
        return currentUser!!.uid
    }
}
