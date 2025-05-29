package com.runvpn.app.android.widgets

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.runvpn.app.android.RunVpnTheme
import com.runvpn.app.core.ui.cardBackgroundColor
import com.runvpn.app.core.ui.shadowAmbientColor
import com.runvpn.app.core.ui.shadowSpotColor


@Composable
fun ElevatedClickableContainerView(
    modifier: Modifier = Modifier,
    radius: Dp = 8.dp,
    containerColor: Color = cardBackgroundColor,
    onClick: () -> Unit,
    content: @Composable () -> Unit
) {
    ElevatedCard(
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
        ),
        modifier = modifier
            .padding(vertical = 5.dp)
            .fillMaxWidth()
            .clip(RoundedCornerShape(radius))
            .shadow(
                elevation = 20.dp,
                spotColor = shadowSpotColor,
                ambientColor = shadowAmbientColor
            )
            .clickable { onClick() }

    ) {
        content()
    }
}


@Preview(showBackground = true, backgroundColor = 0xFFCDCDCD)
@Composable
private fun ElevatedContainerViewPreview() {
    RunVpnTheme {
        ElevatedClickableContainerView(modifier = Modifier.padding(16.dp), onClick = { /*TODO*/ }) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("ElevatedClickableContainerView")
                Text("ElevatedClickableContainerView")
                Text("ElevatedClickableContainerView")
            }
        }
    }
}
