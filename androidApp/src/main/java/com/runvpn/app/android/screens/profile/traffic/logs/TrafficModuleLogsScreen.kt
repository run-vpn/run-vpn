package com.runvpn.app.android.screens.profile.traffic.logs

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.runvpn.app.android.R
import com.runvpn.app.android.RunVpnTheme
import com.runvpn.app.android.widgets.AppToolBar
import com.runvpn.app.feature.trafficmodule.FakeTrafficModuleComponent
import com.runvpn.app.feature.trafficmodule.TrafficModuleComponent

@Composable
fun TrafficModuleLogsScreen(component: TrafficModuleComponent, modifier: Modifier = Modifier) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .fillMaxSize()
    ) {

        AppToolBar(
            onBackClick = { component.onBack() },
            title = stringResource(id = R.string.tfs_logs_title),
            subtitle = stringResource(
                id = R.string.tfs_logs_desc
            )
        )

        val dbLogs = component.getDbLogs()
//        val baseLogsStr = buildAnnotatedString {
//            dbLogs.forEach {
//                it as StatusRecord
//                withStyle(style = SpanStyle(color = getLogTextColor(it.type))) {
//                    append("\n$it")
//                }
//            }
//        }
//
//        Log(component, baseLogsStr)
    }
}

@Composable
private fun Log(component: TrafficModuleComponent, logTextInit: AnnotatedString) {
    val logText = remember {
        mutableStateOf(buildAnnotatedString { append(logTextInit) })
    }

//    LaunchedEffect(Unit) {
//        component.logsReceiver(this).collect {
//            val sdkStateStr = buildAnnotatedString {
//                it as StatusRecord
//                withStyle(style = SpanStyle(color = getLogTextColor(it.type))) {
//                    append("\n$it")
//                }
//            }
//
//            logText.value += sdkStateStr
//        }
//    }

    val logScroll = rememberScrollState()

    LaunchedEffect(logText.value) {
        logScroll.scrollTo(logScroll.maxValue)
    }
    Row(modifier = Modifier.fillMaxHeight()) {
        Text(
            text = logText.value,
            fontSize = 14.sp,
            textAlign = TextAlign.Left,
            modifier = Modifier
                .padding(horizontal = 16.dp)
                .fillMaxWidth()
                .fillMaxHeight()
                .verticalScroll(logScroll)
        )

    }
}


@Preview(showBackground = true, backgroundColor = 0xFFFFFFFF)
@Composable
fun TrafficModuleLogsScreenPreview() {
    RunVpnTheme {
        TrafficModuleLogsScreen(FakeTrafficModuleComponent())
    }
}
