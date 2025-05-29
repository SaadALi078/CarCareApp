package com.example.carcare.Screens

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.ui.res.stringResource
import androidx.compose.material3.Text
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Checkbox
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.material3.Text

import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.example.carcare.Component.NormalTextComponent

import com.example.carcare.R
import androidx.compose.ui.res.stringResource

import androidx.compose.ui.res.stringArrayResource

import androidx.compose.ui.unit.dp
import com.example.carcare.Component.ButtonComponent
import com.example.carcare.Component.CheckboxComponent
import com.example.carcare.Component.ClickableLoginTextComponent
import com.example.carcare.Component.DividerTextComponent
import com.example.carcare.Component.HeadingTextComponent
import com.example.carcare.Component.MyTextField
import com.example.carcare.Component.UnderlineTextComponent
import com.example.carcare.Component.passwordTextField
import com.example.carcare.Data.LoginViewModel
import com.example.carcare.R.drawable.email
import com.example.carcare.app.carcare
import com.example.carcare.navigation.BackHandler
import com.example.carcare.navigation.Router
import com.example.carcare.navigation.Screen


@Composable
fun LoginScreen (){
    Surface(
        color = Color.White,
        modifier = Modifier
            .fillMaxSize()
            .padding(28.dp)
    ) {
       Column {
           Spacer(modifier = Modifier.height(15.dp))
           NormalTextComponent(value = stringResource(id = R.string.login))
           HeadingTextComponent(value = stringResource(id = R.string.welcome))
Spacer(modifier = Modifier.height(20.dp))
           MyTextField(labelValue = stringResource(id = R.string.email),
           painterResource(id = R.drawable.email),
               errorMessage = stringResource(id = R.string.emailerro),
               onTextSelected = {

               })


          passwordTextField(labelValue = stringResource(id = R.string.password),
               painterResource(id = R.drawable.passwordicon),
              onTextSelected = {

              })


           Spacer(modifier = Modifier.height(40.dp))
           UnderlineTextComponent(value = stringResource(id = R.string.forgot_password),)


           Spacer(modifier = Modifier.height(40.dp))
ButtonComponent(value = stringResource(id = R.string.login), onButtonClicked = {

})

           Spacer(modifier = Modifier.height(20.dp))
           DividerTextComponent()

ClickableLoginTextComponent (tryingToLogin = false, onTextSelected = {  Router.navigateTo(Screen.Signup) })

       }
   }
    BackHandler {
        Router.navigateTo(Screen.Signup)
    }
}

@Preview(showBackground = true)
@Composable
fun LoginScreenPreview(){
    LoginScreen()
}