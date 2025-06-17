package com.example.carcare.Screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.carcare.navigation.BackHandler
import com.example.carcare.navigation.Router
import com.example.carcare.navigation.Screen
import com.example.carcare.ui.theme.WhiteColor

@Composable
fun TermsAndConditionsScreen() {
    BackHandler {
        Router.navigateTo(Screen.Signup)
    }

    Surface(
        modifier = Modifier
            .fillMaxSize()
            .background(color = WhiteColor)
            .padding(16.dp)
    ) {
        Column(modifier = Modifier.verticalScroll(rememberScrollState())) {
            Text(
                text = "Terms and Conditions",
                fontSize = 22.sp,
                fontWeight = FontWeight.Bold,
                color = Color(0xFFBF0D81),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Text(
                text = """
Welcome to CarCare! By downloading or using this app, you agree to the following terms and conditions. Please read them carefully.

1. Acceptance of Terms:
By accessing and using CarCare, you agree to be bound by these Terms and Conditions. If you do not agree with any part of the terms, please do not use the app.

2. App Usage:
You may use this app only for personal, non-commercial purposes. You must not misuse the app or interfere with its normal operation.

3. Account Information:
You are responsible for maintaining the confidentiality of your login information and for all activities that occur under your account.

4. Privacy:
We respect your privacy. Your personal information will not be shared with third parties without your consent. For more details, please read our Privacy Policy.

5. Intellectual Property:
All content, logos, and trademarks in the app are the property of CarCare and are protected by copyright and intellectual property laws.

6. Modifications:
We reserve the right to modify or update these Terms at any time. Continued use of the app after changes means you accept those changes.

7. Disclaimer:
We are not responsible for any damages or losses caused by the use or inability to use this app. Use it at your own risk.

8. Contact Us:
If you have any questions or concerns about these Terms, feel free to contact us at: saadali23495097@gmail.com

Thank you for using CarCare!
""".trimIndent(),
                fontSize = 12.sp,
                lineHeight = 15.sp,
                color = Color.DarkGray
            )
        }
    }
}
