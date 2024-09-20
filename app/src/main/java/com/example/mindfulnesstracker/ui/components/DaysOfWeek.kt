package com.example.mindfulnesstracker.ui.components

import android.annotation.SuppressLint
import androidx.compose.foundation.layout.Row
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import java.text.SimpleDateFormat
import java.util.Calendar

@SuppressLint("SimpleDateFormat")
@Composable
fun DaysOfWeek(modifier: Modifier = Modifier) {
    Row(modifier) {
        val calendar = Calendar.getInstance()

        calendar.set(Calendar.DAY_OF_WEEK, calendar.firstDayOfWeek)

        for (i in 0..6) {
            Text(
                text = SimpleDateFormat("E").format(calendar.time),
                modifier = Modifier.weight(1f),
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
            )
            calendar.add(Calendar.DAY_OF_WEEK, 1)
        }
    }
}
