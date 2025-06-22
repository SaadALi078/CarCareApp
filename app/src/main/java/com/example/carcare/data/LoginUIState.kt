package com.example.carcare.data



data class LoginUIState (

    var email: String=" ",
    var password: String=" ",

    var emailError: Boolean=false,
    var passwordError: Boolean=true,
)
