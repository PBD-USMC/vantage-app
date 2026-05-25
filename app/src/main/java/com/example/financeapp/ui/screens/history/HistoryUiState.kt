package com.example.financeapp.ui.screens.history

import com.example.financeapp.ui.utils.getCurrentMonthKey
import com.example.financeapp.ui.utils.getMonthLabelFromKey

data class HistoryUiState(
    val selectedMonthKey: String = getCurrentMonthKey(),
    val monthLabel: String = getMonthLabelFromKey(getCurrentMonthKey()),
    val selectedFilter: String = "All",

    val transactions: List<HistoryTransaction> = emptyList(),

    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val balance: Double = 0.0,

    val isLoading: Boolean = false,
    val message: String = ""
)

data class HistoryTransaction(
    val transactionId: String,
    val title: String,
    val date: String,
    val category: String,
    val amount: String,
    val note: String = "",
    val isIncome: Boolean,
    val createdAt: Long = 0L
)