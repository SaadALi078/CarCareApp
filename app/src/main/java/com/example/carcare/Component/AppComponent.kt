package com.example.carcare.Component
import android.R.attr.icon
import android.R.id.icon
import android.content.res.Resources
import android.icu.number.NumberFormatter
import android.inputmethodservice.Keyboard
import android.util.Log
import androidx.compose.foundation.Image

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.ui.graphics.Color
import com.example.carcare.R
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height


import androidx.compose.material3.OutlinedTextFieldDefaults

import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.ClickableText
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CheckboxDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.isTraceInProgress
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.unit.dp
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush

import androidx.compose.ui.graphics.Color.Companion.White
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.node.Ref
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.unit.sp
import com.example.carcare.ui.theme.TextColor
import androidx.compose.ui.res.colorResource
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.LineHeightStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle

import com.example.carcare.ui.theme.GrayColor
import com.example.carcare.ui.theme.Secondary
import com.example.carcare.ui.theme.primary


@Composable
fun NormalTextComponent(value: String){
    Text(
         text = value,
        modifier = Modifier.fillMaxWidth().heightIn(min = 40 .dp),
       style = androidx.compose.ui.text.TextStyle(
           fontSize = 24.sp,
           fontStyle = FontStyle.Normal,
           fontWeight = FontWeight.Normal



       )
        , color = colorResource(R.color.colorWhite ),
        textAlign = TextAlign.Center
    )

}
@Composable
fun HeadingTextComponent(value: String){
    Text(
        text = value,
        modifier = Modifier.fillMaxWidth().heightIn(),
        style = androidx.compose.ui.text.TextStyle(
            fontSize = 35.sp,
            fontStyle = FontStyle.Normal,
            fontWeight = FontWeight.Bold,
            fontFamily = FontFamily.SansSerif,



        )
        , color = Color(0xFFBF0D81),
        textAlign = TextAlign.Center
    )

}
@Composable
fun MyTextField(
    labelValue: String,
    painterResources: Painter,
    onTextSelected: (String) -> Unit,
    errorStatus: Boolean = false,
    errorMessage: String = ""
) {
    val textValue = remember { mutableStateOf("") }

    Column(modifier = Modifier.fillMaxWidth()) {
        OutlinedTextField(
            value = textValue.value,
            onValueChange = {
                textValue.value = it
                onTextSelected(it)
            },
            modifier = Modifier
                .fillMaxWidth()
                .height(62.dp)
                .clip(RoundedCornerShape(4.dp)),
            label = {
                Text(
                    text = labelValue,
                    color = Color(0xFFDCD7D7) // 💡 Label text color
                )
            },
            textStyle = androidx.compose.ui.text.TextStyle( // 💡 Important part
                color = Color(0xFFDCD7D7),
                fontSize = 16.sp
            ),
            leadingIcon = {
                Icon(
                    painter = painterResources,
                    contentDescription = null,
                    tint = Color(0xFFDCD7D7),//, icon color
                    modifier = Modifier.size(20.dp)
                )
            },
            shape = RoundedCornerShape(percent = 50),
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = Color(0xFF2D2C2C),   // when text field is focused
                unfocusedContainerColor = Color(0xFF2D2C2C), // when not focused

                focusedBorderColor = colorResource(R.color.colorGray),
                unfocusedBorderColor = colorResource(R.color.colorGray),
                disabledBorderColor = colorResource(R.color.colorGray),
                errorBorderColor = Color(0xFFFF5C5C), // red color for errors

                focusedTextColor = Color.Black,
                unfocusedTextColor = Color.Black,

                focusedPlaceholderColor = Color.DarkGray,
                unfocusedPlaceholderColor = Color.DarkGray,

                focusedLeadingIconColor = Color.DarkGray,
                unfocusedLeadingIconColor = Color.DarkGray
            ),
            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
            isError = !errorStatus,
            singleLine = true,
            maxLines = 1
        )

        if (!errorStatus && errorMessage.isNotEmpty()) {
            Text(
                text = errorMessage,
                color = Color(0xFFFF5C5C),
                fontSize = 12.sp,
                modifier = Modifier.padding(start = 16.dp, top = 4.dp)
            )
        }
    }
}



