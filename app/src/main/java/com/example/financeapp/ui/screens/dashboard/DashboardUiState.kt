package com.example.financeapp.ui.screens.dashboard

import com.example.financeapp.data.model.Expense
import com.example.financeapp.data.model.Goal
import com.example.financeapp.data.model.Income

data class DashboardUiState(
    val incomes: List<Income> = emptyList(),
    val expenses: List<Expense> = emptyList(),
    val goals: List<Goal> = emptyList(),

    val totalIncome: Double = 0.0,
    val totalExpense: Double = 0.0,
    val balance: Double = 0.0,

    val activeGoal: Goal? = null,
    val goalProgressPercentage: Int = 0,
    val goalRemainingAmount: Double = 0.0,

    val recentIncomes: List<Income> = emptyList(),
    val recentExpenses: List<Expense> = emptyList(),

    val isLoading: Boolean = false,
    val message: String = ""
)