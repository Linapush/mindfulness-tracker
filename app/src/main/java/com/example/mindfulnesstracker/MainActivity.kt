package com.example.mindfulnesstracker

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.mindfulnesstracker.ui.HabitNavigation
import com.example.mindfulnesstracker.ui.theme.MindfulnessTrackerTheme

const val USER_ID = "tyPH6CtFeQAt5dh1G0PX"

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        setContent {
            MindfulnessTrackerTheme {
                HabitNavigation()
            }
        }
    }
}

// class MainActivity : ComponentActivity() {
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//        setContent {
//            MindfulnessTrackerTheme {
//                // A surface container using the 'background' color from the theme
//                Surface(
//                    modifier = Modifier.fillMaxSize(),
//                    color = MaterialTheme.colorScheme.background,
//                ) {
//                    Greeting("Android")
//                }
//            }
//        }
//    }
// }
//
// @Composable
// fun Greeting(
//    name: String,
//    modifier: Modifier = Modifier,
// ) {
//    Button(
//        onClick = {
//            throw RuntimeException("Test Crash")
//        },
//    ) { Text(text = "Crash") }
// }
//
// @Preview(showBackground = true)
// @Composable
// fun GreetingPreview() {
//    MindfulnessTrackerTheme {
//        Greeting("Android")
//    }
// }
