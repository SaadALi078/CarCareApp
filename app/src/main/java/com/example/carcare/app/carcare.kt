package com.example.carcare.app

import androidx.compose.animation.Crossfade
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import com.example.carcare.Screens.HomeScreen
import com.example.carcare.Screens.LoginScreen
import com.example.carcare.Screens.SignupScreen
import com.example.carcare.Screens.TermsAndConditionsScreen
import com.example.carcare.navigation.Router
import com.example.carcare.navigation.Screen


@Composable
fun carcare(){

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = Color.White,

    ){
        Crossfade(targetState = Router.currentScreen){currentState->
            when(currentState.value){
                is Screen.Signup->{
                    SignupScreen()
                }
                is Screen.TermsAndCondtionsScreen->{
                    TermsAndConditionsScreen()
                }
is Screen.LoginScreen->{
    LoginScreen()
}
                is Screen.HomeScreen ->{
                    HomeScreen()
                }
                Screen.TermsAndCondtionsScreen -> TODO()
                Screen.TermsAndCondtionsScreen -> TODO()
            }

        }
    }
}
