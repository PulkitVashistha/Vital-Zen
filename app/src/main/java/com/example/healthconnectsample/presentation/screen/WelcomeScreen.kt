/*
 * Copyright 2022 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.healthconnectsample.presentation.screen

import android.content.Context
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
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
import androidx.compose.foundation.layout.wrapContentWidth
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Button
import androidx.compose.material.ButtonDefaults
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shadow
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.em
import androidx.compose.ui.unit.sp
import androidx.health.connect.client.HealthConnectClient.Companion.SDK_AVAILABLE
import androidx.health.connect.client.HealthConnectClient.Companion.SDK_UNAVAILABLE
import androidx.health.connect.client.HealthConnectClient.Companion.SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.LifecycleOwner
import com.example.healthconnectsample.R
import com.example.healthconnectsample.data.HealthConnectManager
import com.example.healthconnectsample.presentation.theme.HealthConnectTheme
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.delay
import org.json.JSONObject
import java.net.HttpURLConnection
import java.net.URL

/**
 * Welcome screen shown when the app is first launched.
 */
@Composable
fun WelcomeScreen(
    healthConnectAvailability: Int,
    healthConnectManager: HealthConnectManager,
    onResumeAvailabilityCheck: () -> Unit,
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
) {
    val currentOnAvailabilityCheck by rememberUpdatedState(onResumeAvailabilityCheck)
    var isLoading by remember { mutableStateOf(false) }
    var header by remember { mutableStateOf("Default Header") }
    var description by remember { mutableStateOf("Default Description") }


    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                currentOnAvailabilityCheck()
            }
        }

        // Add the observer to the lifecycle
        lifecycleOwner.lifecycle.addObserver(observer)

        // When the effect leaves the Composition, remove the observer
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(32.dp),
        verticalArrangement = Arrangement.Top,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "VitalZen",
            style = TextStyle(
                fontFamily = FontFamily.Monospace,
                fontWeight = FontWeight.Bold,
                fontSize = 28.sp,
                letterSpacing = 0.05.em,
                shadow = Shadow(
                    color = Color.Black,
                    offset = Offset(2f, 2f),
                    blurRadius = 4f
                )
            ),
            textAlign = TextAlign.Center
        )
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(10.dp)
        )
        LazyVerticalGrid(
            columns = GridCells.Fixed(2),
            content = {
                val imageButtons = listOf(
                    R.drawable.yoga6, R.drawable.yoga3, R.drawable.yoga5,
                    R.drawable.yoga4, R.drawable.yoga2, R.drawable.yoga1
                )

                items(imageButtons.size) { index ->
                    Image(
                        painter = painterResource(id = imageButtons[index]),
                        contentDescription = null,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .width(160.dp)
                            .height(90.dp)
                            .padding(8.dp)
                    )
                }
            }
        )
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(2.dp)
                .background(Color.Gray)
                .padding(vertical = 8.dp)
        )

        Text(
            text = "Get personalized meditation recommendations!",
            style = TextStyle.Default.copy(fontSize = 14.sp),
            color = Color.Gray,
            textAlign = TextAlign.Center,
            modifier = Modifier
                .wrapContentWidth()
                .padding(top = 16.dp, bottom = 16.dp)
                .fillMaxWidth()
                .align(Alignment.CenterHorizontally)
        )
        Spacer(
            modifier = Modifier
                .fillMaxWidth()
                .height(20.dp)
        )
        Button(
            onClick = {
                isLoading = true
            },
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            colors = ButtonDefaults.buttonColors(
                backgroundColor = Color.Gray,
                contentColor = Color.Black
            ),
            shape = RoundedCornerShape(8.dp)
        ) {
            Text(text = "Meditate with AI")
        }

        // Show loader if it's requested
        if (isLoading) {
            LaunchedEffect(Unit) {
                //send data to AI model
//                val sleepData = healthConnectManager.readSleepSessions()
//                val readTotalSteps = healthConnectManager.readTotalSteps()
//                val readHeartRate = healthConnectManager.readHeartRate()
//                val gson = Gson()
//                val mergedJson = JsonObject()
//                mergedJson.add("stepsData",gson.toJsonTree(readTotalSteps))
//                mergedJson.add("sleepData",gson.toJsonTree(sleepData))
//                mergedJson.add("heartrateData",gson.toJsonTree(readHeartRate))
//                healthConnectManager.sendData(gson.toJson(mergedJson))
                delay(5000)
                //receive data from AI model
//                val url = "YOUR_API_ENDPOINT"
//                val connection = URL(url).openConnection() as HttpURLConnection
//                connection.requestMethod = "GET"
//
//                val response = connection.inputStream.bufferedReader().use { it.readText() }
//
//                val jsonObject = JSONObject(response)
//                header = jsonObject.getString("header")
//                description = jsonObject.getString("description")
                isLoading = false
            }
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color.Black.copy(alpha = 0.5f)),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = Color.White)
            }
        }

        if(!isLoading){
            PersonalizedMeditation()
        }

    }

    fun sendDataToAPI(){
    }
}

@Preview
@Composable
fun PersonalizedMeditation(){
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .width(64.dp)
                    .height(64.dp)
            ) {
                Image(
                    painter = painterResource(id = R.drawable.meditate),
                    contentDescription = null,
                    contentScale = ContentScale.Fit,
                    modifier = Modifier
                        .width(160.dp)
                        .height(90.dp)
                        .padding(8.dp)
                )
            }

            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(start = 16.dp),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = "Mindfulness Meditation",
                    style = TextStyle(fontWeight = FontWeight.Bold),
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.Start)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = "In mindfulness meditation, you focus on the present moment without judgment.",
                    modifier = Modifier
                        .fillMaxWidth()
                        .wrapContentWidth(Alignment.Start)
                )
            }
        }
    }
}


//@Preview
//@Composable
//fun InstalledMessagePreview() {
//    HealthConnectTheme {
//        WelcomeScreen(
//            healthConnectAvailability = SDK_AVAILABLE,
//            healthConnectManager = HealthConnectManager(this),
//            onResumeAvailabilityCheck = {}
//        )
//    }
//}
//
//@Preview
//@Composable
//fun NotInstalledMessagePreview() {
//    HealthConnectTheme {
//        WelcomeScreen(
//            healthConnectAvailability = SDK_UNAVAILABLE_PROVIDER_UPDATE_REQUIRED,
//            healthConnectManager = HealthConnectManager(this),
//            onResumeAvailabilityCheck = {}
//        )
//    }
//}
//
//@Preview
//@Composable
//fun NotSupportedMessagePreview() {
//    HealthConnectTheme {
//        WelcomeScreen(
//            healthConnectAvailability = SDK_UNAVAILABLE,
//            healthConnectManager = HealthConnectManager(this),
//            onResumeAvailabilityCheck = {}
//        )
//    }
//}
