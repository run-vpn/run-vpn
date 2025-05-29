package com.runvpn.app.android

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Shapes
import androidx.compose.material3.Typography
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.runvpn.app.core.ui.backgroundColor
import com.runvpn.app.core.ui.primaryColor
import com.runvpn.app.data.settings.domain.AppTheme


val rubikFontFamily = FontFamily(
    Font(R.font.rubik_light, FontWeight.Light),
    Font(R.font.rubik_medium, FontWeight.Medium),
    Font(R.font.rubik_bold, FontWeight.Bold),
    Font(R.font.rubik_semibold, FontWeight.SemiBold),
    Font(R.font.rubik_regular, FontWeight.Normal)
)

val monacoFontFamily = FontFamily(
    Font(
        R.font.monaco_regular,
        FontWeight.Normal
    )
)

@Composable
fun RunVpnTheme(
    appTheme: AppTheme = AppTheme.SYSTEM,
    content: @Composable () -> Unit
) {
    val colors = if (appTheme == AppTheme.DARK) {
        darkColorScheme(
            background = backgroundColor,
            primary = Color(0xFFBB86FC),
            secondary = Color(0xFF03DAC5),
            tertiary = Color(0xFF3700B3)
        )
    } else {
        lightColorScheme(
            background = backgroundColor,
            primary = primaryColor,
            secondary = Color(0xFF03DAC5),
            tertiary = Color(0xFF3700B3)
        )
    }

    val defaultTypography = Typography()
    val typography = Typography(

        displayLarge = defaultTypography.displayLarge.copy(fontFamily = rubikFontFamily),
        displayMedium = defaultTypography.displayMedium.copy(fontFamily = rubikFontFamily),
        displaySmall = defaultTypography.displaySmall.copy(fontFamily = rubikFontFamily),

        headlineLarge = defaultTypography.headlineLarge.copy(fontFamily = rubikFontFamily),
        headlineMedium = defaultTypography.headlineMedium.copy(fontFamily = rubikFontFamily),
        headlineSmall = defaultTypography.headlineSmall.copy(fontFamily = rubikFontFamily),

        titleLarge = defaultTypography.titleLarge.copy(fontFamily = rubikFontFamily),
        titleMedium = defaultTypography.titleMedium.copy(fontFamily = rubikFontFamily),
        titleSmall = defaultTypography.titleSmall.copy(fontFamily = rubikFontFamily),

        bodyLarge = defaultTypography.bodyLarge.copy(fontFamily = rubikFontFamily),
        bodyMedium = defaultTypography.bodyMedium.copy(fontFamily = rubikFontFamily),
        bodySmall = defaultTypography.bodySmall.copy(fontFamily = rubikFontFamily),

        labelLarge = defaultTypography.labelLarge.copy(fontFamily = rubikFontFamily),
        labelMedium = defaultTypography.labelMedium.copy(fontFamily = rubikFontFamily),
        labelSmall = defaultTypography.labelSmall.copy(fontFamily = rubikFontFamily)
    )

    val shapes = Shapes(
        small = RoundedCornerShape(4.dp),
        medium = RoundedCornerShape(8.dp),
        large = RoundedCornerShape(16.dp)
    )

    MaterialTheme(
        colorScheme = colors,
        typography = typography,
        shapes = shapes,
        content = content
    )
}

