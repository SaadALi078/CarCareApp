package com.example.carcare

import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.carcare.data.LoginUIEvent
import com.example.carcare.data.LoginUIState
import com.example.carcare.data.rules.Validator
import com.example.carcare.navigation.Router
import com.example.carcare.navigation.Screen
import com.google.firebase.auth.FirebaseAuth

class LoginViewModel : ViewModel() {
    private val TAG = LoginViewModel::class.simpleName

    var LoginUIState = mutableStateOf(LoginUIState())   // UI state (email, password, errors)
    var allValidationsPassed = mutableStateOf(false)    // Form validation flag
    var logininProgress = mutableStateOf(false)         // Progress indicator
    var errorMessage = mutableStateOf<String?>(null)    // Toast error or success message

    // UI event handler
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

    // Validates email and password fields
    private fun validateInputs() {
        val emailResult = Validator.validateEmail(LoginUIState.value.email)
        val passwordResult = Validator.validatePassword(LoginUIState.value.password)

        LoginUIState.value = LoginUIState.value.copy(
            emailError = emailResult.status,
            passwordError = !passwordResult.status
        )

        allValidationsPassed.value = emailResult.status && passwordResult.status
    }

    // Handles actual login logic with Firebase
    private fun login() {
        if (logininProgress.value) return

        logininProgress.value = true
        val email = LoginUIState.value.email
        val password = LoginUIState.value.password

        FirebaseAuth.getInstance()
            .signInWithEmailAndPassword(email, password)
            .addOnCompleteListener { task ->
                logininProgress.value = false
                if (task.isSuccessful) {
                    // success hone par sirf navigation karo, message set mat karo
                    Router.navigateTo(Screen.HomeScreen)
                } else {
                    errorMessage.value = "Invalid credentials"
                }
            }
            .addOnFailureListener {
                logininProgress.value = false
                errorMessage.value = "Invalid credentials"
            }
    }

}
