package com.example.carcare.Data



data class LoginUIState (

    var email: String=" ",
    var password: String=" ",

    var emailError: Boolean=false,
    var passwordError: Boolean=true,
)
