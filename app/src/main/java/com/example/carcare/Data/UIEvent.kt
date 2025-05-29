package com.example.carcare.Data

sealed class UIEvent {
    data class FisrtNameChange(val firstName: String) : UIEvent()
    data class LastNameChange(val lastName: String) : UIEvent()
    data class EmailChange(val email: String) : UIEvent()
    data class PasswordChange(val password: String) : UIEvent()
    data class PrivacyPolicyCheckBoxClicked(val status: Boolean): UIEvent()
    object RegisterButtonClicked: UIEvent()

}
