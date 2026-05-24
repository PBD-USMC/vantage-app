package com.example.financeapp.ui.screens.dashboard

import com.example.financeapp.ui.utils.getCurrentMonthKey
import com.example.financeapp.ui.utils.getMonthLabelFromKey

data class DashboardUiState(
    val selectedMonthKey: String = getCurrentMonthKey(),
    val monthLabel: String = getMonthLabelFromKey(getCurrentMonthKey()),

    val totalIncome: Double = 0.0,
    val totalExpenses: Double = 0.0,
    val balance: Double = 0.0,

    val goalTitle: String = "",
    val goalDeadlineLabel: String = "",
    val goalProgress: Float = 0f,
    val savedAmount: Double = 0.0,
    val remainingAmount: Double = 0.0,
    val monthlySaving: Double = 0.0,

    val committedExpenses: Double = 0.0,
    val discretionaryExpenses: Double = 0.0,
    val savedBalance: Double = 0.0,

    val recentTransactions: List<DashboardTransaction> = emptyList()
)

data class DashboardTransaction(
    val title: String,
    val date: String,
    val category: String,
    val amount: String,
    val isIncome: Boolean,
    val createdAt: Long = 0L
)