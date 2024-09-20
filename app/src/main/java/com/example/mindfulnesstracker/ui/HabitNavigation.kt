package com.example.mindfulnesstracker.ui

import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.mindfulnesstracker.USER_ID
import com.example.mindfulnesstracker.ui.screens.AddScreen
import com.example.mindfulnesstracker.ui.screens.ListScreen
import com.example.mindfulnesstracker.ui.screens.ProgressScreen
import com.example.mindfulnesstracker.ui.screens.SplashScreen
import java.net.URLEncoder

private const val SPLASH_SCREEN = "splash"
private const val LIST_SCREEN = "list"
private const val PROGRESS_SCREEN = "progress"
private const val ADD_SCREEN = "add"
private const val ARG_HABIT_ID = "habitId"

@Composable
fun HabitNavigation() {
    val navigator = rememberNavController()

    NavHost(
        navController = navigator,
        startDestination = SPLASH_SCREEN,
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
                userId = USER_ID,
                onHabitLabelClick = { habitId -> navigator.navigate("$PROGRESS_SCREEN/${URLEncoder.encode(habitId, "UTF-8")}") },
                onAddClick = { navigator.navigate(ADD_SCREEN) },
            )
        }
        composable(
            "$PROGRESS_SCREEN/{$ARG_HABIT_ID}",
            listOf(navArgument(ARG_HABIT_ID) { type = NavType.StringType }),
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
