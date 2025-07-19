// Car Care/app/src/main/java/com/example/carcare/data/LoginUIState.kt
package com.example.carcare.data

data class LoginUIState(
    val email: String = "",
    val password: String = "",
    val emailError: String? = null,
    val passwordError: String? = null,
    val isFormValid: Boolean = false,
    val isLoading: Boolean = false,
    val loginSuccess: Boolean = false,
    val errorMessage: String? = null
)