package com.example.mindfulnesstracker.ui.screens

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringArrayResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mindfulnesstracker.R
import com.example.mindfulnesstracker.ui.components.HabitScaffold
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import kotlin.time.Duration.Companion.seconds

@Composable
fun SplashScreen(nextScreen: () -> Unit) {
    HabitScaffold(
        showBackButton = false,
    ) { paddingValues ->
        val scope = rememberCoroutineScope()

        LaunchedEffect(Unit) {
            scope.launch {
                delay(1.seconds)
                withContext(Dispatchers.Main) {
                    nextScreen()
                }
            }
        }

        Box {
            Image(
                painter = painterResource(R.drawable.splash_background),
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(paddingValues)
            ) {
                Text(
                    text = stringResource(R.string.app_name),
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(top = 144.dp)
                        .padding(horizontal = 32.dp),
                    color = Color.White,
                    fontSize = 32.sp,
                    textAlign = TextAlign.Center
                )
                val strings = stringArrayResource(R.array.motd_array)
                val text = remember { strings.random() }
                Text(
                    text = text,
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .padding(bottom = 96.dp)
                        .padding(horizontal = 32.dp),
                    color = Color.White,
                    fontSize = 16.sp
                )
            }
        }
    }
}
