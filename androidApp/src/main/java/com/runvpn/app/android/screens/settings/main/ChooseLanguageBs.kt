package com.runvpn.app.android.screens.settings.main

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.arkivanov.decompose.extensions.compose.jetpack.subscribeAsState
import com.murgupluoglu.flagkit.FlagKit
import com.runvpn.app.android.R
import com.runvpn.app.android.ext.findActivity
import com.runvpn.app.android.widgets.AppBottomSheet
import com.runvpn.app.android.widgets.RoundedImage
import com.runvpn.app.data.settings.domain.Language
import com.runvpn.app.feature.settings.language.ChooseLanguageComponent
import com.runvpn.app.feature.settings.language.FakeChooseLanguageComponent

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ChooseLanguageBs(component: ChooseLanguageComponent, modifier: Modifier = Modifier) {

    val languages by component.languages.subscribeAsState()
    val currentLanguage by component.currentLanguage.subscribeAsState()

    val context = LocalContext.current

    AppBottomSheet(
        onDismiss = component::onDismissClicked,
        title = stringResource(R.string.choose_language),
        modifier = modifier
    ) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 32.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {


            Spacer(modifier = Modifier.height(32.dp))

            repeat(languages.size) {
                LanguageItem(
                    language = languages[it],
                    isSelected = languages[it] == currentLanguage,
                    onClick = {
                        component.onLanguageChosen(languages[it])
                        context.findActivity()?.recreate()
                    }
                )
            }

        }
    }
}

@Composable
fun LanguageItem(
    language: Language,
    isSelected: Boolean,
    modifier: Modifier = Modifier,
    onClick: () -> Unit
) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = modifier
            .clickable { onClick() }
            .padding(start = 16.dp, end = 16.dp, top = 12.dp, bottom = 14.dp)
    ) {
        RoundedImage(
            painter = painterResource(
                id = FlagKit.getResId(
                    LocalContext.current,
                    language.flagIsoCode
                )
            ),
            contentDescription = "",
            modifier = Modifier
                .width(56.dp)
                .height(38.dp)
        )

        Spacer(modifier = Modifier.width(12.dp))

        Text(
            text = "${language.languageInEnglish} - ${language.language}",
            fontSize = 16.sp,
            fontWeight = FontWeight(400),
            color = Color.Black,
            modifier = Modifier.weight(1f)
        )

        if (isSelected) {
            Spacer(modifier = Modifier.width(12.dp))
            Image(painter = painterResource(id = R.drawable.ic_check), contentDescription = "")
        }
    }
}

@Preview
@Composable
private fun ChooseLanguageBsPreview() {
    ChooseLanguageBs(component = FakeChooseLanguageComponent())
}
