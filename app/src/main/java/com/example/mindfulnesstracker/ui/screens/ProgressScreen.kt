package com.example.mindfulnesstracker.ui.screens

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mindfulnesstracker.R
import com.example.mindfulnesstracker.ui.components.DaysOfWeek
import com.example.mindfulnesstracker.ui.components.HabitScaffold
import com.example.mindfulnesstracker.ui.theme.ProgressDone
import com.example.mindfulnesstracker.ui.theme.ProgressFail
import com.example.mindfulnesstracker.ui.theme.ProgressFalse
import com.example.mindfulnesstracker.ui.viewmodels.ProgressViewModel

@SuppressLint("SimpleDateFormat")
@Composable
fun ProgressScreen(habitId: Int) {
    HabitScaffold(
        screenTitle = stringResource(R.string.screen_title_progress),
    ) { paddingValues ->
        Column(
            Modifier
                .padding(paddingValues)
                .padding(horizontal = 16.dp),
        ) {
            val viewModel = viewModel(modelClass = ProgressViewModel::class)

            Text(
                text = "TODO",
                modifier = Modifier.padding(vertical = 16.dp),
            )

            Row(
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth(),
            ) {
                IconButton(onClick = { viewModel.previousMonth() }) {
                    Icon(Icons.Default.ArrowBack, stringResource(R.string.month_previous))
                }

                Text(
                    text = viewModel.monthName.value,
                    modifier =
                        Modifier
                            .clickable {
                                viewModel.currentMonth()
                            }
                            .padding(8.dp),
                )

                IconButton(onClick = { viewModel.nextMonth() }) {
                    Icon(Icons.Default.ArrowForward, stringResource(R.string.month_next))
                }
            }

            DaysOfWeek()

            val days = viewModel.days.toList()
            for (w in days.indices step 7) {
                Row {
                    for (d in 0..6) {
                        Box(
                            modifier = Modifier.weight(1f),
                            contentAlignment = Alignment.Center,
                        ) {
                            val day = days[w + d]
                            val color =
                                day.second?.let {
                                    if (it) ProgressDone else ProgressFail
                                } ?: ProgressFalse

                            Box(
                                modifier =
                                    Modifier
                                        .aspectRatio(1f)
                                        .padding(8.dp)
                                        .clip(CircleShape)
                                        .background(color),
                            )
                            Text(
                                text = day.first.toString(),
                                fontSize = 12.sp,
                                color = Color.White,
                            )
                        }
                    }
                }
            }
        }
    }
}
