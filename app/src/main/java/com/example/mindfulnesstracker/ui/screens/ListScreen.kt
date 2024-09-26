package com.example.mindfulnesstracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.mindfulnesstracker.Habit
import com.example.mindfulnesstracker.ProgressItem
import com.example.mindfulnesstracker.R
import com.example.mindfulnesstracker.ui.components.HabitScaffold
import com.example.mindfulnesstracker.ui.theme.ProgressDone
import com.example.mindfulnesstracker.ui.theme.ProgressFail
import com.example.mindfulnesstracker.ui.theme.ProgressFalse
import com.example.mindfulnesstracker.ui.viewmodels.HabitsUiState
import com.example.mindfulnesstracker.ui.viewmodels.HabitsViewModel
import com.google.firebase.Timestamp
import java.time.Instant
import java.time.LocalDate
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.util.concurrent.TimeUnit

@Composable
fun ListScreen(
    onHabitLabelClick: (habitId: String) -> Unit,
    onAddClick: () -> Unit,
    userId: String,
) {
    val habitsViewModel: HabitsViewModel = viewModel()

    HabitScaffold(
        screenTitle = stringResource(R.string.screen_title_list),
        showBackButton = false,
        actions = {
            IconButton(onClick = {
                onAddClick()
            }) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.button_add),
                )
            }
        },
    ) { paddingValues ->
        Column(Modifier.padding(paddingValues)) {
            WeekDays()

            Spacer(Modifier.height(8.dp))

            HabitsPlaceHolder(
                userId = userId,
                habitsViewModel.habitsUiState,
                PaddingValues(4.dp),
                onHabitLabelClick,
                onDayClick = { habitId, currentStatus, dayEpoch ->
                    habitsViewModel.updateDayStatus(userId, habitId, currentStatus, dayEpoch)
                },
            )
        }
    }
}

@Composable
fun WeekDays() {
    val today = LocalDate.now()
    val lastSevenDays = (0..6).map { today.minusDays(it.toLong()) }
    val formatter = DateTimeFormatter.ofPattern("EEE")

    Row(
        modifier =
            Modifier
                .padding(horizontal = 8.dp)
                .height(40.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        lastSevenDays.reversed().forEach { day ->
            Column(
                modifier =
                    Modifier
                        .weight(1f)
                        .padding(horizontal = 4.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = day.format(formatter),
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                )
            }
        }
    }
}

@Composable
fun HabitsPlaceHolder(
    userId: String,
    habitsUiState: HabitsUiState,
    paddingValues: PaddingValues,
    onHabitLabelClick: (habitId: String) -> Unit,
    onDayClick: (habitId: String, currentStatus: Boolean, dayEpoch: Long) -> Unit,
) {
    when (habitsUiState) {
        is HabitsUiState.Loading -> Text(stringResource(R.string.loading))
        is HabitsUiState.Success ->
            HabitList(
                userId = userId,
                habits = habitsUiState.habits,
                paddingValues = paddingValues,
                onHabitLabelClick = onHabitLabelClick,
                onDayClick = onDayClick,
            )

        is HabitsUiState.Error -> Text(stringResource(R.string.error))
    }
}

@Composable
private fun HabitList(
    userId: String,
    habits: List<Habit>,
    paddingValues: PaddingValues,
    onHabitLabelClick: (habitId: String) -> Unit,
    onDayClick: (habitId: String, currentStatus: Boolean, dayEpoch: Long) -> Unit,
) {
    LazyColumn(
        contentPadding = paddingValues,
    ) {
        items(habits.size) { index ->
            val habit = habits[index]
            HabitItem(
                habit = habit,
                onHabitLabelClick = {
                    onHabitLabelClick(habit.habitId)
                },
                onDayClick = { habitId, currentStatus, dayEpoch -> onDayClick(habitId, currentStatus, dayEpoch) },
            )
        }
    }
}

@Composable
fun HabitItem(
    habit: Habit,
    onHabitLabelClick: (habitId: String) -> Unit,
    onDayClick: (habitId: String, currentStatus: Boolean, dayEpoch: Long) -> Unit,
) {
    Column(
        modifier =
            Modifier
                .padding(8.dp),
    ) {
        Text(
            modifier =
                Modifier
                    .clickable {
                        onHabitLabelClick(habit.habitId)
                    },
            text = habit.name,
            fontWeight = FontWeight.Bold,
        )
        Row(
            modifier =
                Modifier
                    .height(80.dp),
        ) {
            val today = LocalDate.now()
            val lastSevenDays = (0..6).map { today.minusDays(it.toLong()) }

            lastSevenDays.reversed().forEachIndexed { index, day ->
                val progressItem =
                    habit.progress.find {
                        val progressDate =
                            Instant.ofEpochSecond(it.dayEpoch * 24 * 60 * 60).atZone(ZoneId.systemDefault()).toLocalDate()
                        progressDate == day
                    }
                val color =
                    calculateProgressColor(
                        habitCreationDate = habit.creationTimestamp,
                        progressItem = progressItem,
                        epochDay = day.toEpochDay(),
                    )

                Column(
                    modifier =
                        Modifier
                            .weight(1f)
                            .clickable {
                                onDayClick(habit.habitId, progressItem?.indicator ?: false, day.toEpochDay())
                            },
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Box(
                        modifier =
                            Modifier
                                .aspectRatio(1f)
                                .padding(8.dp)
                                .clip(CircleShape)
                                .background(color),
                    )
                    if (index == 6) {
                        Text(
                            text = stringResource(R.string.today),
                            fontSize = 12.sp,
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun calculateProgressColor(
    habitCreationDate: Timestamp,
    progressItem: ProgressItem?,
    epochDay: Long,
): Color {
    val habitCreationEpochDay = TimeUnit.MILLISECONDS.toDays(habitCreationDate.toDate().toInstant().toEpochMilli())

    return if (progressItem != null) {
        if (progressItem.indicator) ProgressDone else ProgressFail
    } else {
        if (epochDay >= habitCreationEpochDay && epochDay <= LocalDate.now().toEpochDay()) {
            ProgressFail
        } else {
            ProgressFalse
        }
    }
}
