package com.runvpn.app.android.screens.profile.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.painter.Painter
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.runvpn.app.android.RunVpnTheme
import com.runvpn.app.android.R
import com.runvpn.app.android.widgets.ElevatedContainerView
import com.runvpn.app.core.ui.profileCardsBackgroundColor
import com.runvpn.app.core.ui.textSecondaryColor

@Composable
fun ProfileClickableElement(
    painter: Painter,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {

    ElevatedContainerView(
        modifier = modifier
            .padding(horizontal = 12.dp, vertical = 16.dp),
        containerColor = profileCardsBackgroundColor
    ) {

        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Icon(painter = painter, contentDescription = null, tint = Color.Unspecified)

            Spacer(modifier = Modifier.height(2.dp))

            Text(text = title, fontSize = 14.sp, fontWeight = FontWeight(500))

            Text(text = subtitle, fontSize = 10.sp, color = textSecondaryColor)
        }
    }
}

@Preview
@Composable
fun ProfileClickableElementPreview() {
    RunVpnTheme {
        ProfileClickableElement(
            painter = painterResource(id = R.drawable.ic_tab_profile),
            title = "Referral system",
            subtitle = "Some subtitle for referral system plashka"
        )
    }
}
