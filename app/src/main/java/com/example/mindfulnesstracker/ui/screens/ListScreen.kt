package com.example.mindfulnesstracker.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.mindfulnesstracker.R
import com.example.mindfulnesstracker.ui.components.HabitScaffold
import kotlin.random.Random

private val habits = listOf(
    "Привычка 1",
    "Привычка 2",
    "Привычка 3",
    "Привычка 4",
    "Привычка 5",
    "Привычка 6",
    "Привычка 7",
    "Привычка 8"
)

@Composable
fun ListScreen(onItemClick: (id: Int) -> Unit, onAddClick: () -> Unit) {
    HabitScaffold(
        screenTitle = stringResource(R.string.screen_title_list),
        showBackButton = false,
        actions = {
            IconButton(onClick = onAddClick) {
                Icon(
                    imageVector = Icons.Default.Add,
                    contentDescription = stringResource(R.string.button_add)
                )
            }
        }
    ) { paddingValues ->
        LazyColumn(
            contentPadding = paddingValues
        ) {// количество элементов и вызов кнопки
            items(habits.size) { index ->
                HabitItem(index, onItemClick)
            }
        }
    }
}

@Composable
fun HabitItem(habit: Int, onItemClick: (id: Int) -> Unit) {
    val statuses = remember {
        listOf(
            Random.nextBoolean(),
            Random.nextBoolean(),
            Random.nextBoolean(),
            Random.nextBoolean(),
            Random.nextBoolean(),
            Random.nextBoolean(),
            Random.nextBoolean()
        )
    }

    Column( // clicable добавляет возможность нажатия на элемент списка привычек
        modifier = Modifier
            .clickable {
                onItemClick(habit)
            }
            .padding(8.dp)
    ) {
        Text(
            text = habits[habit],
            fontWeight = FontWeight.Bold
        )
        Row {// для каждого статуса в списке
            statuses.forEachIndexed { index, status ->
                val color = if (status) {
                    Color.Green
                } else {
                    if (index == 6) Color.Gray else Color.Red
                }
                Column(
                    modifier = Modifier.weight(1f),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Box(
                        modifier = Modifier
                            .aspectRatio(1f)
                            .padding(8.dp)
                            .clip(CircleShape)
                            .background(color)
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
