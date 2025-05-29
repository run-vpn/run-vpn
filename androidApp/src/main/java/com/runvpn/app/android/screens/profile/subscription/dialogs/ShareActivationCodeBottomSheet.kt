package com.runvpn.app.android.screens.profile.subscription.dialogs

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.runvpn.app.android.R
import com.runvpn.app.android.widgets.AppBottomSheet
import com.runvpn.app.android.widgets.AppButton
import com.runvpn.app.core.ui.colorIconAccent
import com.runvpn.app.core.ui.hintTextColor
import com.runvpn.app.core.ui.textInputBackgroundColor
import com.runvpn.app.core.ui.textWhite
import com.runvpn.app.feature.subscription.activate.sharecodedialog.FakeShareActivationCodeComponent
import com.runvpn.app.feature.subscription.activate.sharecodedialog.ShareActivationCodeComponent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ShareActivationCodeBottomSheet(component: ShareActivationCodeComponent) {

    val state by component.state.subscribeAsState()

    AppBottomSheet(
        title = stringResource(id = R.string.activation_code),
        onDismiss = component::onDismissClicked
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        ) {
            Text(
                text = stringResource(R.string.activation_code_desc_dialog),
                color = hintTextColor,
                fontSize = 14.sp,
                lineHeight = 16.sp
            )

            Spacer(modifier = Modifier.height(24.dp))

            Row(modifier = Modifier
                .fillMaxWidth()
                .background(textInputBackgroundColor, RoundedCornerShape(8.dp))
                .clip(RoundedCornerShape(8.dp))
                .clickable { component.onCopyLinkClick() }
                .padding(horizontal = 12.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically) {

                Text(
                    text = state.code,
                    fontSize = 14.sp,
                    fontWeight = FontWeight(400),
                    color = Color.Black,
                    modifier = Modifier.weight(1f)
                )

                Spacer(modifier = Modifier.width(12.dp))

                Image(
                    painter = painterResource(id = R.drawable.ic_copy_clipboard),
                    contentDescription = ""
                )
            }

            Spacer(modifier = Modifier.height(20.dp))

            AppButton(
                onClick = { /*TODO*/ },
                modifier = Modifier.fillMaxWidth(),
                containerColor = textWhite,
                contentColor = colorIconAccent
            ) {
                Text(text = stringResource(R.string.usage_instructions), fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(12.dp))

            AppButton(
                onClick = {},
                modifier = Modifier.fillMaxWidth(),
                containerColor = colorIconAccent,
                contentColor = textWhite
            ) {
                Icon(painterResource(id = R.drawable.ic_link), contentDescription = null)

                Spacer(modifier = Modifier.width(4.dp))

                Text(text = stringResource(R.string.share), fontSize = 16.sp)
            }
        }

        Spacer(modifier = Modifier.height(20.dp))

    }
}


@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
private fun ShareActivationCodeBottomSheetPreview() {
    ShareActivationCodeBottomSheet(FakeShareActivationCodeComponent())
}
