package com.example.carcare.data.rules


    object Validator {

        fun validateFirstName(fName: String): ValidationResult {
            return ValidationResult(
                !fName.isNullOrEmpty() && fName.length >= 2
            )
        }

        fun validateLastName(lName: String): ValidationResult {
            return ValidationResult(
                !lName.isNullOrEmpty() && lName.length >= 2
            )
        }

        fun validateEmail(email: String): ValidationResult {
            if (email.isNullOrEmpty()) {
                return ValidationResult(false, "Email cannot be empty")
            }
            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                return ValidationResult(false, "Invalid email format")
            }
            return ValidationResult(true)
        }


        fun validatePassword(password: String): ValidationResult {
            val isValid = password.length >= 8 &&
                    password.any { it.isUpperCase() } &&
                    password.any { it.isLowerCase() } &&
                    password.any { it.isDigit() } &&
                    password.any { !it.isLetterOrDigit() }

            return ValidationResult(isValid)
        }
        fun validateprivacypolicyAcceptance(status: Boolean): ValidationResult{
            return ValidationResult(
                status
            )
        }
    }


data class ValidationResult(
    val status: Boolean=false,

    val message: String? = null
)