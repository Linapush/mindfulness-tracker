package com.example.mindfulnesstracker.ui.viewmodels

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mindfulnesstracker.Habit
import com.example.mindfulnesstracker.USER_ID
import com.example.mindfulnesstracker.getHabitsForUser
import kotlinx.coroutines.launch
import java.io.IOException

/**
 * UI state for the Home screen
 */
sealed interface HabitsUiState {
    data class Success(val habits: List<Habit>) : HabitsUiState

    object Error : HabitsUiState

    object Loading : HabitsUiState
}

class HabitsViewModel : ViewModel() {
    var habitsUiState: HabitsUiState by mutableStateOf(HabitsUiState.Loading)
        private set

    init {
        getHabits(USER_ID)
    }

    fun getHabits(userId: String) {
        viewModelScope.launch {
            habitsUiState = HabitsUiState.Loading
            habitsUiState =
                try {
                    val listResult = getHabitsForUser(userId)
                    HabitsUiState.Success(
                        listResult,
                    )
                } catch (e: IOException) {
                    HabitsUiState.Error
                } catch (e: RuntimeException) {
                    HabitsUiState.Error
                }
        }
    }
}
