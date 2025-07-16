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
                // No need to set `Router.currentScreen.value`
                CarCareapp()
            }
        }
    }
}


@Preview
@Composable
fun DefaultPreview() {
    CarCareTheme {
        Router.navigateTo(Screen.LoginScreen)
        CarCareapp()
    }
}
