package com.runvpn.app.core.common

import android.content.ClipData
import android.content.Context

class AndroidClipboardManager(private val context: Context) : ClipboardManager {

    override fun copy(value: String) {
        val clipboard =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        val clip = ClipData.newPlainText("tag", value)
        clipboard.setPrimaryClip(clip)
    }

    override fun paste(): String? {
        val clipboard =
            context.getSystemService(Context.CLIPBOARD_SERVICE) as android.content.ClipboardManager
        return clipboard.primaryClip?.getItemAt(0)?.text?.toString()
    }

}
