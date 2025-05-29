package com.runvpn.app.android.widgets

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.selection.selectableGroup
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.runvpn.app.android.rubikFontFamily
import com.runvpn.app.core.ui.backgroundAccentColor
import com.runvpn.app.core.ui.badgeDotColor
import com.runvpn.app.core.ui.bottomNavbarItemInactiveColor
import com.runvpn.app.core.ui.bottomNavbarItemInactiveDisabledColor
import com.runvpn.app.core.ui.bottomNavigationColor
import com.runvpn.app.core.ui.colorIconAccent


@Composable
fun BottomNavigationBar(
    modifier: Modifier = Modifier,
    content: @Composable RowScope.() -> Unit
) {
    Surface(
        color = bottomNavigationColor,
        modifier = modifier
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .height(70.dp)
        ) {
            Divider(thickness = 1.dp, color = backgroundAccentColor)

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .selectableGroup(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceAround,
                content = content
            )
        }
    }
}

@Composable
fun NavBarItem(
    selected: Boolean,
    onClick: () -> Unit,
    icon: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    hasBadge: Boolean = false,
    label: String? = null,
    disabledAction: (() -> Unit)? = null
) {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween,
        modifier = modifier
            .clickable(indication = null, interactionSource = remember {
                MutableInteractionSource()
            }) {
                if (enabled) {
                    onClick()
                } else {
                    disabledAction?.invoke()
                }
            }
    ) {
        val color = if (selected) {
            colorIconAccent
        } else {
            if (enabled) {
                bottomNavbarItemInactiveColor
            } else {
                bottomNavbarItemInactiveDisabledColor
            }
        }

        Box {
            val styledIcon = @Composable {
                val iconColor = if (selected) {
                    colorIconAccent
                } else {
                    if (enabled) {
                        bottomNavbarItemInactiveColor
                    } else {
                        bottomNavbarItemInactiveDisabledColor
                    }
                }

                Box(modifier = Modifier) {
                    CompositionLocalProvider(LocalContentColor provides iconColor, content = icon)
                }
            }

            styledIcon()

            if (hasBadge) {
                Box(
                    modifier = Modifier
                        .offset(x = 4.dp)
                        .size(6.dp)
                        .clip(RoundedCornerShape(50))
                        .background(badgeDotColor)
                        .align(Alignment.TopEnd)
                )
            }
        }
        if (label != null) {
            Text(
                text = label,
                color = color,
                fontFamily = rubikFontFamily,
                fontWeight = FontWeight.Normal,
                fontSize = 12.sp
            )
        }
    }
}

@Preview
@Composable
fun BottomNavigationBarPreview() {
    BottomNavigationBar {
        NavBarItem(
            selected = true,
            onClick = { /*TODO*/ },
            icon = { Icon(Icons.Filled.Home, contentDescription = "") },
            label = "Home"
        )
        NavBarItem(
            selected = false,
            onClick = { /*TODO*/ },
            icon = { Icon(Icons.Filled.List, contentDescription = "") },
            label = "Servers"
        )
        NavBarItem(
            selected = false,
            onClick = { /*TODO*/ },
            icon = { Icon(Icons.Filled.Person, contentDescription = "") },
            label = "Profile"
        )
        NavBarItem(
            selected = false,
            onClick = { /*TODO*/ },
            icon = { Icon(Icons.Filled.Settings, contentDescription = "") },
            label = "Settings",
            hasBadge = true
        )
    }
}

