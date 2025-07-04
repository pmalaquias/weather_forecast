package com.pmalaquias.weatherforecast.presentation.ui.pages.aboutPage

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MediumFlexibleTopAppBar
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.tooling.preview.Preview
import com.pmalaquias.weatherforecast.presentation.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class, ExperimentalMaterial3Api::class)
@Composable
fun AboutScreen(
    modifier: Modifier = Modifier,
    onBackClick: () -> Unit = {  }
) {

    val uri = "https://www.weatherapi.com/"

    val uriHandler = LocalUriHandler.current
    val annotatedLinkString: AnnotatedString = buildAnnotatedString {
        append("Powered by ")

        pushStringAnnotation(
            tag = "URL", annotation = "https://www.weatherapi.com/"
        )
        withStyle(
            style = SpanStyle(
                color = Color(0xFF1E88E5), textDecoration = TextDecoration.Underline
            )
        ) {
            append("WeatherAPI.com")
        }
        pop()
    }

    val imgLogoWeatherAPI = "//cdn.weatherapi.com/v4/images/weatherapi_logo.png"



    Scaffold(
        topBar = {
            TopAppBar(title = { Text(text = "About") }, navigationIcon = {
                IconButton(onClick = { onBackClick() }) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",

                        )
                }
            })
        }) {
        Column(
            modifier = Modifier.padding(it),

            ) {
            Row(
                modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.Center
            ) {
                Text(
                    text = annotatedLinkString,
                    modifier = Modifier.clickable(onClick = { uriHandler.openUri(uri) }),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun AboutScreenPreview() {
    AppTheme {
        AboutScreen()
    }
}