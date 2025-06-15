package com.pmalaquias.weatherforecast.presentation.ui.pages.weatherScreen.components


import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3ExpressiveApi
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pmalaquias.weatherforecast.R

@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun PrecipitationCard(
    rainfall: Double,
    textColor: Color = Color.Black
) {
    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.4f),
        )
    ) {
        Column(
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .width(150.dp)
                .height(100.dp)
                .fillMaxWidth()
                .padding(8.dp),
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text("☔", fontSize = 20.sp)
                Spacer(Modifier.width(4.dp))
                Text(
                    text = stringResource(R.string.precipitation_title),
                    style = MaterialTheme.typography.titleSmallEmphasized,
                    color = textColor,
                )
            }
            Spacer(modifier = Modifier.height(16.dp))
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center

                // Display rainfall value
            ) {
                Text(
                    text = " $rainfall  mm",
                    style = MaterialTheme.typography.titleMedium,
                    color = textColor,
                )
            }
        }
    }
}