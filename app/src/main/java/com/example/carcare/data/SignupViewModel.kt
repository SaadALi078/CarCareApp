package com.example.carcare.data

import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import com.example.carcare.data.rules.Validator
import com.example.carcare.navigation.Router
import com.example.carcare.navigation.Screen
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuth.AuthStateListener

class SignupViewModel : ViewModel() {
    private val TAG = SignupViewModel::class.simpleName

    var RegistrationUIState = mutableStateOf(RegistrationUIState())

    var allValidationsPassed=mutableStateOf(false)

    var signupInProgress=mutableStateOf(false)

    fun onEvent(event: SingupUIEvent) {

        when (event) {
            is SingupUIEvent.FisrtNameChange -> {
                RegistrationUIState.value = RegistrationUIState.value.copy(
                    firstname = event.firstName,
                )

            }

            is SingupUIEvent.LastNameChange -> {
                RegistrationUIState.value = RegistrationUIState.value.copy(
                    lastname = event.lastName
                )

            }

            is SingupUIEvent.EmailChange -> {
                RegistrationUIState.value = RegistrationUIState.value.copy(
                    email = event.email
                )

            }

            is SingupUIEvent.PasswordChange -> {
                val passwordResult = Validator.validatePassword(event.password)

                RegistrationUIState.value = RegistrationUIState.value.copy(
                    password = event.password,
                    passwordError = passwordResult.status
                )

                // Re-check all validations after this change



            }

            is SingupUIEvent.RegisterButtonClicked->{
                signup()
            }

is SingupUIEvent.PrivacyPolicyCheckBoxClicked -> {
    RegistrationUIState.value=RegistrationUIState.value.copy(
        privacyPolicyError = false,
        privacyPolicyAccepted = event.status



    )
}
            // Add more UIEvent cases here if needed
        }
validateDataWithRules()
        printState()
    }

    private fun signup(){
        Log.d(TAG,"Inside_SignUp")
        printState()
        validateDataWithRules()
        createUserInFirebase(
            email = RegistrationUIState.value.email,
            password = RegistrationUIState.value.password
        )

printState()
        validateDataWithRules()

    }

    private fun validateDataWithRules() {
        val fNameResult = Validator.validateFirstName(
            fName = RegistrationUIState.value.firstname // fixed
        )
        val lNameResult = Validator.validateLastName(
            lName = RegistrationUIState.value.lastname
        )
        val emailResult = Validator.validateEmail(
            email = RegistrationUIState.value.email
        )
        val passwordResult = Validator.validatePassword(
            password = RegistrationUIState.value.password
        )
        val privacyPolicyResult= Validator.validateprivacypolicyAcceptance(
            status = RegistrationUIState.value.privacyPolicyAccepted

        )

        Log.d(TAG, "Inside_validateDataWithRules")
        Log.d(TAG, "fNameResult = $fNameResult")
        Log.d(TAG, "lNameResult = $lNameResult")
        Log.d(TAG, "emailResult = $emailResult")
        Log.d(TAG, "passwordResult = $passwordResult")
        Log.d(TAG, "privacyPolicyResult = $privacyPolicyResult")

        RegistrationUIState.value = RegistrationUIState.value.copy(
            firstNameError = fNameResult.status,
            lastNameError = lNameResult.status,
            emailError = emailResult.status,
            passwordError = !passwordResult.status,
            privacyPolicyError =privacyPolicyResult.status
        )
        allValidationsPassed.value=fNameResult.status &&lNameResult.status&&
                emailResult.status&&passwordResult.status&&privacyPolicyResult.status

        // Optional: If all valid, proceed

    }

    private fun printState() {
        Log.d(TAG,"Inside_printstate")
        Log.d(TAG,RegistrationUIState.value.toString())
    }
    private fun createUserInFirebase(email: String, password: String) {
        signupInProgress.value=true
        // Firebase Auth logic here
        FirebaseAuth.getInstance()
            .createUserWithEmailAndPassword(email,password)
            .addOnCompleteListener {
                Log.d(TAG,"Inside_onCompleteListner")
                Log.d(TAG,"${it.isSuccessful}")
                signupInProgress.value=false
                if(it.isSuccessful){
                    Router.navigateTo(Screen.HomeScreen)
                }

            }
            .addOnFailureListener {
                Log.d(TAG,"Inside_OnfailueListner")
                Log.d(TAG,"Exception=${it.message}")
                Log.d(TAG,"Exception=${it.localizedMessage}")

            }
    }
    fun logout(){
val firebaseAuth= FirebaseAuth.getInstance()

firebaseAuth.signOut()

        val authStateListener= AuthStateListener{
            if (it.currentUser==null){
                Log.d(TAG,"Inside sign out success")
                Router.navigateTo((Screen.LoginScreen))
            }
            else{
                Log.d(TAG,"Insdie sign out is not complete")

            }
        }
firebaseAuth.addAuthStateListener(authStateListener)
    }
}
