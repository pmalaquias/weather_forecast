package com.pmalaquias.weatherforecast.presentation.ui.pages.weatherScreen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.LinearWavyProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun HumidityDataDisplay(
    humidityData: Int
) {

    Card(){
        Column(
            modifier = Modifier
                .height(100.dp)
                .width(150.dp)
                .padding(8.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text("💧", fontSize = 20.sp)
                Spacer(Modifier.width(4.dp))
                Text("Umidade", style = MaterialTheme.typography.titleSmallEmphasized)
            }

            Spacer(modifier = Modifier.height(16.dp))

            Column(
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    "$humidityData %",
                    style = MaterialTheme.typography.bodyLarge,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(horizontal = 8.dp)
                )
                LinearWavyProgressIndicator(
                    progress = { humidityData / 100f },
                    modifier = Modifier
                        .padding(top = 8.dp)
                        .height(8.dp),
                )
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
fun HumidityDataDisplayPreview() {
    HumidityDataDisplay(humidityData = 75)
}