package com.runvpn.app.android.screens.settings.connection.splittunneling

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import com.runvpn.app.feature.settings.splittunneling.ips.SplitTunnelingIpsComponent

@Composable
fun SplitTunnelingIpsScreen(component: SplitTunnelingIpsComponent) {

    Column {
        Text(text = "Split Tunneling Ips Screen")
        Button(onClick = { component.onBackClick() }) {
            Text(text = "OnBack")
        }
    }

}
