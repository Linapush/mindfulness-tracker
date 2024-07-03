package com.example.mindfulnesstracker.ui.components

import android.app.Activity
import androidx.activity.compose.LocalOnBackPressedDispatcherOwner
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.RowScope
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalView
import androidx.compose.ui.res.stringResource
import androidx.core.view.WindowCompat
import com.example.mindfulnesstracker.R

@Composable
fun HabitScaffold(
    screenTitle: String? = null,
    showBackButton: Boolean = true,
    actions: @Composable RowScope.() -> Unit = {},
    content: @Composable (PaddingValues) -> Unit
) {
    val isDarkTheme = isSystemInDarkTheme()
    // локальные значения для использования внутри дочерних элементов
    CompositionLocalProvider(LocalContentColor provides if (isDarkTheme) Color.White else Color.Black) {
        val view = LocalView.current
        if (!view.isInEditMode) {
            SideEffect {
                // извлекается window приложения из контекста представления
                val window = (view.context as Activity).window
                // контроллер для управления внешними отступами окна
                val controller = WindowCompat.getInsetsController(window, view)
                controller.isAppearanceLightStatusBars = !isDarkTheme
            }
        }
        Scaffold(
            topBar = {
                HabitAppBar(screenTitle, showBackButton, actions, isDarkTheme)
            },
            content = content
        )
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HabitAppBar(
    screenTitle: String?,
    showBackButton: Boolean,
    actions: @Composable RowScope.() -> Unit,
    isDarkTheme: Boolean
) {
    val backgroundColor = if (isDarkTheme) Color.Black else Color.White
    val contentColor = if (isDarkTheme) Color.White else Color.Black

    screenTitle?.let {
        Surface(color = backgroundColor) {
            TopAppBar(
                title = { Text(screenTitle, color = contentColor) },
                navigationIcon = {
                    if (showBackButton) {
                        val dispatcherOwner = LocalOnBackPressedDispatcherOwner.current
                        val dispatcher = dispatcherOwner?.onBackPressedDispatcher
                        dispatcher?.let {
                            IconButton(onClick = { it.onBackPressed() }) {
                                Icon(
                                    imageVector = Icons.Filled.ArrowBack,
                                    contentDescription = stringResource(R.string.button_add),
                                    tint = contentColor
                                )
                            }
                        }
                    }
                },
                actions = actions
            )
        }
    }
}
