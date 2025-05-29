package com.runvpn.app.android.widgets

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.TextUnitType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.runvpn.app.android.RunVpnTheme
import com.runvpn.app.android.utils.WidgetPreview
import com.runvpn.app.core.ui.disabledTextHintColor
import com.runvpn.app.core.ui.textErrorColor
import com.runvpn.app.core.ui.textHintColor
import com.runvpn.app.core.ui.textInputBackgroundColor
import com.runvpn.app.core.ui.textInputErrorBackgroundColor

@Composable
fun AppTextField(
    modifier: Modifier = Modifier,
    value: String,
    placeholder: String,
    onValueChanged: (String) -> Unit,
    isError: Boolean = false,
    singleLine: Boolean = true,
    enabled: Boolean = true,
    readOnly: Boolean = false,
    errorMessage: String? = null,
    interactionSource: MutableInteractionSource = remember {
        MutableInteractionSource()
    },
    placeHolderColor: Color = textHintColor,
    focusedIndicatorColor: Color = Color.Transparent,
    unfocusedIndicatorColor: Color = Color.Transparent,
    disabledPlaceholderColor: Color = disabledTextHintColor,
    disabledTextColor: Color = disabledTextHintColor,
    focusedContainerColor: Color = textInputBackgroundColor,
    unfocusedContainerColor: Color = textInputBackgroundColor,
    textStyle: TextStyle = TextStyle(
        fontSize = TextUnit(14f, TextUnitType.Sp)
    ),
    maxLines: Int = if (singleLine) 1 else Int.MAX_VALUE,
    visualTransformation: VisualTransformation = VisualTransformation.None,
    keyboardOptions: KeyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Text),
    keyboardActions: KeyboardActions = KeyboardActions(),
    leadingIcon: (@Composable () -> Unit)? = null,
    trailingIcon: (@Composable () -> Unit)? = null
) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChanged,
        placeholder = {
            Text(
                text = placeholder, color = if (isError) textErrorColor else placeHolderColor
            )
        },
        readOnly = readOnly,
        shape = RoundedCornerShape(8.dp),
        interactionSource = interactionSource,
        colors = TextFieldDefaults.colors(
            focusedIndicatorColor = focusedIndicatorColor,
            unfocusedIndicatorColor = unfocusedIndicatorColor,
            disabledIndicatorColor = Color.Transparent,
            focusedContainerColor = focusedContainerColor,
            unfocusedContainerColor = unfocusedContainerColor,
            disabledPlaceholderColor = disabledPlaceholderColor,
            disabledContainerColor = unfocusedContainerColor,
            errorContainerColor = textInputErrorBackgroundColor,
            disabledTextColor = disabledTextColor
        ),
        visualTransformation = visualTransformation,
        keyboardOptions = keyboardOptions,
        keyboardActions = keyboardActions,
        textStyle = textStyle,
        isError = isError,
        leadingIcon = leadingIcon,
        trailingIcon = trailingIcon,
        singleLine = singleLine,
        enabled = enabled,
        maxLines = maxLines,
        modifier = modifier,
    )
    if (isError && errorMessage != null) {
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
private fun AppTextFieldPreview() {
    RunVpnTheme {
        Box(
            modifier = Modifier.padding(32.dp)
        ) {
            AppTextField(value = "", placeholder = "", onValueChanged = {})
        }
    }
}
