package com.runvpn.app.android.widgets


import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.runvpn.app.android.R
import com.runvpn.app.android.RunVpnTheme
import com.runvpn.app.android.screens.profile.subscription.DeviceSelectItem
import com.runvpn.app.core.ui.dividerColor
import com.runvpn.app.data.device.TestDataDevices

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun <T> ItemListBottomSheet(
    modifier: Modifier = Modifier,
    title: String,
    items: List<T>,
    onItemClick: (T) -> Unit,
    onDismiss: () -> Unit,
    dismissOnSelect: Boolean = true,
    spacerBottomHeight: Dp = 0.dp,
    emptyView: @Composable () -> Unit = { DefaultEmptyView() },
    itemElement: @Composable (T) -> Unit
) {

    AppBottomSheet(
        title = title,
        onDismiss = onDismiss,
        modifier = modifier
    ) {
        if (items.isNotEmpty()) {
            LazyColumn {
                itemsIndexed(items) { index, item ->
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable {
                                onItemClick(item)
                                if (dismissOnSelect) onDismiss()
                            }
                    ) {
                        itemElement(item)
                    }
                    if (index < items.size - 1) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp),
                            color = dividerColor
                        )
                    }
                }
            }
        } else {
            emptyView()
        }

        Spacer(modifier = Modifier.height(spacerBottomHeight))

    }
}

@Composable
private fun DefaultEmptyView() {
    Text(
        text = stringResource(R.string.list_is_empty),
        fontSize = 16.sp,
        textAlign = TextAlign.Center,
        fontWeight = FontWeight.Medium,
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 24.dp)
    )
}


@Preview
@Composable
private fun ItemListBottomSheetPreview() {
    RunVpnTheme {
        ItemListBottomSheet(
            items = TestDataDevices.testDevicesList,
            title = "Test Devices ",
            onItemClick = {},
            onDismiss = {},
        ) {
            DeviceSelectItem(device = it)
        }
    }
}