@Composable
fun passwordTextField(
    labelValue: String,
    painterResources: Painter,
    onTextSelected: (String) -> Unit,
    errorStatus: Boolean=false// ✅ add this line
) {
    val password = remember {
        mutableStateOf(value = "")
    }

    val passwordVisible = remember {
        mutableStateOf(false)
    }

    OutlinedTextField(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(size = 4.dp)),
        label = { Text(text = labelValue,
                color = Color(0xFFDCD7D7))
                },

        value = password.value,
        shape = RoundedCornerShape(percent = 50),
        colors = OutlinedTextFieldDefaults.colors(
            focusedContainerColor = Color(0xFF2D2C2C),   // when text field is focused
            unfocusedContainerColor = Color(0xFF2D2C2C), // when not focused

            focusedBorderColor = colorResource(R.color.colorGray),
            unfocusedBorderColor = colorResource(R.color.colorGray),
            disabledBorderColor = colorResource(R.color.colorGray),
            errorBorderColor = Color(0xFFFF5C5C), // red color for errors

            focusedTextColor = Color.Black,
            unfocusedTextColor = Color.Black,

            focusedPlaceholderColor = Color.DarkGray,
            unfocusedPlaceholderColor = Color.DarkGray,

            focusedLeadingIconColor = Color.DarkGray,
            unfocusedLeadingIconColor = Color.DarkGray
        ),

                keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = ImeAction.Done
        ),
        singleLine = true,
        maxLines = 1,
        onValueChange = {
            password.value = it
            onTextSelected(it) // ✅ now this will work fine
        },
        textStyle = androidx.compose.ui.text.TextStyle( // 💡 Important part
            color = Color(0xFFDCD7D7),
            fontSize = 16.sp
        ),
        leadingIcon = {
            Icon(
                painter = painterResources,
                tint = Color(0xFFDCD7D7),
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
        },
        trailingIcon = {
            val iconImage = if (passwordVisible.value) {
                Icons.Filled.Visibility
            } else {
                Icons.Filled.VisibilityOff
            }

            val description = if (passwordVisible.value) {
                stringResource(id = R.string.hide_passowrd)
            } else {
                stringResource(id = R.string.show_password)
            }

            IconButton(onClick = { passwordVisible.value = !passwordVisible.value }) {
                Icon(imageVector = iconImage, contentDescription = description)

            }
        },
        isError = errorStatus,
        visualTransformation = if (passwordVisible.value) VisualTransformation.None
        else PasswordVisualTransformation()

    )
    if (errorStatus) {
        Text(
            text = "Password must be 8+ chars, include upper, lower, number & special char.",
            color = Color(0xFFFF5C5C),
            fontSize = 12.sp
        )
    }

}
@Composable
fun CheckboxComponent(
    value: String,
    onTextSelected: (String) -> Unit,
    onCheckedChange: (Boolean) -> Unit
) {
    val checkState = remember { mutableStateOf(false) }

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(56.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Checkbox(
            checked = checkState.value,
            onCheckedChange = {
                checkState.value = it
                onCheckedChange.invoke(it)
            },
            colors = CheckboxDefaults.colors(
                checkedColor = Color(0xFFBF0D81),      // Box fill when checked
                uncheckedColor = Color(0xFFBF0D81),    // Border color when unchecked
                checkmarkColor = Color.White           // Tick mark color
            )
        )
        Spacer(modifier = Modifier.width(8.dp))
        ClickableTextComponent(value = value, onTextSelected = onTextSelected)
    }
}

