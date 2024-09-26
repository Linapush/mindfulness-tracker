package com.example.mindfulnesstracker.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mindfulnesstracker.Habit
import com.example.mindfulnesstracker.USER_ID
import com.example.mindfulnesstracker.getHabitsForUser
import com.example.mindfulnesstracker.updateHabitStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * UI state for the Home screen
 */
sealed interface HabitsUiState {
    data class Success(val habits: List<Habit>) : HabitsUiState

    data object Error : HabitsUiState

    data object Loading : HabitsUiState
}

class HabitsViewModel : ViewModel() {
    var habitsUiState: HabitsUiState by mutableStateOf(HabitsUiState.Loading)
        private set

    init {
        viewModelScope.launch {
            getHabits()

            while (true) {
                delay(1000)

                updateHabits()
            }
        }
    }

    fun getHabits() {
        viewModelScope.launch {
            habitsUiState = HabitsUiState.Loading
            habitsUiState =
                try {
                    val listResult = getHabitsForUser(USER_ID)

                    HabitsUiState.Success(
                        listResult,
                    )
                } catch (ex: Exception) {
                    println(ex.message)
                    HabitsUiState.Error
                }
        }
    }

    private fun updateHabits() {
        viewModelScope.launch {
            habitsUiState =
                try {
                    val listResult = getHabitsForUser(USER_ID)

                    HabitsUiState.Success(
                        listResult,
                    )
                } catch (e: Exception) {
                    HabitsUiState.Error
                }
        }
    }

    fun updateDayStatus(
        userId: String,
        habitId: String,
        currentStatus: Boolean,
        dayEpoch: Long,
    ) {
        updateHabitStatus(
            userId = userId,
            habitId = habitId,
            dayEpoch = dayEpoch,
            status = !currentStatus,
            onSuccess = {
                updateHabits()
            },
        )
    }
}
