package com.example.carcare.data

sealed class SingupUIEvent {
    data class FisrtNameChange(val firstName: String) : SingupUIEvent()
    data class LastNameChange(val lastName: String) : SingupUIEvent()
    data class EmailChange(val email: String) : SingupUIEvent()
    data class PasswordChange(val password: String) : SingupUIEvent()
    data class PrivacyPolicyCheckBoxClicked(val status: Boolean): SingupUIEvent()
    object RegisterButtonClicked: SingupUIEvent()

}
