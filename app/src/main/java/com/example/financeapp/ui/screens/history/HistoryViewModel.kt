package com.example.financeapp.ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeapp.data.model.Expense
import com.example.financeapp.data.model.Income
import com.example.financeapp.data.repository.FinanceRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class HistoryViewModel : ViewModel() {

    private val financeRepository = FinanceRepository()

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    init {
        loadHistory()
    }

    fun loadHistory() {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                message = ""
            )

            val incomes = financeRepository.getIncomesFromFirestore()
            val expenses = financeRepository.getExpensesFromFirestore()

            val totalIncome = incomes.sumOf { income ->
                income.amount
            }

            val totalExpense = expenses.sumOf { expense ->
                expense.amount
            }

            _uiState.value = _uiState.value.copy(
                incomes = incomes,
                expenses = expenses,
                totalIncome = totalIncome,
                totalExpense = totalExpense,
                balance = totalIncome - totalExpense,
                isLoading = false
            )
        }
    }

    fun onFilterChange(filter: String) {
        _uiState.value = _uiState.value.copy(
            selectedFilter = filter
        )
    }

    fun deleteIncome(incomeId: String) {
        viewModelScope.launch {
            val isDeleted = financeRepository.deleteIncomeFromFirestore(incomeId)

            if (isDeleted) {
                loadHistory()
            } else {
                _uiState.value = _uiState.value.copy(
                    message = "Failed to delete income"
                )
            }
        }
    }

    fun deleteExpense(expenseId: String) {
        viewModelScope.launch {
            val isDeleted = financeRepository.deleteExpenseFromFirestore(expenseId)

            if (isDeleted) {
                loadHistory()
            } else {
                _uiState.value = _uiState.value.copy(
                    message = "Failed to delete expense"
                )
            }
        }
    }

    fun getFilteredIncomes(): List<Income> {
        val currentState = _uiState.value

        return when (currentState.selectedFilter) {
            "Income" -> currentState.incomes
            "All" -> currentState.incomes
            else -> emptyList()
        }
    }

    fun getFilteredExpenses(): List<Expense> {
        val currentState = _uiState.value

        return when (currentState.selectedFilter) {
            "Expense" -> currentState.expenses
            "All" -> currentState.expenses
            else -> emptyList()
        }
    }
}