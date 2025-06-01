package com.example.carcare

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.carcare.Data.LoginUIEvent
import com.example.carcare.Data.LoginUIState
import com.example.carcare.Data.SignupViewModel
import com.example.carcare.Data.rules.Validator
import com.example.carcare.navigation.Router
import com.example.carcare.navigation.Screen
import com.google.firebase.auth.FirebaseAuth

class LoginViewModel:ViewModel() {
    private val TAG = LoginViewModel::class.simpleName

    var LoginUIState= mutableStateOf(LoginUIState())

var allValidationsPassed=mutableStateOf(false)

    var logininProgress=mutableStateOf(false)

    fun onEvent(event: LoginUIEvent){
        when(event){
            is LoginUIEvent.EmailChange->{
                LoginUIState.value = LoginUIState.value.copy(
                    email = event.email
                )

            }
            is LoginUIEvent.PasswordChange->{
                LoginUIState.value = LoginUIState.value.copy(
                    password = event.password
                )

            }
            is LoginUIEvent.LoginButtonClicked->{
login()
            }
        }
        validationLoginUIDataWithRules()
    }


    private fun validationLoginUIDataWithRules(){

        val emailResult = Validator.validateEmail(
            email = LoginUIState.value.email
        )
        val passwordResult = Validator.validatePassword(
            password = LoginUIState.value.password
        )
LoginUIState.value=LoginUIState.value.copy(
    emailError = emailResult.status,
    passwordError = !passwordResult.status
)
      allValidationsPassed.value=emailResult.status&& passwordResult.status
    }
    private fun login() {
        logininProgress.value=true
        val email=LoginUIState.value.email
        val password=LoginUIState.value.password
        FirebaseAuth
            .getInstance()
            .signInWithEmailAndPassword(email,password)
            .addOnCompleteListener {

                Log.d(TAG,"Inside_login-Success")
                Log.d(TAG,"${it.isSuccessful}")
                if (it.isSuccessful){
                    logininProgress.value=false
                    Router.navigateTo(Screen.HomeScreen)
                }
            }
            .addOnFailureListener {
                Log.d(TAG,"Inside_Login_Failurel")
                Log.d(TAG,"${it.localizedMessage}")
                logininProgress.value=false
            }

    }
}