@Composable
fun ClickableTextComponent(value: String, onTextSelected: (String) -> Unit) {
    val initialText = "By continuing you accept our "
    val privacyPolicyText = "Privacy Policy"
    val andText = " and "
    val termAndConditionsText = "Terms of Use"

    val annotatedString = buildAnnotatedString {
        // Initial non-clickable white text
        withStyle(style = SpanStyle(color = Color.White)) {
            append(initialText)
        }

        // Privacy Policy clickable pink text
        withStyle(style = SpanStyle(color = Color(0xFFBF0D81))) {
            pushStringAnnotation(tag = "privacy_policy", annotation = privacyPolicyText)
            append(privacyPolicyText)
            pop()
        }

        // ' and ' non-clickable white text
        withStyle(style = SpanStyle(color = Color.White)) {
            append(andText)
        }

        // Terms of Use clickable pink text
        withStyle(style = SpanStyle(color = Color(0xFFBF0D81))) {
            pushStringAnnotation(tag = "terms_of_use", annotation = termAndConditionsText)
            append(termAndConditionsText)
            pop()
        }
    }

    ClickableText(
        text = annotatedString,
        onClick = { offset ->
            annotatedString.getStringAnnotations(start = offset, end = offset)
                .firstOrNull()?.let { annotation ->
                    onTextSelected(annotation.item)
                }
        }
    )
}
@Composable
fun ClickableUnderlineTextComponent(value: String, onClick: () -> Unit) {
    Text(
        text = value,
        color = Color.White,
        textDecoration = TextDecoration.Underline,
        modifier = Modifier.clickable { onClick() }
    )
}
@Composable
fun ButtonComponent(value: String,onButtonClicked:()-> Unit,isEnabled : Boolean=false){
    Button(onClick =  {

        onButtonClicked.invoke()

    },
    modifier = Modifier.fillMaxWidth()
        .heightIn(48.dp),
        contentPadding = PaddingValues(),
        enabled = isEnabled,


        colors = ButtonDefaults.buttonColors(Color.Transparent),

    )  {
        Box(modifier = Modifier
            .fillMaxWidth()
            .heightIn(48.dp)
            .background(
                color = Color(0xFFBF0D81)


            ),

            contentAlignment = Alignment.Center

            )
        {
            Text(text = value,
                fontSize = 18.sp,
                color = Color.White,
                fontWeight = FontWeight.Bold)
        }
        }


}
@Composable
fun DividerTextComponent() {
    Row(modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically){
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            color = White,
            thickness = 1.dp
        )

        Text(text = "or", fontSize = 18.sp, color = White,
            modifier = Modifier.padding(7.dp))
        Divider(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f),
            color = White,
            thickness = 1.dp
        )
    }
}
@Composable
fun ClickableLoginTextComponent(tryingToLogin: Boolean=true, onTextSelected:(String)-> Unit) {
    // Define the text parts
    val initialText =if (tryingToLogin) "Already have an account? "
    else
    {"Don't have an account yet?"}

    val LoginText = if (tryingToLogin)"Login" else "Register"

    // Building the annotated string
    val annotatedString = buildAnnotatedString {
        append(initialText)

        // Privacy Policy clickable part
        withStyle(style = SpanStyle(color = Color(0xFFc451c9))) {
            pushStringAnnotation(tag = LoginText, annotation = LoginText)
            append(LoginText)
        }

    }

    // Make the text clickable
    ClickableText(
        modifier = Modifier.fillMaxWidth().heightIn(min = 40 .dp),
        style = androidx.compose.ui.text.TextStyle(
            fontSize = 21.sp,
            fontStyle = FontStyle.Italic,
            fontWeight = FontWeight.Normal,
            textAlign = TextAlign.Center,
                    color = Color.White



        ),

        text = annotatedString,
        onClick = { offset ->
            // Find the clicked part of the text and handle accordingly
            annotatedString.getStringAnnotations(offset, offset).firstOrNull()?.
            also { span ->
                // Handle Privacy Policy Click (e.g., navigate to Privacy Policy screen)
                Log.d("ClickableTextComponent", "{$span}")
                if (span.item==LoginText  ){
                    onTextSelected(span.item)
                }
            }



        }
    )
}
@Composable
fun UnderlineTextComponent(value: String){
    Text(
        text = value,
        modifier = Modifier.fillMaxWidth().heightIn(min = 40 .dp),
        style = androidx.compose.ui.text.TextStyle(
            fontSize = 16.sp,
            fontStyle = FontStyle.Normal,
            fontWeight = FontWeight.Normal



        )
        , color = Color.White,
        textAlign = TextAlign.Center,
        textDecoration = TextDecoration.Underline
    )

}
