package com.example.carcare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.carcare.app.CarCareapp
import com.example.carcare.navigation.Router
import com.example.carcare.navigation.Screen
import com.example.carcare.ui.theme.CarCareTheme
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CarCareTheme {
                Router.currentScreen.value = Screen.LoginScreen
                CarCareapp()
            }
        }
    }
}

@Preview
@Composable
fun DefaultPreview(){
    CarCareTheme {
        Router.currentScreen.value = Screen.LoginScreen
        CarCareapp()
    }
}