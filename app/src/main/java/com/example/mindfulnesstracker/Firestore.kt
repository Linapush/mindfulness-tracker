package com.example.mindfulnesstracker

import android.annotation.SuppressLint
import android.util.Log
import com.google.firebase.Timestamp
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import kotlinx.coroutines.tasks.await

@SuppressLint("StaticFieldLeak")
val db = FirebaseFirestore.getInstance()

data class User(val Id: String, val habits: List<Habit>)

data class Habit(
    var habitId: String,
    val name: String,
    val notification: String,
    var progress: List<ProgressItem>,
) {
    constructor() : this("1", "", "", emptyList())
}

data class ProgressItem(
    var itemId: String,
    val date: Timestamp,
    val indicator: Boolean,
) {
    constructor() : this("", Timestamp(0, 0), false)
}

fun getUser(
    username: String,
    onUserReceived: (User) -> Unit,
) {
    db.collection("users").whereEqualTo("username", username).get()
        .addOnSuccessListener { documents ->
            for (document in documents) {
                val user = document.toObject(User::class.java)
                onUserReceived(user)
            }
        }.addOnFailureListener { exception ->
            println("Ошибка при получении пользователя: $exception")
        }
}

suspend fun getHabitsForUser(userId: String): List<Habit> {
    val habitReference = db.collection("users").document(userId).collection("habits")
    val snapshot =
        try {
            habitReference.get().await()
        } catch (e: FirebaseFirestoreException) {
            throw RuntimeException("Ошибка при получении данных из Firebase", e)
        }
    return snapshot.map {
        it.toObject(Habit::class.java)
            .also { habit ->
                habit.habitId = it.id
                habit.progress = getProgressForHabit(userId, habitId = it.id)
            }
    }
}

suspend fun getProgressForHabit(
    userId: String,
    habitId: String,
): List<ProgressItem> {
    val progressReference =
        db.collection("users")
            .document(userId)
            .collection("habits")
            .document(habitId)
            .collection("progress")

    val snapshot =
        try {
            progressReference.get().await()
        } catch (e: FirebaseFirestoreException) {
            throw RuntimeException("Ошибка при получении данных из Firebase", e)
        }
    return snapshot.map {
        it.toObject(ProgressItem::class.java)
            .also { progress ->
                progress.itemId = it.id
            }
    }
}

fun addUser(username: String) {
    val user =
        hashMapOf(
            "username" to username,
        )
    db.collection("users").add(user).addOnSuccessListener { documentReference ->
        println("Новый пользователь успешно добавлен с ID: ${documentReference.id}")
    }.addOnFailureListener { exception ->
        println("Ошибка при добавлении нового пользователя: $exception")
    }
}

fun addHabit(
    userId: String,
    name: String,
) {
    val habit =
        hashMapOf(
            "name" to name,
            "statusList" to listOf<Boolean>(false),
        )
    db.collection("users").document(userId).collection("habits").add(habit)
        .addOnSuccessListener { documentReference ->
            Log.d("firestore", "Новая привычка успешно добавлена с ID: ${documentReference.id}")
        }.addOnFailureListener { exception ->
            Log.d("firestore", "Ошибка при добавлении новой привычки: $exception")
        }
}

fun updateHabitStatus(
    userId: String,
    habitId: String,
    dayIndex: Int,
    status: Boolean,
) {
    db.collection("users").document(userId).collection("habits").document(habitId)
        .update(hashMapOf("statusList.$dayIndex" to status) as Map<String, Any>)
        .addOnSuccessListener {
            Log.d("firestore", "Статус привычки обновлен")
        }.addOnFailureListener { exception ->
            Log.d("firestore", "Ошибка при обновлении статуса привычки: $exception")
        }
}
