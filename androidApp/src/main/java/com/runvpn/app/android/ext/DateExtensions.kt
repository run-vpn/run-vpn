package com.runvpn.app.android.ext

import android.icu.text.SimpleDateFormat
import kotlinx.datetime.Instant
import java.util.Date
import java.util.Locale

fun Instant.toDateLocalized(locale: Locale = Locale.getDefault()): String {
    val formatterOut = SimpleDateFormat("dd.MM.yyyy", locale)
    val date = Date(this.toEpochMilliseconds())
    return formatterOut.format(date)
}

fun Instant.toTimeLocalized(locale: Locale = Locale.getDefault()): String{
    val formatterOut = SimpleDateFormat("HH:mm", locale)
    val date = Date(this.toEpochMilliseconds())
    return formatterOut.format(date)
}
