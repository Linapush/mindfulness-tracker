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
    val creationTimestamp: Timestamp,
    var progress: List<ProgressItem>,
) {
    constructor() : this("1", "", "", Timestamp.now(), emptyList())
}

data class ProgressItem(
    var itemId: String,
    val dayEpoch: Long,
    val indicator: Boolean,
) {
    constructor() : this("", 0, false)
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
    val db = FirebaseFirestore.getInstance()
    val habitRef = db.collection("users").document(userId).collection("habits").document(habitId)

    return try {
        val document = habitRef.get().await()
        if (document.exists()) {
            val statusList = document.get("statusList")
            Log.d("firestore", "statusList: $statusList")

            if (statusList is List<*>) {
                statusList.mapNotNull { item ->
                    if (item is Map<*, *>) {
                        ProgressItem(
                            itemId = item["itemId"] as? String ?: "",
                            dayEpoch = item["dayEpoch"] as? Long ?: 0,
                            indicator = item["indicator"] as? Boolean ?: false,
                        )
                    } else {
                        null
                    }
                }
            } else {
                emptyList()
            }
        } else {
            emptyList()
        }
    } catch (e: FirebaseFirestoreException) {
        throw RuntimeException("Ошибка при получении данных из Firebase", e)
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
    hour: Int,
    minute: Int,
) {
    val habit =
        hashMapOf(
            "name" to name,
            "hour" to hour,
            "minute" to minute,
            "creationTimestamp" to Timestamp.now(),
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
    dayEpoch: Long,
    status: Boolean,
    onSuccess: () -> Unit,
) {
    val db = FirebaseFirestore.getInstance()
    val habitRef = db.collection("users").document(userId).collection("habits").document(habitId)

    habitRef.get().addOnSuccessListener { document ->
        if (document.exists()) {
            val statusList = document.get("statusList") as? List<Map<String, Any>> ?: emptyList()
            val updatedStatusList = statusList.toMutableList()
            var statusUpdated = false

            for (i in 0 until updatedStatusList.size) {
                val item = updatedStatusList[i].toMutableMap()
                if (item["dayEpoch"] == dayEpoch) {
                    item["indicator"] = status
                    updatedStatusList[i] = item.toMap()
                    statusUpdated = true
                    break
                }
            }

            if (!statusUpdated) {
                val newStatusItem =
                    mapOf(
                        "itemId" to dayEpoch,
                        "dayEpoch" to dayEpoch,
                        "indicator" to status,
                    )
                updatedStatusList.add(newStatusItem)
            }

            habitRef.update("statusList", updatedStatusList)
                .addOnSuccessListener {
                    Log.d("firestore", "Статус привычки обновлен")
                    onSuccess()
                }
                .addOnFailureListener { exception ->
                    Log.d("firestore", "Ошибка при обновлении статуса привычки: $exception")
                }
        } else {
            Log.d("firestore", "Документ не найден")
        }
    }.addOnFailureListener { exception ->
        Log.d("firestore", "Ошибка при получении документа: $exception")
    }
}
