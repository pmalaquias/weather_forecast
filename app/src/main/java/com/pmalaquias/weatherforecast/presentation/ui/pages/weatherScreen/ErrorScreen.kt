package com.pmalaquias.weatherforecast.presentation.ui.pages.weatherScreen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.pmalaquias.weatherforecast.R
import com.pmalaquias.weatherforecast.presentation.ui.theme.AppTheme

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun ErrorScreen(errorMessage: Int, onRetry: () -> Unit) {
    val size = ButtonDefaults.MediumContainerHeight

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceAround,
        modifier = Modifier.fillMaxSize(),

        ) {
        Text(
            text = stringResource(R.string.standard_error_message, stringResource(errorMessage)),
            color = MaterialTheme.colorScheme.error,
            style = MaterialTheme.typography.bodyLargeEmphasized,
            textAlign = TextAlign.Center,
            modifier = Modifier.padding(16.dp)
        )
        // Botão para tentar novamente
        Button(
            onClick = { onRetry },
            shapes = ButtonDefaults.shapes(),
            modifier = Modifier.padding(top = 8.dp),
            contentPadding = ButtonDefaults.contentPaddingFor(size),
            content = {Text(stringResource(R.string.try_again_button)) },
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ErrorScreenPreview() {
    AppTheme {
        ErrorScreen(
            errorMessage = R.string.error_message_without_conection,
            onRetry = {}
        )
    }
}