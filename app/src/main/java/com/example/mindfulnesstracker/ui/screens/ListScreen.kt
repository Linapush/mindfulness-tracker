package com.example.mindfulnesstracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
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
import com.example.mindfulnesstracker.R
import com.example.mindfulnesstracker.ui.components.HabitScaffold
import com.example.mindfulnesstracker.ui.theme.ProgressDone
import com.example.mindfulnesstracker.ui.theme.ProgressFail
import com.example.mindfulnesstracker.ui.theme.ProgressFalse
import com.example.mindfulnesstracker.ui.viewmodels.HabitsUiState
import com.example.mindfulnesstracker.ui.viewmodels.HabitsViewModel

@Composable
fun ListScreen(
    userId: String,
    onHabitLabelClick: (habitId: String) -> Unit,
    onAddClick: () -> Unit,
) {
    HabitScaffold(
        screenTitle = stringResource(R.string.screen_title_list),
        showBackButton = false,
        actions = {
            IconButton(onClick = onAddClick) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.button_add),
                )
            }
        },
    ) { paddingValues ->
        val habitsViewModel: HabitsViewModel = viewModel()
        HabitsPlaceHolder(userId = userId, habitsViewModel.habitsUiState, paddingValues, onHabitLabelClick)
    }
}

@Composable
fun HabitsPlaceHolder(
    userId: String,
    habitsUiState: HabitsUiState,
    paddingValues: PaddingValues,
    onHabitLabelClick: (habitId: String) -> Unit,
) {
    when (habitsUiState) {
        is HabitsUiState.Loading -> Text(stringResource(R.string.loading))
        is HabitsUiState.Success ->
            HabitList(
                userId = userId,
                habits = habitsUiState.habits,
                paddingValues = paddingValues,
                onHabitLabelClick = onHabitLabelClick,
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
    onCurrentDayClick: (habitId: String) -> Unit = {},
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
                onCurrentDayClick = { onCurrentDayClick(habit.habitId) },
            )
        }
    }
}

@Composable
fun HabitItem(
    habit: Habit,
    onHabitLabelClick: (habitId: String) -> Unit,
    onCurrentDayClick: (habitId: String) -> Unit,
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
                    .height(60.dp),
        ) {
            habit.progress.forEachIndexed { index, progressItem ->
                val color =
                    if (progressItem.indicator) {
                        ProgressDone
                    } else {
                        if (index == 6) ProgressFalse else ProgressFail
                    }
                Column(
                    modifier =
                        Modifier
                            .weight(1f)
                            .clickable {
                                if (index == 6) {
                                    onCurrentDayClick(habit.habitId)
                                }
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
