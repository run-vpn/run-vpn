package com.runvpn.app.android.widgets

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.runvpn.app.android.R
import com.runvpn.app.core.ui.colorIconAccent
import com.runvpn.app.core.ui.textWhite

@Composable
fun ConfirmationElement(
    modifier: Modifier = Modifier,
    message: String,
    onConfirm: () -> Unit
) {
    Column(
        modifier = modifier
            .padding(horizontal = 16.dp)
            .padding(bottom = 24.dp)
    ) {

        Text(text = message, fontSize = 16.sp, lineHeight = 18.sp)

        Spacer(modifier = Modifier.height(24.dp))

        AppButton(
            onClick = onConfirm,
            containerColor = colorIconAccent,
            contentColor = textWhite,
            modifier = Modifier.fillMaxWidth()
        ) {
            Text(text = stringResource(R.string.confirm))
        }
    }
}

@Preview(showSystemUi = false, showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun ConfirmationElementPreview() {
    ConfirmationElement(
        modifier = Modifier.fillMaxWidth(),
        message = "Are your confirm?",
        {}
    )
}
