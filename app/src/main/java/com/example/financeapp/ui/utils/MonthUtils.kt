package com.example.financeapp.ui.utils

import com.google.firebase.Timestamp
import java.text.SimpleDateFormat
import java.time.LocalDate
import java.time.YearMonth
import java.time.ZoneId
import java.time.format.DateTimeFormatter
import java.time.format.TextStyle
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

fun getCurrentMonthKey(): String {
    val currentMonth = YearMonth.now()
    return formatMonthKey(currentMonth)
}

fun getCurrentMonthLabel(): String {
    val currentMonth = YearMonth.now()
    return formatMonthLabel(currentMonth)
}

fun formatMonthKey(
    yearMonth: YearMonth
): String {
    return yearMonth.format(
        DateTimeFormatter.ofPattern("yyyy-MM")
    )
}

fun formatMonthLabel(
    yearMonth: YearMonth
): String {
    val monthName = yearMonth.month.getDisplayName(
        TextStyle.FULL,
        Locale.ENGLISH
    )

    return "$monthName ${yearMonth.year}"
}

fun parseMonthKey(
    monthKey: String
): YearMonth {
    return YearMonth.parse(
        monthKey,
        DateTimeFormatter.ofPattern("yyyy-MM")
    )
}

fun getPreviousMonthKey(
    monthKey: String
): String {
    val yearMonth = parseMonthKey(monthKey)
    return formatMonthKey(yearMonth.minusMonths(1))
}

fun getNextMonthKey(
    monthKey: String
): String {
    val yearMonth = parseMonthKey(monthKey)
    return formatMonthKey(yearMonth.plusMonths(1))
}

fun getMonthLabelFromKey(
    monthKey: String
): String {
    val yearMonth = parseMonthKey(monthKey)
    return formatMonthLabel(yearMonth)
}

fun isDateInMonth(
    timestamp: Timestamp,
    monthKey: String
): Boolean {
    val formattedDate = formatTimestampToDate(timestamp)
    return formattedDate.startsWith(monthKey)
}

fun formatMonthLabelFromDate(
    date: LocalDate = LocalDate.now()
): String {
    val monthName = date.month.getDisplayName(
        TextStyle.FULL,
        Locale.ENGLISH
    )

    return "$monthName ${date.year}"
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