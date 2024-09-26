package com.example.mindfulnesstracker.ui.viewmodels

import android.annotation.SuppressLint
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.mindfulnesstracker.*
import com.example.mindfulnesstracker.ui.screens.ProgressState
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.concurrent.TimeUnit

data class CalendarUiDay(
    val dayNumber: Int,
    val dayEpoch: Long,
    val status: Boolean?,
)

class ProgressViewModel : ViewModel() {
    val habitCreationEpochDay = mutableStateOf(Long.MAX_VALUE)
    private val todayCalendar = Calendar.getInstance()
    private val calendar = Calendar.getInstance()

    val monthName = mutableStateOf("")
    val days = mutableStateListOf<CalendarUiDay>()

    fun initialize(habitId: String) {
        updateFields(habitId)
        updateHabitCreationDate(habitId)
    }

    private fun updateHabitCreationDate(habitId: String) {
        viewModelScope.launch {
            val creationEpochDay =
                TimeUnit.MILLISECONDS.toDays(
                    getHabitsForUser(USER_ID)
                        .find { it.habitId == habitId }
                        ?.creationTimestamp
                        ?.toDate()
                        ?.toInstant()
                        ?.toEpochMilli() ?: return@launch,
                )

            habitCreationEpochDay.value = creationEpochDay
        }
    }

    fun previousMonth(habitId: String) {
        calendar.add(Calendar.MONTH, -1)
        updateFields(habitId)
    }

    fun currentMonth(habitId: String) {
        calendar.time = todayCalendar.time
        updateFields(habitId)
    }

    fun nextMonth(habitId: String) {
        calendar.add(Calendar.MONTH, 1)
        updateFields(habitId)
    }

    private fun updateFields(habitId: String) {
        monthName.value = formatMonth()
        days.clear()
        viewModelScope.launch {
            days.addAll(getDays(userId = USER_ID, habitId = habitId))
        }
    }

    @SuppressLint("SimpleDateFormat")
    private fun formatMonth(): String {
        return if (todayCalendar.get(Calendar.YEAR) == calendar.get(Calendar.YEAR)) {
            SimpleDateFormat("LLLL").format(calendar.time)
        } else {
            SimpleDateFormat("LLLL yyyy").format(calendar.time)
        }
    }

    private suspend fun getDays(
        userId: String,
        habitId: String,
    ): List<CalendarUiDay> {
        val progressItems = getProgressForHabit(userId, habitId)

        println("progressItems: $progressItems")

        val progressMap = mutableMapOf<Long, Boolean>()
        for (progressItem in progressItems) {
            val epochDay = progressItem.dayEpoch
            progressMap[epochDay] = progressItem.indicator
        }

        val days = mutableListOf<CalendarUiDay>()
        val monthCalendar = calendar.clone() as Calendar
        monthCalendar.set(Calendar.DAY_OF_MONTH, 1)
        val maxDayOfMonth = monthCalendar.getActualMaximum(Calendar.DAY_OF_MONTH)

        for (day in 1..maxDayOfMonth) {
            monthCalendar.set(Calendar.DAY_OF_MONTH, day)
            val epochDay = monthCalendar.time.toInstant().epochSecond / (24 * 60 * 60)
            val status = progressMap[epochDay]
            days.add(CalendarUiDay(dayNumber = day, dayEpoch = epochDay, status = status))
        }

        return days
    }

    fun updateProgress(
        habitId: String,
        dayEpoch: Long,
        progressState: ProgressState,
    ) {
        println("habitId $habitId; day epoch: $dayEpoch")

        updateHabitStatus(
            userId = USER_ID,
            habitId = habitId,
            dayEpoch = dayEpoch,
            status = progressState != ProgressState.Done,
            onSuccess = {
                updateFields(habitId)
            },
        )
    }
}
