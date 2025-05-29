package com.runvpn.app.android.ext

import android.app.Activity
import android.content.ContentResolver
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Parcelable
import android.os.RemoteException
import android.provider.OpenableColumns
import android.provider.Settings
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.FileProvider
import androidx.core.os.BundleCompat
import java.io.BufferedReader
import java.io.File
import java.io.FileReader
import java.io.IOException
import java.io.InputStreamReader


inline fun <reified T : Parcelable> Intent.getParcelableExtraCompat(name: String): T? {
    val extras = this.extras ?: return null

    return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        BundleCompat.getParcelable(extras, name, T::class.java)
    } else {
        getParcelableExtra(name) as? T
    }
}

fun Context.readFile(uri: Uri): String? {
    try {
        val conf = contentResolver.openInputStream(uri)

        val isr = InputStreamReader(conf)
        val br = BufferedReader(isr)

        var config = ""
        var line: String?

        while (true) {
            line = br.readLine()
            if (line == null) break
            config += "$line\n"
        }
        br.readLine()
        return config
    } catch (e: IOException) {
        e.printStackTrace()
    } catch (e: RemoteException) {
        e.printStackTrace()
    }
    return null
}

fun Context.getFileName(uri: Uri): String? = when (uri.scheme) {
    ContentResolver.SCHEME_CONTENT -> getContentFileName(uri)
    else -> uri.path?.let(::File)?.name
}

fun readFileContent(filePath: String): String? {
    val file = File(filePath)
    val stringBuilder = StringBuilder()

    try {
        val reader = BufferedReader(FileReader(file))
        var line: String? = reader.readLine()

        while (line != null) {
            stringBuilder.append(line).append("\n")
            line = reader.readLine()
        }
    } catch (e: Exception) {
        e.printStackTrace()
        return null
    }

    return stringBuilder.toString()
}

private fun Context.getContentFileName(uri: Uri): String? = runCatching {
    contentResolver.query(uri, null, null, null, null)?.use { cursor ->
        cursor.moveToFirst()
        return@use cursor.getColumnIndexOrThrow(OpenableColumns.DISPLAY_NAME).let(cursor::getString)
    }
}.getOrNull()

fun Context.findActivity(): Activity? {
    var context = this
    while (context is ContextWrapper) {
        if (context is Activity) return context
        context = context.baseContext
    }
    return null
}

fun Context.installUpdate(fileName: String) {
    val intent = Intent(Intent.ACTION_VIEW)
    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
    val uri = FileProvider.getUriForFile(
        this,
        this.packageName + ".update.provider",
        File(this.cacheDir.toString() + "/update" + "/$fileName")
    )
    intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION)
    intent.setDataAndType(
        uri, "application/vnd.android.package-archive"
    )
    this.startActivity(intent)
}

fun Context.openAppSettings() {
    val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
        data = Uri.fromParts("package", packageName, null)
    }
    startActivity(intent)
}

fun Context.isNotificationsAllowed() =
    NotificationManagerCompat.from(this).areNotificationsEnabled()
