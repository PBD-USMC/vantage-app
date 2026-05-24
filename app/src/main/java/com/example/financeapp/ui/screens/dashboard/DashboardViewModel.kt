package com.example.financeapp.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeapp.data.model.Expense
import com.example.financeapp.data.model.Income
import com.example.financeapp.data.repository.FinanceRepository
import com.example.financeapp.ui.utils.calculateGoalProgressPercentage
import com.example.financeapp.ui.utils.calculateRemainingAmount
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class DashboardViewModel : ViewModel() {

    private val financeRepository = FinanceRepository()

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    fun loadDashboardData() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                message = ""
            )

            val incomes = financeRepository.getIncomesFromFirestore()
            val expenses = financeRepository.getExpensesFromFirestore()
            val goals = financeRepository.getGoalsFromFirestore()

            val totalIncome = incomes.sumOf { income ->
                income.amount
            }

            val totalExpense = expenses.sumOf { expense ->
                expense.amount
            }

            val activeGoal = goals.firstOrNull { goal ->
                goal.status == "Active"
            } ?: goals.firstOrNull()

            val goalProgressPercentage = activeGoal?.let { goal ->
                calculateGoalProgressPercentage(
                    targetAmount = goal.targetAmount,
                    currentAmount = goal.currentAmount
                )
            } ?: 0

            val goalRemainingAmount = activeGoal?.let { goal ->
                calculateRemainingAmount(
                    targetAmount = goal.targetAmount,
                    currentAmount = goal.currentAmount
                )
            } ?: 0.0

            _uiState.value = _uiState.value.copy(
                incomes = incomes,
                expenses = expenses,
                goals = goals,
                totalIncome = totalIncome,
                totalExpense = totalExpense,
                balance = totalIncome - totalExpense,
                activeGoal = activeGoal,
                goalProgressPercentage = goalProgressPercentage,
                goalRemainingAmount = goalRemainingAmount,
                recentIncomes = getRecentIncomes(incomes),
                recentExpenses = getRecentExpenses(expenses),
                isLoading = false
            )
        }
    }

    private fun getRecentIncomes(incomes: List<Income>): List<Income> {
        return incomes
            .sortedByDescending { income ->
                income.date.seconds
            }
            .take(3)
    }

    private fun getRecentExpenses(expenses: List<Expense>): List<Expense> {
        return expenses
            .sortedByDescending { expense ->
                expense.date.seconds
            }
            .take(3)
    }
}