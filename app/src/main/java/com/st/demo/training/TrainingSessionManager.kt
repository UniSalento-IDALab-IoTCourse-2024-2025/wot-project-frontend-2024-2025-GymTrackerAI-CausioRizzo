package com.st.demo.training

import android.annotation.SuppressLint
import android.content.Context
import kotlinx.coroutines.*
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.ConcurrentHashMap

@SuppressLint("StaticFieldLeak")
object TrainingSessionManager {

    private val repetitions = ConcurrentHashMap<String, Int>()
    private val totalSeconds = ConcurrentHashMap<String, Long>()
    private val activeTimers = ConcurrentHashMap<String, Job>()

    //private var currentActivity: String? = null

    private lateinit var context: Context
    private const val PREF_NAME = "TrainingPrefs"
    private const val PREF_DATE_KEY = "last_reset_date"
    private const val PREF_HISTORY_KEY = "training_history"

    private val history = mutableMapOf<String, DayRecord>()
    private var plankJob: Job? = null

    fun initialize(appContext: Context) {
        context = appContext
        loadHistory()
    }

    fun checkAndResetDaily() {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val lastDate = prefs.getString(PREF_DATE_KEY, null)
        val currentDate = getTodayDateString()

        if (lastDate == null || lastDate != currentDate) {
            saveTodayToHistory(lastDate ?: currentDate)
            prefs.edit().putString(PREF_DATE_KEY, currentDate).apply()
        }
    }

    /*fun updateActivity(newActivity: String) {
        if (newActivity != currentActivity) {
            currentActivity?.let {
                stopTimer(it)
            }
            increment(newActivity)
            currentActivity = newActivity
        }
    }*/

    fun loadTodayStateFromHistory() {
        val today = getTodayDateString()
        history[today]?.let { record ->
            repetitions["Plank"] = record.plankReps
            repetitions["JumpingJack"] = record.jumpingJackReps
            repetitions["SquatJack"] = record.squatJackReps

            totalSeconds["Plank"] = record.plankTime
            totalSeconds["JumpingJack"] = record.jumpingJackTime
            totalSeconds["SquatJack"] = record.squatJackTime
        }
    }

     fun saveTodayToHistory(date: String) {
        val record = DayRecord(
            plankTime = totalSeconds.getOrDefault("Plank", 0L),
            plankReps = repetitions.getOrDefault("Plank", 0),
            jumpingJackTime = totalSeconds.getOrDefault("JumpingJack", 0L),
            jumpingJackReps = repetitions.getOrDefault("JumpingJack", 0),
            squatJackTime = totalSeconds.getOrDefault("SquatJack", 0L),
            squatJackReps = repetitions.getOrDefault("SquatJack", 0)
        )
        history[date] = record
    }

    fun increment(activity: String) {

        //solo jumpingjack e squatjack hanno ripetizioni!
        if (activity == "JumpingJack" || activity == "SquatJack") {
            repetitions[activity] = repetitions.getOrDefault(activity, 0) + 1
        }

        activeTimers.keys.forEach { otherActivity ->
            if(otherActivity != activity) {
                stopTimer(otherActivity)
            }
        }

        if (!activeTimers.containsKey(activity)) {
            startTimer(activity)
        }

        saveTodayToHistory(getTodayDateString())
        saveHistory()
    }

    private fun startTimer(activity: String) {
        val job = CoroutineScope(Dispatchers.Default).launch {
            while (isActive) {
                delay(1000)
                totalSeconds[activity] = totalSeconds.getOrDefault(activity, 0L) + 1
            }
        }
        activeTimers[activity] = job
    }

    fun stopTimer(activity: String) {
        activeTimers[activity]?.cancel()
        activeTimers.remove(activity)
    }

    fun getRepetitions(activity: String): Int = repetitions.getOrDefault(activity, 0)

    fun getTime(activity: String): String {
        val total = totalSeconds.getOrDefault(activity, 0L)
        val minutes = total / 60
        val seconds = total % 60
        return String.format("%d min %02d sec", minutes, seconds)
    }

    fun deleteDay(date: String) {
        history.remove(date)
        saveHistory()
    }

