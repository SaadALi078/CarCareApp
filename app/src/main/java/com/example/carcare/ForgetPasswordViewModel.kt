package com.example.carcare.data

import android.util.Log
import androidx.lifecycle.ViewModel
import com.google.firebase.auth.FirebaseAuth

class ForgetPasswordViewModel : ViewModel() {

    private val TAG = ForgetPasswordViewModel::class.simpleName

    fun sendPasswordResetEmail(
        email: String,
        onResult: (Boolean, String?) -> Unit
    ) {
        if (email.trim().isEmpty()) {
            onResult(false, "Please enter your email")
            return
        }

        FirebaseAuth.getInstance()
            .sendPasswordResetEmail(email.trim())
            .addOnCompleteListener { task ->
                if (task.isSuccessful) {
                    Log.d(TAG, "Password reset email sent to $email")
                    onResult(true, "Password reset email sent.")
                } else {
                    Log.e(TAG, "Error: ${task.exception?.message}")
                    onResult(false, task.exception?.localizedMessage)
                }
            }
    }
}
