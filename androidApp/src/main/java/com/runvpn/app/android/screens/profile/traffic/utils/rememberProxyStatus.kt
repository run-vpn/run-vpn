//package com.runvpn.app.android.screens.profile.traffic.utils
//
//import androidx.compose.runtime.Composable
//import androidx.compose.runtime.LaunchedEffect
//import androidx.compose.runtime.mutableStateOf
//import androidx.compose.runtime.remember
//import androidx.compose.ui.text.AnnotatedString
//import androidx.compose.ui.text.SpanStyle
//import androidx.compose.ui.text.buildAnnotatedString
//import androidx.compose.ui.text.withStyle
//import com.android.asdk.wrapper.app.logs.model.StatusRecord
//import com.runvpn.app.android.screens.profile.traffic.getLogTextColor
//import com.runvpn.app.feature.trafficmodule.TrafficModuleComponent
//
//@Composable
//internal fun rememberProxyStatus(component: TrafficModuleComponent): Pair<StatusRecord, AnnotatedString> {
//
//    val status = component.getStatus() as StatusRecord
//    val logText = remember {
//        mutableStateOf(buildAnnotatedString { append(status.status) })
//    }
//
//    val savedStatus = remember {
//        mutableStateOf(status)
//    }
//
//    LaunchedEffect(Unit) {
//        component.logsReceiver(this).collect {
//            it as StatusRecord
//            savedStatus.value = it
//            val sdkStateStr = buildAnnotatedString {
//                withStyle(style = SpanStyle(color = getLogTextColor(it.type))) {
//                    append("\n${it.status}:${it.host}")
//                }
//            }
//
//            logText.value += sdkStateStr
//        }
//    }
//    return Pair(savedStatus.value, logText.value)
//}
