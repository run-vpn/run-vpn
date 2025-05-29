package com.runvpn.app.android.widgets

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.runvpn.app.android.R
import com.runvpn.app.android.RunVpnTheme
import com.runvpn.app.android.utils.WidgetPreview
import com.runvpn.app.core.ui.disabledTextHintColor
import com.runvpn.app.core.ui.textErrorColor
import com.runvpn.app.core.ui.textHintColor
import com.runvpn.app.core.ui.textInputBackgroundColor
import com.runvpn.app.core.ui.textInputErrorBackgroundColor

@Composable
fun AppTextFieldPassword(
    modifier: Modifier = Modifier,
    value: String,
    onValueChanged: (String) -> Unit,
    placeholder: String = "",
    disabledPlaceholderColor: Color = disabledTextHintColor,
    singleLine: Boolean = true,
    placeHolderColor: Color = textHintColor,
    isError: Boolean = false,
    errorMessage: String = "",
    keyboardOptions: KeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Password),
    visibility: MutableState<Boolean> = remember { mutableStateOf(false) }
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChanged,
        placeholder = {
            Text(
                text = placeholder,
                color = if (isError) textErrorColor else placeHolderColor
            )
        },
        shape = RoundedCornerShape(8.dp),
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,
            focusedContainerColor = textInputBackgroundColor,
            unfocusedContainerColor = textInputBackgroundColor,
            disabledPlaceholderColor = disabledPlaceholderColor,
            disabledContainerColor = textInputBackgroundColor,
            errorContainerColor = textInputErrorBackgroundColor,
        ),
        keyboardOptions = keyboardOptions,
        textStyle = TextStyle(
            fontSize = TextUnit(14f, TextUnitType.Sp)
        ),
        singleLine = singleLine,
        trailingIcon = {
            IconButton(onClick = { visibility.value = !visibility.value }) {
                val visibilityIcon =
                    if (visibility.value) ImageVector.vectorResource(id = R.drawable.ic_visibility_off)
                    else ImageVector.vectorResource(id = R.drawable.ic_visiblity_on)
                //provide localized description for accessibility services
                val description = if (visibility.value) "Show password" else "Hide password"
                Icon(imageVector = visibilityIcon, contentDescription = description)
            }
        },
        visualTransformation = if (visibility.value) VisualTransformation.None else PasswordVisualTransformation(),
        modifier = modifier,
        isError = isError
    )
    if (isError && errorMessage.isNotEmpty()) {
        Text(
            text = errorMessage,
            color = Color.Red,
            fontSize = 12.sp,
            modifier = Modifier.padding(start = 16.dp)
        )
    }
}

@WidgetPreview
@Composable
private fun AppTextFieldPasswordVisiblePreview() {
    RunVpnTheme {
        Box(
            modifier = Modifier.padding(32.dp)
        ) {
            AppTextFieldPassword(
                value = "123456",
                placeholder = "",
                onValueChanged = {},
                isError = false,
                visibility = remember { mutableStateOf(true) }
            )
        }
    }
}

@WidgetPreview
@Composable
private fun AppTextFieldPasswordPreview() {
    RunVpnTheme {
        Box(
            modifier = Modifier.padding(32.dp)
        ) {
            AppTextFieldPassword(
                value = "123456",
                placeholder = "",
                onValueChanged = {},
                isError = false
            )
        }
    }
}
