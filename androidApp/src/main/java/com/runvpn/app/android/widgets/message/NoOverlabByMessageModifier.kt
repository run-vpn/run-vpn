package com.runvpn.app.android.widgets.message

import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.staticCompositionLocalOf
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.layout.findRootCoordinates
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import java.util.UUID
import kotlin.math.roundToInt

val LocalMessageOffsets = staticCompositionLocalOf { mutableStateMapOf<String, Int>() }

/**
 * Allows to specify that a message (see [MessageUi]) should be displayed above an UI element.
 * For example, set noOverlapByMessage() to a bottom navigation bar, so it will not be overlapped by a message popup.
 */
fun Modifier.noOverlapByMessage(): Modifier = composed {
    val key = remember { UUID.randomUUID().toString() }
    val localMessageOffsets = LocalMessageOffsets.current

    DisposableEffect(Unit) {
        onDispose { localMessageOffsets.remove(key) }
    }

    then(
        onGloballyPositioned { layoutCoordinates ->
            if (layoutCoordinates.isAttached) {
                val rootCoordinates = layoutCoordinates.findRootCoordinates()
                val result = rootCoordinates.size.height -
                        layoutCoordinates.positionInRoot().y.roundToInt()
                localMessageOffsets[key] = result
            }
        }
    )
}
