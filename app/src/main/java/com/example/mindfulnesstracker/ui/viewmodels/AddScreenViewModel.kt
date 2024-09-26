package com.example.mindfulnesstracker.ui.viewmodels

import androidx.lifecycle.ViewModel
import com.example.mindfulnesstracker.USER_ID
import com.example.mindfulnesstracker.addHabit

class AddScreenViewModel : ViewModel() {
    fun save(
        title: String,
        hour: Int,
        minute: Int,
    ) {
        addHabit(
            userId = USER_ID,
            name = title,
            hour = hour,
            minute = minute,
        )
    }
}
