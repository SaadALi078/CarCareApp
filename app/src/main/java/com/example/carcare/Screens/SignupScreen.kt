package com.example.carcare.Screens
import android.util.Log
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
import androidx.compose.ui.tooling.preview.Preview
import com.example.carcare.Component.NormalTextComponent

import com.example.carcare.R

import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.carcare.Component.ButtonComponent
import com.example.carcare.Component.CheckboxComponent
import com.example.carcare.Component.ClickableLoginTextComponent
import com.example.carcare.Component.DividerTextComponent
import com.example.carcare.Component.HeadingTextComponent
import com.example.carcare.Component.MyTextField
import com.example.carcare.Component.passwordTextField
import com.example.carcare.Data.SignupViewModel
import com.example.carcare.Data.SingupUIEvent
import com.example.carcare.navigation.Router
import com.example.carcare.navigation.Screen


@Composable
fun SignupScreen(loginViewModel: SignupViewModel= viewModel()) {

    Box(modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center) {

        Image(
            painter = painterResource(id = R.drawable.bg2),
            contentDescription = "Background Image",
            contentScale = ContentScale.Crop,
            modifier = Modifier.fillMaxSize()
        )

        Surface(
            color = Color.Transparent,
            modifier = Modifier
                .fillMaxSize()

                .padding(28.dp)
        ) {
            Spacer(modifier = Modifier.height(5.dp))
            Column {


                Spacer(modifier = Modifier.height(200.dp))

                MyTextField(
                    labelValue = stringResource(id = R.string.firstname),
                    painterResources = painterResource(id = R.drawable.profileicon),
                    onTextSelected = {
                        loginViewModel.onEvent(SingupUIEvent.FisrtNameChange(it))
                    },
                    errorStatus = loginViewModel.RegistrationUIState.value.firstNameError
                )
                Spacer(modifier = Modifier.height(0.dp))
                MyTextField(
                    labelValue = stringResource(id = R.string.lastname),
                    painterResources = painterResource(id = R.drawable.profileicon),
                    onTextSelected = {
                        loginViewModel.onEvent(SingupUIEvent.LastNameChange(it))
                    },
                    errorStatus = loginViewModel.RegistrationUIState.value.lastNameError
                )
                Spacer(modifier = Modifier.height(0.dp))
                MyTextField(
                    labelValue = stringResource(id = R.string.email),
                    errorMessage = stringResource(id = R.string.emailerro),
                    painterResources = painterResource(id = R.drawable.email),
                    onTextSelected = {
                        loginViewModel.onEvent(SingupUIEvent.EmailChange(it))


                    },
                    errorStatus = loginViewModel.RegistrationUIState.value.emailError,

                    )
                Spacer(modifier = Modifier.height(0.dp))
                passwordTextField(
                    labelValue = stringResource(id = R.string.password),
                    painterResources = painterResource(id = R.drawable.passwordicon),
                    onTextSelected = {
                        loginViewModel.onEvent(SingupUIEvent.PasswordChange(it))
                        Log.d(
                            "DEBUG",
                            "Password Error: ${loginViewModel.RegistrationUIState.value.passwordError}"
                        )

                    },
                    errorStatus = loginViewModel.RegistrationUIState.value.passwordError
                )
                CheckboxComponent(
                    value = stringResource(id = R.string.terms_and_Conditions),
                    onTextSelected = {
                        Router.navigateTo(Screen.TermsAndCondtionsScreen)
                    },
                    onCheckedChange = {
                        loginViewModel.onEvent(SingupUIEvent.PrivacyPolicyCheckBoxClicked(it))
                    })
                Spacer(modifier = Modifier.height(0.dp))

                ButtonComponent(
                    value = stringResource(id = R.string.register),
                    onButtonClicked = {
                        loginViewModel.onEvent(SingupUIEvent.RegisterButtonClicked)
                    },
                    isEnabled = loginViewModel.allValidationsPassed.value
                )

                Spacer(modifier = Modifier.height(3.dp))
                DividerTextComponent()

                ClickableLoginTextComponent(tryingToLogin = true, onTextSelected = {
                    Router.navigateTo(Screen.LoginScreen)


                })

            }
        }
        if (loginViewModel.signupInProgress.value) {
            CircularProgressIndicator()
        }
    }
}








@Preview(showBackground = true)
@Composable
fun DefaultPreviewOfSignUpScreen() {
    SignupScreen()
}









