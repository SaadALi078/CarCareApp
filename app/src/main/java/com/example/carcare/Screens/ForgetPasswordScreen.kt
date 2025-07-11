import android.widget.Toast
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.carcare.data.ForgetPasswordViewModel
import com.example.carcare.navigation.BackHandler
import com.example.carcare.navigation.Router
import com.example.carcare.navigation.Screen

@Composable
fun ForgetPasswordScreen(
    onNavigateBack: () -> Unit = {},
) {
    val context = LocalContext.current
    val viewModel: ForgetPasswordViewModel = viewModel()
    var email by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }
    val primaryColor = Color(0xFFBF0D81)

    BackHandler {
        Router.navigateTo(Screen.LoginScreen)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.White)
            .padding(16.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(top = 8.dp),
            contentAlignment = Alignment.TopEnd
        ) {
            Text(
                text = "← Back",
                color = primaryColor,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                modifier = Modifier
                    .clickable { Router.navigateTo(Screen.LoginScreen) }
                    .padding(8.dp)
            )
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Forgot Password",
                color = primaryColor,
                fontSize = 28.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 20.dp)
            )

            OutlinedTextField(
                value = email,
                onValueChange = { email = it },
                label = { Text("Enter your registered email") },
                singleLine = true,
                shape = RoundedCornerShape(percent = 50),
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (email.isNotBlank()) {
                        isLoading = true
                        viewModel.sendPasswordResetEmail(email) { success, message ->
                            isLoading = false
                            Toast.makeText(context, message ?: "", Toast.LENGTH_LONG).show()
                            if (success) {
                                Router.navigateTo(Screen.LoginScreen)
                            }
                        }
                    } else {
                        Toast.makeText(context, "Email cannot be empty", Toast.LENGTH_SHORT).show()
                    }
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isLoading,
                colors = ButtonDefaults.buttonColors(
                    containerColor = primaryColor,
                    contentColor = Color.White
                )
            ) {
                if (isLoading) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = Color.White
                    )
                } else {
                    Text(text = "Send Reset Email")
                }
            }
        }
    }
}