package com.example.mindfulnesstracker.ui.screens

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.mindfulnesstracker.R
import com.example.mindfulnesstracker.ui.components.HabitScaffold

@Composable
fun ProgressScreen(habitId: Int) {
    HabitScaffold(
        screenTitle = stringResource(R.string.screen_title_progress)
    ) { paddingValues ->

    }
}