    fun getDailyComparison(activity: String): String {
        val today = getTodayDateString()
        val yesterday = getYesterdayDateString()

        val todayRecord = history[today]
        val yesterdayRecord = history[yesterday]

        if (activity == "Plank") {
            val todayTime = todayRecord?.plankTime ?: totalSeconds.getOrDefault("Plank", 0L)
            val yesterdayTime = yesterdayRecord?.plankTime ?: 0L

            return when {
                yesterdayTime == 0L && todayTime == 0L -> "Nessun allenamento oggi e ieri."
                yesterdayTime == 0L && todayTime > 0L -> "Oggi hai svolto questo esercizio."
                yesterdayTime > 0L && todayTime == 0L -> "Ieri hai svolto questo esercizio."
                todayTime > yesterdayTime -> "Oggi hai svolto più tempo di ieri."
                todayTime == yesterdayTime -> "Sia oggi che ieri hai svolto lo stesso tempo."
                else -> "Nessun dato disponibile ancora."
            }
        }

        // Logica normale per JumpingJack e SquatJack
        val todayReps = when (activity) {
            "JumpingJack" -> todayRecord?.jumpingJackReps ?: repetitions.getOrDefault("JumpingJack", 0)
            "SquatJack" -> todayRecord?.squatJackReps ?: repetitions.getOrDefault("SquatJack", 0)
            else -> 0
        }

        val yesterdayReps = when (activity) {
            "JumpingJack" -> yesterdayRecord?.jumpingJackReps ?: 0
            "SquatJack" -> yesterdayRecord?.squatJackReps ?: 0
            else -> 0
        }

        if (yesterdayReps == 0 && todayReps == 0) {
            return "Nessun allenamento oggi e ieri."
        }

        if (yesterdayReps == 0 && todayReps > 0) {
            return "Oggi hai svolto questo esercizio."
        }

        val delta = todayReps - yesterdayReps
        val percentage = (delta * 100.0 / yesterdayReps).toInt().coerceAtMost(100)

        return if (percentage >= 0) {
            "Oggi +$percentage% rispetto a ieri."
        } else {
            "Oggi ${percentage}% rispetto a ieri."
        }
    }

    /*fun getDailyComparison(activity: String): String {
        val today = getTodayDateString()
        val yesterday = getYesterdayDateString()

        val todayRecord = history[today]
        val yesterdayRecord = history[yesterday]

        val todayReps = when (activity) {
            "Plank" -> ((todayRecord?.plankTime ?: totalSeconds.getOrDefault("Plank", 0L)) / 60).toInt() //secondi in minuti
            "JumpingJack" -> todayRecord?.jumpingJackReps ?: repetitions.getOrDefault("JumpingJack", 0)
            "SquatJack" -> todayRecord?.squatJackReps ?: repetitions.getOrDefault("SquatJack", 0)
            else -> 0
        }

        val yesterdayReps = when (activity) {
            "Plank" -> ((yesterdayRecord?.plankTime ?: 0L) / 60).toInt() //secondi in minuti
            "JumpingJack" -> yesterdayRecord?.jumpingJackReps ?: 0
            "SquatJack" -> yesterdayRecord?.squatJackReps ?: 0
            else -> 0
        }

        if (yesterdayReps == 0) {
            return "Ieri non hai svolto questo esercizio."
        }

        val delta = todayReps - yesterdayReps
        val percentage = (delta * 100.0 / yesterdayReps).toInt()

        return if (percentage >= 0) {
            "Oggi +$percentage% rispetto a ieri."
        } else {
            "Oggi ${percentage}% rispetto a ieri."
        }
    }*/

    private fun getYesterdayDateString(): String {
        val cal = Calendar.getInstance().apply { add(Calendar.DATE, -1) }
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(cal.time)
    }

    fun reset() {
        repetitions.clear()
        totalSeconds.clear()
    }

    fun getHistory(): Map<String, DayRecord> = history.toSortedMap(reverseOrder())

    fun getTodayDateString(): String {
        val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
        return sdf.format(Date())
    }

    private fun saveHistory() {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val serialized = history.entries.joinToString(";") { (date, record) ->
            listOf(
                date,
                record.plankTime,
                record.plankReps,
                record.jumpingJackTime,
                record.jumpingJackReps,
                record.squatJackTime,
                record.squatJackReps
            ).joinToString(",")
        }
        prefs.edit().putString(PREF_HISTORY_KEY, serialized).apply()
    }

    fun startPlankTimer() {
        if(plankJob == null) {
            plankJob = CoroutineScope(Dispatchers.Default).launch {
                while (isActive) {
                    delay(1000)
                    totalSeconds["Plank"] = totalSeconds.getOrDefault("Plank", 0L) + 1
                }
            }
        }
    }

    fun stopPlankTimer() {
        plankJob?.cancel()
        plankJob = null
    }

    private fun loadHistory() {
        val prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE)
        val serialized = prefs.getString(PREF_HISTORY_KEY, null) ?: return
        serialized.split(";").forEach { entry ->
            val parts = entry.split(",")
            if (parts.size == 7) {
                val date = parts[0]
                val record = DayRecord(
                    plankTime = parts[1].toLong(),
                    plankReps = parts[2].toInt(),
                    jumpingJackTime = parts[3].toLong(),
                    jumpingJackReps = parts[4].toInt(),
                    squatJackTime = parts[5].toLong(),
                    squatJackReps = parts[6].toInt()
                )
                history[date] = record
            }
        }
    }

    data class DayRecord(
        val plankTime: Long,
        val plankReps: Int,
        val jumpingJackTime: Long,
        val jumpingJackReps: Int,
        val squatJackTime: Long,
        val squatJackReps: Int
    )
}