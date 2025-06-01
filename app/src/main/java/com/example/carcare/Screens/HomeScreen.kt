package com.example.carcare.Screens
import com.example.carcare.R
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.carcare.Component.ButtonComponent
import com.example.carcare.Component.HeadingTextComponent
import com.example.carcare.Data.SignupViewModel

@Composable
fun HomeScreen(loginViewModel: SignupViewModel= viewModel()){
    Surface(
        color = Color.White,
        modifier = Modifier
            .fillMaxSize()
            .padding(28.dp)
    ) {
        Column(modifier = Modifier.fillMaxSize()) {
            HeadingTextComponent(value = "Home")
ButtonComponent(value = stringResource(id = R.string.logout),
    onButtonClicked = {
        loginViewModel.logout()

},
    isEnabled = true

    )

        }
    }

}
