package com.example.carcare.Screens


import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.carcare.R
import com.example.carcare.Component.HeadingTextComponent
import com.example.carcare.navigation.BackHandler
import com.example.carcare.navigation.Router
import com.example.carcare.navigation.Screen
import com.example.carcare.ui.theme.WhiteColor

@Composable
fun TermsAndConditionsScreen(){

    Surface (modifier = Modifier.fillMaxSize()
        .background(color = WhiteColor)
        .padding(16.dp))
    {
        HeadingTextComponent(value = stringResource(id = R.string.terms_and_Conditions))


    }
    BackHandler {
        Router.navigateTo(Screen.Signup)
    }
}
@Preview
@Composable
fun TermsAndCondtionsScreenPreview(){
    TermsAndConditionsScreen()
}