package com.pmalaquias.weatherforecast.presentation.ui.pages.weatherScreen.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
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
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.pmalaquias.weatherforecast.R
import com.pmalaquias.weatherforecast.domain.models.WindInfo
import com.pmalaquias.weatherforecast.presentation.ui.pages.weatherScreen.utils.getCompassLabel


@OptIn(ExperimentalMaterial3ExpressiveApi::class)
@Composable
fun WindDataCard(
    windInfo: WindInfo,
    textColor: Color = Color.Black
) {
    val windDirection: Int = getCompassLabel(windInfo.direction)

    Card(
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.4f),
        )
    ) {
        Column(
            modifier = Modifier
                .width(150.dp)
                .height(100.dp)
                .fillMaxWidth()
                .padding(8.dp),
            horizontalAlignment = Alignment.Start,
            verticalArrangement = Arrangement.Center
        ) {

            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.Center
            ) {
                Text("🍃", fontSize = 20.sp)
                Spacer(Modifier.width(4.dp))
                Text(
                    stringResource(R.string.wind_title),
                    style = MaterialTheme.typography.titleSmallEmphasized,
                    color = textColor
                )
            }

            Box(
                modifier = Modifier
                    .fillMaxSize()
                //.padding(bottom = 8.dp)
                ,
                contentAlignment = Alignment.Center
            ) {
                WindDirectionDisplay(
                    windDirection = windInfo.direction,
                    rotationDegrees = windDirectionToDegrees(windInfo.direction),
                )
                Column(
                    modifier = Modifier,
                    verticalArrangement = Arrangement.SpaceEvenly,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    // Display wind speed and direction
                    Text(
                        text = "${windInfo.speed} km/h",
                        style = MaterialTheme.typography.bodyLargeEmphasized,
                        color = textColor
                    )
                    Text(
                        text = stringResource(windDirection),
                        style = MaterialTheme.typography.bodySmallEmphasized,
                        color = textColor
                    )
                }
            }
        }
    }
}


@Preview(showBackground = true)
@Composable
fun WindDataDisplayPreview() {
    // Example preview data
    WindDataCard(
        windInfo = WindInfo(speed = 15.0, direction = "E")
    )
}

