package com.example.carcare.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.ui.res.stringResource
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource

import com.example.carcare.R

import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.carcare.Component.ButtonComponent
import com.example.carcare.Component.ClickableLoginTextComponent
import com.example.carcare.Component.ClickableUnderlineTextComponent
import com.example.carcare.Component.DividerTextComponent
import com.example.carcare.Component.MyTextField
import com.example.carcare.Component.passwordTextField
import com.example.carcare.Data.LoginUIEvent
import com.example.carcare.LoginViewModel
import com.example.carcare.navigation.BackHandler
import com.example.carcare.navigation.Router
import com.example.carcare.navigation.Screen
import android.widget.Toast
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext


@Composable
fun LoginScreen(loginViewModel: LoginViewModel = viewModel()) {
    val context = LocalContext.current
    val errorMessage = loginViewModel.errorMessage.value

    // Show toast when errorMessage is updated
    LaunchedEffect(errorMessage) {
        errorMessage?.let {
            Toast.makeText(context, it, Toast.LENGTH_SHORT).show()
            loginViewModel.errorMessage.value = null  // Clear message after showing
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        // Background Image
        Image(
            painter = painterResource(id = R.drawable.bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // Login Form UI
        Surface(
            color = Color.Transparent,
            modifier = Modifier
                .fillMaxSize()
                .padding(28.dp)
        ) {
            Column {
                Spacer(modifier = Modifier.height(215.dp))

                // Email Input
                MyTextField(
                    labelValue = stringResource(id = R.string.email),
                    painterResource(id = R.drawable.email),
                    errorMessage = stringResource(id = R.string.emailerro),
                    onTextSelected = {
                        loginViewModel.onEvent(LoginUIEvent.EmailChange(it))
                    },
                    errorStatus = loginViewModel.LoginUIState.value.emailError
                )

                Spacer(modifier = Modifier.height(15.dp))

                // Password Input
                passwordTextField(
                    labelValue = stringResource(id = R.string.password),
                    painterResource(id = R.drawable.passwordicon),
                    onTextSelected = {
                        loginViewModel.onEvent(LoginUIEvent.PasswordChange(it))
                    },
                    errorStatus = loginViewModel.LoginUIState.value.passwordError
                )

                Spacer(modifier = Modifier.height(40.dp))

                // Login Button
                ButtonComponent(
                    value = stringResource(id = R.string.login),
                    onButtonClicked = {
                        loginViewModel.onEvent(LoginUIEvent.LoginButtonClicked)
                    },
                    isEnabled = loginViewModel.allValidationsPassed.value
                )

                Spacer(modifier = Modifier.height(20.dp))

                // Forgot Password
                ClickableUnderlineTextComponent(value = stringResource(id = R.string.forgot_password)) {
                    Router.navigateTo(Screen.ForgetPasswordScreen)
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Divider and Sign up link
                DividerTextComponent()
                ClickableLoginTextComponent(
                    tryingToLogin = false,
                    onTextSelected = { Router.navigateTo(Screen.Signup) }
                )
            }
        }

        // Show loader while logging in
        if (loginViewModel.logininProgress.value) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }

    // Back press goes to signup
    BackHandler {
        Router.navigateTo(Screen.Signup)
    }
}
