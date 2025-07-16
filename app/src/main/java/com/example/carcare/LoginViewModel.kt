package com.example.carcare

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.carcare.data.LoginUIEvent
import com.example.carcare.data.LoginUIState
import com.example.carcare.data.rules.Validator
import com.example.carcare.navigation.Router
import com.example.carcare.navigation.Screen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener

class LoginViewModel : ViewModel() {
    private val TAG = LoginViewModel::class.simpleName

    var LoginUIState = mutableStateOf(LoginUIState())
    var allValidationsPassed = mutableStateOf(false)
    var logininProgress = mutableStateOf(false)
    var errorMessage = mutableStateOf<String?>(null)

    fun onEvent(event: LoginUIEvent) {
        when (event) {
            is LoginUIEvent.EmailChange -> {
                LoginUIState.value = LoginUIState.value.copy(email = event.email)
            }

            is LoginUIEvent.PasswordChange -> {
                LoginUIState.value = LoginUIState.value.copy(password = event.password)
            }

            is LoginUIEvent.LoginButtonClicked -> {
                login()
            }
        }
        validateInputs()
    }

    private fun validateInputs() {
        val emailResult = Validator.validateEmail(LoginUIState.value.email)
        val passwordResult = Validator.validatePassword(LoginUIState.value.password)

        LoginUIState.value = LoginUIState.value.copy(
            emailError = emailResult.status,
            passwordError = !passwordResult.status
        )

        allValidationsPassed.value = emailResult.status && passwordResult.status
    }

    private fun login() {
        if (logininProgress.value) return

        // Validate fields again before login
        validateInputs()
        if (!allValidationsPassed.value) {
            errorMessage.value = "Please fill all fields correctly"
            return
        }

        logininProgress.value = true
        val email = LoginUIState.value.email
        val password = LoginUIState.value.password

        FirebaseAuth.getInstance()
            .signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                logininProgress.value = false
                if (task.isSuccessful) {
                    Router.navigateTo(Screen.HomeScreen)
                } else {
                    errorMessage.value = task.exception?.message ?: "Invalid credentials"
                }
            }
            .addOnFailureListener {
                logininProgress.value = false
                errorMessage.value = "Login failed: ${it.message}"
            }
    }

    fun logout() {
        val firebaseAuth = FirebaseAuth.getInstance()
        firebaseAuth.signOut()

        val authStateListener = AuthStateListener {
            if (it.currentUser == null) {
                Log.d(TAG, "Logout successful")
                Router.navigateTo(Screen.LoginScreen)
            }
        }
        firebaseAuth.addAuthStateListener(authStateListener)
    }
}