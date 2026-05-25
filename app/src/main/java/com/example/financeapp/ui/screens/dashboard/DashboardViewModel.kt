package com.example.financeapp.ui.screens.dashboard

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeapp.data.model.Expense
import com.example.financeapp.data.model.Goal
import com.example.financeapp.data.model.Income
import com.example.financeapp.data.repository.FinanceRepository
import com.example.financeapp.ui.utils.formatTimestampToDate
import com.example.financeapp.ui.utils.getCurrentMonthKey
import com.example.financeapp.ui.utils.getMonthLabelFromKey
import com.example.financeapp.ui.utils.getNextMonthKey
import com.example.financeapp.ui.utils.getPreviousMonthKey
import com.example.financeapp.ui.utils.isDateInMonth
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DashboardViewModel : ViewModel() {

    private val financeRepository = FinanceRepository()

    private val _uiState = MutableStateFlow(DashboardUiState())
    val uiState: StateFlow<DashboardUiState> = _uiState.asStateFlow()

    init {
        loadDashboardData()
    }

    fun loadDashboardData(
        monthKey: String = _uiState.value.selectedMonthKey
    ) {
        viewModelScope.launch {
            val monthLabel = getMonthLabelFromKey(monthKey)

            val allIncomes = financeRepository.getIncomesFromFirestore()
            val allExpenses = financeRepository.getExpensesFromFirestore()
            val goals = financeRepository.getGoalsFromFirestore()

            val incomes = allIncomes.filter { income ->
                isDateInMonth(income.date, monthKey)
            }

            val expenses = allExpenses.filter { expense ->
                isDateInMonth(expense.date, monthKey)
            }

            val totalIncome = incomes.sumOf { income ->
                income.amount
            }

            val totalExpenses = expenses.sumOf { expense ->
                expense.amount
            }

            val balance = totalIncome - totalExpenses

            val committedExpenses = expenses
                .filter { expense ->
                    expense.expenseType == "Committed"
                }
                .sumOf { expense ->
                    expense.amount
                }

            val discretionaryExpenses = expenses
                .filter { expense ->
                    expense.expenseType == "Discretionary"
                }
                .sumOf { expense ->
                    expense.amount
                }

            val activeGoal = goals
                .filter { goal ->
                    goal.status == "Active"
                }
                .maxByOrNull { goal ->
                    goal.createdAt.seconds
                }

            val recentTransactions = buildRecentTransactions(
                incomes = incomes,
                expenses = expenses
            )

            _uiState.update { currentState ->
                currentState.copy(
                    selectedMonthKey = monthKey,
                    monthLabel = monthLabel,

                    totalIncome = totalIncome,
                    totalExpenses = totalExpenses,
                    balance = balance,

                    committedExpenses = committedExpenses,
                    discretionaryExpenses = discretionaryExpenses,
                    savedBalance = balance,

                    goalTitle = activeGoal?.title ?: "",
                    goalDeadlineLabel = activeGoal?.let { goal ->
                        formatTimestampToDate(goal.deadline)
                    } ?: "",
                    goalProgress = calculateGoalProgress(activeGoal),
                    savedAmount = activeGoal?.currentAmount ?: 0.0,
                    remainingAmount = calculateRemainingAmount(activeGoal),
                    monthlySaving = activeGoal?.monthlyRequiredSaving ?: 0.0,

                    recentTransactions = recentTransactions
                )
            }
        }
    }

    fun onPreviousMonthClick() {
        val previousMonthKey = getPreviousMonthKey(
            _uiState.value.selectedMonthKey
        )

        loadDashboardData(previousMonthKey)
    }

    fun onNextMonthClick() {
        val nextMonthKey = getNextMonthKey(
            _uiState.value.selectedMonthKey
        )

        loadDashboardData(nextMonthKey)
    }

    fun onCurrentMonthClick() {
        loadDashboardData(getCurrentMonthKey())
    }

    private fun buildRecentTransactions(
        incomes: List<Income>,
        expenses: List<Expense>
    ): List<DashboardTransaction> {
        val incomeTransactions = incomes.map { income ->
            DashboardTransaction(
                title = income.source.ifBlank { "Income" },
                date = formatTimestampToDate(income.date),
                category = "Income • ${income.incomeType}",
                amount = "+ LKR ${formatAmount(income.amount)}",
                isIncome = true,
                createdAt = income.createdAt.seconds
            )
        }

        val expenseTransactions = expenses.map { expense ->
            DashboardTransaction(
                title = expense.categoryName.ifBlank { "Expense" },
                date = formatTimestampToDate(expense.date),
                category = "Expense • ${expense.expenseType}",
                amount = "- LKR ${formatAmount(expense.amount)}",
                isIncome = false,
                createdAt = expense.createdAt.seconds
            )
        }

        return (incomeTransactions + expenseTransactions)
            .sortedByDescending { transaction ->
                transaction.createdAt
            }
            .take(5)
    }

    private fun calculateGoalProgress(goal: Goal?): Float {
        if (goal == null || goal.targetAmount <= 0.0) {
            return 0f
        }

        return (goal.currentAmount / goal.targetAmount)
            .coerceIn(0.0, 1.0)
            .toFloat()
    }

    private fun calculateRemainingAmount(goal: Goal?): Double {
        if (goal == null) {
            return 0.0
        }

        return (goal.targetAmount - goal.currentAmount)
            .coerceAtLeast(0.0)
    }

    private fun formatAmount(value: Double): String {
        return "%,.0f".format(value)
    }
}