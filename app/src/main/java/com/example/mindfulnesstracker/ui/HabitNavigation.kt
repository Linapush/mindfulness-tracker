package com.example.mindfulnesstracker.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mindfulnesstracker.ui.screens.AddScreen
import com.example.mindfulnesstracker.ui.screens.ListScreen
import com.example.mindfulnesstracker.ui.screens.ProgressScreen
import com.example.mindfulnesstracker.ui.screens.SplashScreen

private const val SPLASH_SCREEN = "splash"
private const val LIST_SCREEN = "list"
private const val PROGRESS_SCREEN = "progress"
private const val ADD_SCREEN = "add"
private const val ARG_HABIT_ID = "habitId"

// NavController - компонент, управляющий навигацией между различными destination
// NavHost - это контейнер, который содержит набор различных экранов (composables), которые могут быть отображены в зависимости от текущего состояния навигации
// Используется для определения навигационного графа и управления переходами между экранами

@Composable
fun HabitNavigation() {
    // сохранение состояния навигационного контроллера между перерисовками Composable-функции
    val navigator = rememberNavController()
    NavHost(
        navController = navigator,
        startDestination = SPLASH_SCREEN
    ) {
        composable(SPLASH_SCREEN) {
            SplashScreen {
                navigator.navigate(LIST_SCREEN) {
                    popUpTo(0)
                }
            }
        }
        composable(LIST_SCREEN) {
            ListScreen(
                onItemClick = { navigator.navigate("$PROGRESS_SCREEN/$it")},
                onAddClick = { navigator.navigate(ADD_SCREEN)}
            )
        }
        composable(
            "$PROGRESS_SCREEN/{$ARG_HABIT_ID}",
            listOf(navArgument(ARG_HABIT_ID) { type = NavType.IntType })
        ) { backStackEntry ->
            val id = backStackEntry.arguments?.getInt(ARG_HABIT_ID)

            id?.let {
                ProgressScreen(id)
            } ?: Text("Error: Unable to get ID")
        }
        composable(ADD_SCREEN) {
            AddScreen {
                navigator.popBackStack()
            }
        }
    }
}
