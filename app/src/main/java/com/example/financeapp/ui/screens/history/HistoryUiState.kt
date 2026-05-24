package com.example.financeapp.ui.screens.history

import com.example.financeapp.data.model.Expense
import com.example.financeapp.data.model.Income

data class HistoryUiState(
    val incomes: List<Income> = emptyList(),
    val expenses: List<Expense> = emptyList(),

    val selectedFilter: String = "All",

    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val balance: Double = 0.0,

    val isLoading: Boolean = false,
    val message: String = ""
)