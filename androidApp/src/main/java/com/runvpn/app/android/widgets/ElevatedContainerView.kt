package com.runvpn.app.android.widgets

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
fun ElevatedContainerView(
    modifier: Modifier = Modifier,
    radius: Dp = 8.dp,
    elevation: Dp = 0.dp,
    containerColor: Color = cardBackgroundColor,
    content: @Composable () -> Unit
) {
    ElevatedCard(
        colors = CardDefaults.cardColors(
            containerColor = containerColor,
        ),
        modifier = modifier
            .shadow(
                elevation = elevation,
                spotColor = shadowSpotColor,
                ambientColor = shadowAmbientColor,
                shape = RoundedCornerShape(radius)
            )
            .clip(RoundedCornerShape(radius))
            .fillMaxWidth()

    ) {
        content()
    }
}


@Preview(showBackground = true, backgroundColor = 0xFFD2D2D2)
@Composable
private fun ElevatedContainerViewPreview() {
    RunVpnTheme {
        ElevatedContainerView(modifier = Modifier.padding(16.dp)) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text("ElevatedContainerView")
                Text("ElevatedContainerView")
                Text("ElevatedContainerView")
            }
        }
    }
}
