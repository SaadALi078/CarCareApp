package com.example.carcare.Data



data class RegistrationUIState (
    var firstname: String=" ",
    var lastname: String=" ",
    var email: String=" ",
    var password: String=" ",
    var privacyPolicyAccepted: Boolean=false,




    var firstNameError: Boolean=true,
    var lastNameError: Boolean=true,
    var emailError: Boolean=true,
    var passwordError: Boolean=false,
    var hasAttemptedValidation: Boolean = false,
    var privacyPolicyError: Boolean=false,
)
