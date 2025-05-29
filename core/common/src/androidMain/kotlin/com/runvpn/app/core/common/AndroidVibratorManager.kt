package com.runvpn.app.core.common

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator

class AndroidVibratorManager(private val context: Context) : VibratorManager {

    companion object {
        private const val DEFAULT_VIBRATE_MILLIS = 100L
        private const val DEFAULT_AMPLITUDE = 50
    }

    @SuppressLint("MissingPermission")
    override fun vibrate() {
        val vibrator = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            val vibratorManager = (context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE)
                    as? android.os.VibratorManager)
            vibratorManager?.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            val vibrator = (
                    context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                    )
            vibrator
        } ?: return

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            vibrator.vibrate(
                VibrationEffect.createOneShot(
                    DEFAULT_VIBRATE_MILLIS,
                    DEFAULT_AMPLITUDE)
            )
        } else {
            @Suppress("DEPRECATION")
            vibrator.vibrate(DEFAULT_VIBRATE_MILLIS)
        }
    }
}
