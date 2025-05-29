package com.runvpn.app.android.screens.profile.subscription

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.pluralStringResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.runvpn.app.android.R
import com.runvpn.app.android.ext.toDateLocalized
import com.runvpn.app.core.ui.hintTextColor
import com.runvpn.app.data.subscription.domain.entities.Subscription

@Composable
fun SubscriptionSelectItem(
    modifier: Modifier = Modifier,
    subscription: Subscription
) {
    Column(
        modifier = modifier
            .padding(horizontal = 16.dp, vertical = 8.dp)
    ) {
        Text(
            text = stringResource(
                id = R.string.subscription_period,
                subscription.periodInDays
            ) + " " + pluralStringResource(
                id = R.plurals.days_format,
                count = subscription.periodInDays
            ),
            fontWeight = FontWeight.Medium,
            fontSize = 16.sp,
            lineHeight = 20.sp
        )

        Spacer(modifier = Modifier.height(2.dp))

        Text(
            text = stringResource(
                id = R.string.subscription_activate_before,
                subscription.expirationAt.toDateLocalized()
            ),
            fontSize = 12.sp,
            color = hintTextColor
        )
    }
}
