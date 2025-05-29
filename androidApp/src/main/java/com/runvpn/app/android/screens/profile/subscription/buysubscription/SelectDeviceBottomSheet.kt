package com.runvpn.app.android.screens.profile.subscription.buysubscription


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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.runvpn.app.android.R
import com.runvpn.app.android.widgets.AppBottomSheet
import com.runvpn.app.android.widgets.AppButton
import com.runvpn.app.core.ui.dividerColor
import com.runvpn.app.data.device.domain.models.device.Device

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SelectDeviceBottomSheet(
    modifier: Modifier = Modifier,
    title: String,
    subtitle: String,
    items: List<Device>,
    onItemClick: (Device) -> Unit,
    onDismiss: () -> Unit,
    itemElement: @Composable (Device) -> Unit
) {
    AppBottomSheet(
        title = title,
        onDismiss = onDismiss,
        modifier = modifier
    ) {
        Text(
            text = subtitle,
            fontSize = 16.sp,
            textAlign = TextAlign.Center,
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        Box {
            LazyColumn {
                itemsIndexed(items) { index, item ->
                    Box(modifier = Modifier
                        .fillMaxWidth()
                        .clickable { onItemClick(item) }
                        .padding(vertical = 4.dp, horizontal = 16.dp)) {
                        itemElement(item)
                    }
                    if (index < items.size - 1) {
                        HorizontalDivider(
                            modifier = Modifier.padding(horizontal = 16.dp), color = dividerColor
                        )
                    }
                }

                item {
                    Spacer(modifier = Modifier.height(84.dp))
                }
            }

            Box(
                modifier = Modifier
                    .padding(vertical = 20.dp)
                    .align(Alignment.BottomCenter)
            ) {
                AppButton(
                    onClick = onDismiss, modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp)
                ) {
                    Text(text = stringResource(R.string.done))
                }
            }
        }
    }
}
