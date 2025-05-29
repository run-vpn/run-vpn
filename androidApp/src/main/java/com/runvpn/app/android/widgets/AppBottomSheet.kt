package com.runvpn.app.android.widgets

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.BottomSheetDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.SheetState
import androidx.compose.material3.SheetValue
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalInspectionMode
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.runvpn.app.core.ui.bottomSheetScrimColor
import com.runvpn.app.core.ui.cardBackgroundColor

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppBottomSheet(
    modifier: Modifier = Modifier,
    title: String,
    sheetState: SheetState = rememberModalBottomSheetState(
        skipPartiallyExpanded = true,
        confirmValueChange = { it != SheetValue.PartiallyExpanded }),
    containerColor: Color = cardBackgroundColor,
    onDismiss: () -> Unit,
    content: @Composable ColumnScope.() -> Unit
) {
    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = if (LocalInspectionMode.current) {
            SheetState(
                skipPartiallyExpanded = true,
                initialValue = SheetValue.Expanded
            )
        } else {
            sheetState
        },
        containerColor = containerColor,
        scrimColor = bottomSheetScrimColor,
        windowInsets = BottomSheetDefaults.windowInsets.only(WindowInsetsSides.Bottom),
        modifier = modifier
    ) {
        BottomSheetTitle(title = title, onDismiss = onDismiss)

        Spacer(modifier = Modifier.height(12.dp))

        content()
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Preview
@Composable
private fun AppBottomSheetPreview() {
    AppBottomSheet(
        title = "AppBottomSheet",
        onDismiss = {},
    ) {
        Column(modifier = Modifier.padding(horizontal = 16.dp)) {
            Text(text = "App bottom sheet 1")
            Text(text = "App bottom sheet 2")
            Text(text = "App bottom sheet 3")
        }
    }
}
