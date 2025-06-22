package com.example.carcare

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.carcare.app.CarCareApp // ✅ Correct name here!

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            CarCareApp() // ✅ Correct function call
        }
    }
}

@Preview
@Composable
fun DefaultPreview() {
    CarCareApp() // ✅ Correct preview
}
