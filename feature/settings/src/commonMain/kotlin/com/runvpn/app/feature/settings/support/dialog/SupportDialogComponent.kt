package com.runvpn.app.feature.settings.support.dialog

import com.runvpn.app.tea.decompose.dialogs.SimpleDialogComponent

interface SupportDialogComponent : SimpleDialogComponent {


    fun onCopyContactClick(contact: String)
    fun onOpenTelegramClick(uri: String)

}
