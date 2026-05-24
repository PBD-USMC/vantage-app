package com.example.financeapp.ui.utils

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.util.Date
import java.util.Locale
import kotlin.math.ceil

fun formatTimestampToDate(timestamp: Timestamp): String {
    val date = timestamp.toDate()
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return formatter.format(date)
}

fun parseDateToTimestamp(dateText: String): Timestamp {
    val localDate = LocalDate.parse(dateText)
    val instant = localDate
        .atStartOfDay(ZoneId.systemDefault())
        .toInstant()

    return Timestamp(Date.from(instant))
}

fun calculateMonthlyRequiredSaving(
    targetAmount: Double,
    currentAmount: Double,
    deadlineText: String
): Double {
    return try {
        val deadline = LocalDate.parse(deadlineText)
        val today = LocalDate.now()

        val currentMonth = YearMonth.from(today)
        val deadlineMonth = YearMonth.from(deadline)

        val monthsRemaining = (
                (deadlineMonth.year - currentMonth.year) * 12 +
                        (deadlineMonth.monthValue - currentMonth.monthValue)
                ).coerceAtLeast(1)

        val remainingAmount = (targetAmount - currentAmount).coerceAtLeast(0.0)

        ceil(remainingAmount / monthsRemaining)
    } catch (exception: Exception) {
        0.0
    }
}

fun calculateGoalProgressPercentage(
    targetAmount: Double,
    currentAmount: Double
): Int {
    if (targetAmount <= 0.0) {
        return 0
    }

    val progress = (currentAmount / targetAmount) * 100
    return progress.toInt().coerceIn(0, 100)
}

fun calculateRemainingAmount(
    targetAmount: Double,
    currentAmount: Double
): Double {
    return (targetAmount - currentAmount).coerceAtLeast(0.0)
}

fun isGoalCompleted(
    targetAmount: Double,
    currentAmount: Double
): Boolean {
    return targetAmount > 0.0 && currentAmount >= targetAmount
}