package com.example.carcare.Screens
import android.text.Layout
import android.util.Log
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.ui.res.stringResource
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.material3.Text
import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.layout.AlignmentLine
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.carcare.Component.NormalTextComponent

import com.example.carcare.R
import androidx.compose.ui.res.stringResource

import androidx.compose.ui.res.stringArrayResource

import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.carcare.Component.ButtonComponent
import com.example.carcare.Component.CheckboxComponent
import com.example.carcare.Component.ClickableLoginTextComponent
import com.example.carcare.Component.DividerTextComponent
import com.example.carcare.Component.HeadingTextComponent
import com.example.carcare.Component.MyTextField
import com.example.carcare.Component.passwordTextField
import com.example.carcare.Data.LoginViewModel
import com.example.carcare.Data.UIEvent
import com.example.carcare.R.drawable.email
import com.example.carcare.navigation.Router
import com.example.carcare.navigation.Screen
import java.nio.file.WatchEvent


@Composable
fun SignupScreen(loginViewModel: LoginViewModel= viewModel()) {

        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            Surface(
                color = Color.White,
                modifier = Modifier
                    .fillMaxSize()
                    .padding(28.dp)
            ) {
                Column {
                    NormalTextComponent(value = stringResource(id = R.string.hello))
                    HeadingTextComponent(value = stringResource(id = R.string.createaccount))

                    Spacer(modifier = Modifier.height(20.dp))

                    MyTextField(
                        labelValue = stringResource(id = R.string.firstname),
                        painterResources = painterResource(id = R.drawable.profileicon),
                        onTextSelected = {
                            loginViewModel.onEvent(UIEvent.FisrtNameChange(it))
                        },
                        errorStatus = loginViewModel.RegistrationUIState.value.firstNameError
                    )

                    MyTextField(
                        labelValue = stringResource(id = R.string.lastname),
                        painterResources = painterResource(id = R.drawable.profileicon),
                        onTextSelected = {
                            loginViewModel.onEvent(UIEvent.LastNameChange(it))
                        },
                        errorStatus = loginViewModel.RegistrationUIState.value.lastNameError
                    )

                    MyTextField(
                        labelValue = stringResource(id = R.string.email),
                        errorMessage = stringResource(id = R.string.emailerro),
                        painterResources = painterResource(id = R.drawable.email),
                        onTextSelected = {
                            loginViewModel.onEvent(UIEvent.EmailChange(it))


                        },
                        errorStatus = loginViewModel.RegistrationUIState.value.emailError,

                        )
                    passwordTextField(
                        labelValue = stringResource(id = R.string.password),
                        painterResources = painterResource(id = R.drawable.passwordicon),
                        onTextSelected = {
                            loginViewModel.onEvent(UIEvent.PasswordChange(it))
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
                            loginViewModel.onEvent(UIEvent.PrivacyPolicyCheckBoxClicked(it))
                        })
                    Spacer(modifier = Modifier.height(80.dp))

                    ButtonComponent(
                        value = stringResource(id = R.string.register),
                        onButtonClicked = {
                            loginViewModel.onEvent(UIEvent.RegisterButtonClicked)
                        },
                        isEnabled = loginViewModel.allValidationsPassed.value
                    )

                    Spacer(modifier = Modifier.height(40.dp))
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
