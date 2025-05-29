package com.runvpn.app.android.ext

import android.icu.text.SimpleDateFormat
import java.text.CharacterIterator
import java.text.DecimalFormat
import java.text.NumberFormat
import java.text.StringCharacterIterator
import java.util.Currency
import java.util.Date
import java.util.Locale
import kotlin.math.abs

fun Long.toTimerFormat(locale: Locale = Locale.getDefault()): String {
    val hours = this / (3600)
    val minutes = (this % 3600) / 60
    val seconds = this % 60

    return if (hours > 0) {
        String.format(
            locale,
            "%02d:%02d:%02d",
            hours,
            minutes,
            seconds,
        )
    } else {
        String.format(
            locale,
            "%02d:%02d",
            minutes,
            seconds,
        )
    }
}

val Long.toMinutes
    get() = this / (1000 * 60)

fun Long.toConnTimerFormat(
    format: String,
    locale: Locale = Locale.getDefault()
): String {
    val totSeconds = this / 1000
    val seconds = totSeconds % 60
    val minutes = (totSeconds / 60) % 60
    val hours = (totSeconds / (60 * 60))

    return if (hours > 0) {
        String.format(
            locale,
            format,
            hours,
            minutes,
            seconds,
        )
    } else if (minutes > 0) {
        val data = format.split(".")
        val newFormat = "${data[1]}.${data[2]}."
        String.format(
            locale,
            newFormat,
            minutes,
            seconds,
        )
    } else {
        val data = format.split(".")
        val newFormat = "${data[2]}."
        String.format(
            locale,
            newFormat,
            seconds,
        )
    }
}


fun Long.toDateTimeFormat(): String {
    val formatterOut = SimpleDateFormat("d MMM", Locale.US)

    return formatterOut.format(Date(this))
}

fun Long.secondToDateTimeFormat(locale: Locale = Locale.getDefault()): String {
    val formatterOut = SimpleDateFormat("d MMM", locale)

    return formatterOut.format(Date(this * 1000))
}

fun Long.secondToDateTimeFormat2(locale: Locale = Locale.getDefault()): String {
    val formatterOut = SimpleDateFormat("d MMM", locale)

    return formatterOut.format(Date(this))
}

@Suppress("kotlin:S6511")
fun Long.byteToSize(): String {
    val readableSize: String
    val k = this / 1000.0
    val m = k / 1000.0
    val g = m / 1000.0
    val t = g / 1000.0
    val dec = DecimalFormat("0.0")
    readableSize = if (t > 1) {
        dec.format(t) + " Tb"
    } else if (g > 1) {
        dec.format(g) + " Gb"
    } else if (m > 1) {
        dec.format(m) + " Mb"
    } else if (k > 1) {
        dec.format(k) + " Kb"
    } else {
        dec.format(this) + " B"
    }
    return readableSize
}

@Suppress("kotlin:S6511")
fun Long.byteToSizeRounded(): String {
    val readableSize: String
    val k = this / 1000
    val m = k.toDouble() / 1000
    val g = m / 1000
    val t = g / 1000
    val dec = DecimalFormat("0.0")
    readableSize = if (t > 1) {
        "${dec.format(t)} Tb"
    } else if (g > 1) {
        "${dec.format(g)} Gb"
    } else if (m > 1) {
        "${dec.format(m)} Mb"
    } else if (k > 1) {
        "$k Kb"
    } else {
        "$this B"
    }
    return readableSize
}


fun Float.toCurrencyFormat(locale: Locale = Locale.getDefault()): String {
    val format = NumberFormat.getCurrencyInstance(locale)
    format.currency = Currency.getInstance("USD")
    return format.format(this)
}

fun Double.toCurrencyFormat(locale: Locale = Locale.getDefault()): String {
    val format = NumberFormat.getCurrencyInstance(locale)
    format.currency = Currency.getInstance("USD")
    return format.format(this)
}

val Long.toHumanReadableByteCountSI: String
    get() {
        var bytes = this
        if (-1000 < bytes && bytes < 1000) {
            return "${bytes}B"
        }
        val ci: CharacterIterator = StringCharacterIterator("kMGTPE")
        while (bytes <= -999950 || bytes >= 999950) {
            bytes /= 1000
            ci.next()
        }
        return String.format(Locale.ENGLISH, "%.1f%cb", bytes / 1000.0, ci.current())
    }

val Long.toHumanReadableByteCountBin: String
    get() {
        val bytes = this
        val absB = if (bytes == Long.MIN_VALUE) Long.MAX_VALUE else abs(bytes)
        if (absB < 1024) {
            return "$bytes B"
        }
        var value = absB
        val ci: CharacterIterator = StringCharacterIterator("KMGTPE")
        var i = 40
        while (i >= 0 && absB > 0xfffccccccccccccL shr i) {
            value = value shr 10
            ci.next()
            i -= 10
        }
        value *= java.lang.Long.signum(bytes).toLong()
        return String.format(Locale.ENGLISH, "%.1f %ciB", value / 1024.0, ci.current())
    }

fun Float.round(decimals: Int): Float {
    var multiplier = 1.0f
    repeat(decimals) { multiplier *= 10 }
    return kotlin.math.round(this * multiplier) / multiplier
}
