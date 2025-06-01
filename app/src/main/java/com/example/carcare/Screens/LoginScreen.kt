package com.example.carcare.Screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.ui.tooling.preview.Preview
import com.example.carcare.Component.NormalTextComponent

import com.example.carcare.R

import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.carcare.Component.ButtonComponent
import com.example.carcare.Component.ClickableLoginTextComponent
import com.example.carcare.Component.DividerTextComponent
import com.example.carcare.Component.HeadingTextComponent
import com.example.carcare.Component.MyTextField
import com.example.carcare.Component.UnderlineTextComponent
import com.example.carcare.Component.passwordTextField
import com.example.carcare.Data.LoginUIEvent
import com.example.carcare.LoginViewModel
import com.example.carcare.navigation.BackHandler
import com.example.carcare.navigation.Router
import com.example.carcare.navigation.Screen
import kotlin.math.log


@Composable
fun LoginScreen(loginViewModel: LoginViewModel = viewModel()) {
    Box(
        modifier = Modifier.fillMaxSize()
    ) {
        // 🌄 Background image
        Image(
            painter = painterResource(id = R.drawable.bg),
            contentDescription = null,
            modifier = Modifier.fillMaxSize(),
            contentScale = ContentScale.Crop
        )

        // 🌟 Foreground content (login form)
        Surface(
            color = Color.Transparent,
            modifier = Modifier
                .fillMaxSize()
                .padding(28.dp)
        )

        {
            Column {
                Spacer(modifier = Modifier.height(35.dp))




                Spacer(modifier = Modifier.height(180.dp))
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
                passwordTextField(
                    labelValue = stringResource(id = R.string.password),
                    painterResource(id = R.drawable.passwordicon),
                    onTextSelected = {
                        loginViewModel.onEvent(LoginUIEvent.PasswordChange(it))
                    },
                    errorStatus = loginViewModel.LoginUIState.value.passwordError
                )



                Spacer(modifier = Modifier.height(40.dp))
                ButtonComponent(
                    value = stringResource(id = R.string.login),
                    onButtonClicked = {
                        loginViewModel.onEvent(LoginUIEvent.LoginButtonClicked)
                    },
                    isEnabled = loginViewModel.allValidationsPassed.value
                )

                Spacer(modifier = Modifier.height(20.dp))
                UnderlineTextComponent(value = stringResource(id = R.string.forgot_password))
                Spacer(modifier = Modifier.height(20.dp))
                DividerTextComponent()

                ClickableLoginTextComponent(
                    tryingToLogin = false,
                    onTextSelected = { Router.navigateTo(Screen.Signup) }
                )
            }
        }

        // 🌀 Show loading indicator when logging in
        if (loginViewModel.logininProgress.value) {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator()
            }
        }
    }

    // 🔙 Back press handling
    BackHandler {
        Router.navigateTo(Screen.Signup)
    }
}
