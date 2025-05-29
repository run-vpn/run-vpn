package com.runvpn.app.android.screens.common

import android.view.ViewGroup
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.viewinterop.AndroidView
import com.runvpn.app.feature.common.browser.FakeBrowserComponent
import com.runvpn.app.feature.common.browser.BrowserComponent

@Composable
fun BrowserScreen(component: BrowserComponent, modifier: Modifier = Modifier) {

    AndroidView(
        modifier = modifier,
        factory = {
            WebView(it).apply {
                layoutParams = ViewGroup.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.MATCH_PARENT
                )
                webViewClient = WebViewClient()
                loadUrl(component.url)
            }
        }, update = {
            it.loadUrl(component.url)
        })
}

@Preview(showBackground = true, backgroundColor = 0xFFEDEDED)
@Composable
fun SubscriptionScreenPreview() {
    BrowserScreen(FakeBrowserComponent("https://ya.ru"))
}
