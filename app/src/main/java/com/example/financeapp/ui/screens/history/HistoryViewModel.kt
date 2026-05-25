package com.example.financeapp.ui.screens.history

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeapp.data.model.Expense
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
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch

class HistoryViewModel : ViewModel() {

    private val financeRepository = FinanceRepository()

    private val _uiState = MutableStateFlow(HistoryUiState())
    val uiState: StateFlow<HistoryUiState> = _uiState.asStateFlow()

    val filters = listOf(
        "All",
        "Income",
        "Expense"
    )

    val filteredTransactions = uiState.map { state ->
        when (state.selectedFilter) {
            "Income" -> state.transactions.filter { transaction ->
                transaction.isIncome
            }

            "Expense" -> state.transactions.filter { transaction ->
                !transaction.isIncome
            }

            else -> state.transactions
        }
    }

    init {
        loadTransactions()
    }

    fun loadTransactions(
        monthKey: String = _uiState.value.selectedMonthKey
    ) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isLoading = true,
                message = ""
            )

            val monthLabel = getMonthLabelFromKey(monthKey)

            val allIncomes = financeRepository.getIncomesFromFirestore()
            val allExpenses = financeRepository.getExpensesFromFirestore()

            val incomes = allIncomes.filter { income ->
                isDateInMonth(
                    timestamp = income.date,
                    monthKey = monthKey
                )
            }

            val expenses = allExpenses.filter { expense ->
                isDateInMonth(
                    timestamp = expense.date,
                    monthKey = monthKey
                )
            }

            val totalIncome = incomes.sumOf { income ->
                income.amount
            }

            val totalExpense = expenses.sumOf { expense ->
                expense.amount
            }

            val transactions = buildTransactions(
                incomes = incomes,
                expenses = expenses
            )

            _uiState.value = _uiState.value.copy(
                selectedMonthKey = monthKey,
                monthLabel = monthLabel,
                transactions = transactions,
                totalIncome = totalIncome,
                totalExpense = totalExpense,
                balance = totalIncome - totalExpense,
                isLoading = false,
                message = ""
            )
        }
    }

    fun loadHistory() {
        loadTransactions()
    }

    fun onPreviousMonthClick() {
        val previousMonthKey = getPreviousMonthKey(
            _uiState.value.selectedMonthKey
        )

        loadTransactions(previousMonthKey)
    }

    fun onNextMonthClick() {
        val nextMonthKey = getNextMonthKey(
            _uiState.value.selectedMonthKey
        )

        loadTransactions(nextMonthKey)
    }

    fun onCurrentMonthClick() {
        loadTransactions(getCurrentMonthKey())
    }

    fun onFilterSelected(filter: String) {
        _uiState.value = _uiState.value.copy(
            selectedFilter = filter
        )
    }

    fun onFilterChange(filter: String) {
        onFilterSelected(filter)
    }

    fun deleteIncome(incomeId: String) {
        viewModelScope.launch {
            val isDeleted = financeRepository.deleteIncomeFromFirestore(incomeId)

            if (isDeleted) {
                loadTransactions()
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
                loadTransactions()
            } else {
                _uiState.value = _uiState.value.copy(
                    message = "Failed to delete expense"
                )
            }
        }
    }

    private fun buildTransactions(
        incomes: List<Income>,
        expenses: List<Expense>
    ): List<HistoryTransaction> {
        val incomeTransactions = incomes.map { income ->
            HistoryTransaction(
                transactionId = income.incomeId,
                title = income.source.ifBlank { "Income" },
                date = formatTimestampToDate(income.date),
                category = "Income • ${income.incomeType.ifBlank { "Income" }}",
                amount = "+ LKR ${formatAmount(income.amount)}",
                note = income.note,
                isIncome = true,
                createdAt = income.createdAt.seconds
            )
        }

        val expenseTransactions = expenses.map { expense ->
            HistoryTransaction(
                transactionId = expense.expenseId,
                title = expense.categoryName.ifBlank { "Expense" },
                date = formatTimestampToDate(expense.date),
                category = "Expense • ${expense.expenseType.ifBlank { "Expense" }} • ${expense.paymentMethod.ifBlank { "Payment" }}",
                amount = "- LKR ${formatAmount(expense.amount)}",
                note = expense.note,
                isIncome = false,
                createdAt = expense.createdAt.seconds
            )
        }

        return (incomeTransactions + expenseTransactions)
            .sortedByDescending { transaction ->
                transaction.createdAt
            }
    }

    private fun formatAmount(value: Double): String {
        return "%,.0f".format(value)
    }
}