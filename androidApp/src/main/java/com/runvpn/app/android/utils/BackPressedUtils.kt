package com.runvpn.app.android.utils

import android.content.Context
import android.content.ContextWrapper
import androidx.activity.ComponentActivity


fun dispatchOnBackPressed(context: Context) {
    val activity = context.getActivity() ?: return
    activity.onBackPressedDispatcher.onBackPressed()
}

private fun Context.getActivity(): ComponentActivity? = when (this) {
    is ComponentActivity -> this
    is ContextWrapper -> baseContext.getActivity()
    else -> null
}
