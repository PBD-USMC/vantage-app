package com.example.financeapp.ui.screens.expense

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeapp.data.model.Expense
import com.example.financeapp.data.repository.FinanceRepository
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId

class ExpenseViewModel : ViewModel() {

    private val financeRepository = FinanceRepository()

    private val _uiState = MutableStateFlow(ExpenseUiState())
    val uiState: StateFlow<ExpenseUiState> = _uiState.asStateFlow()

    val categories = listOf(
        "Food",
        "Rent",
        "Subscriptions",
        "Shopping",
        "Utilities",
        "Transport",
        "Entertainment",
        "Other"
    )

    val paymentMethods = listOf(
        "Cash",
        "Card",
        "Bank Transfer",
        "Wallet",
        "Other"
    )

    val expenseTypes = listOf(
        "Discretionary",
        "Committed"
    )

    init {
        loadExpenses()
    }

    fun onAmountChange(newValue: String) {
        if (isValidDecimalInput(newValue)) {
            _uiState.update {
                it.copy(
                    amount = newValue,
                    amountError = false,
                    shouldScrollToAmount = false,
                    isSavedSuccessfully = false,
                    errorMessage = ""
                )
            }
        }
    }

    fun onCategorySelected(category: String) {
        _uiState.update {
            it.copy(
                selectedCategory = category,
                isSavedSuccessfully = false,
                errorMessage = ""
            )
        }
    }

    fun onPaymentMethodSelected(paymentMethod: String) {
        _uiState.update {
            it.copy(
                selectedPaymentMethod = paymentMethod,
                isSavedSuccessfully = false,
                errorMessage = ""
            )
        }
    }

    fun onExpenseTypeSelected(expenseType: String) {
        _uiState.update {
            it.copy(
                selectedExpenseType = expenseType,
                isSavedSuccessfully = false,
                errorMessage = ""
            )
        }
    }

    fun onDateChange(newValue: String) {
        _uiState.update {
            it.copy(
                date = newValue,
                isSavedSuccessfully = false,
                errorMessage = ""
            )
        }
    }

    fun onNoteChange(newValue: String) {
        val limit = _uiState.value.noteLimit

        if (newValue.length <= limit) {
            _uiState.update {
                it.copy(
                    note = newValue,
                    isSavedSuccessfully = false,
                    errorMessage = ""
                )
            }
        }
    }

    fun onSaveExpenseClick() {
        val currentState = _uiState.value

        val parsedAmount = currentState.amount.toDoubleOrNull()
        val isAmountInvalid =
            currentState.amount.isBlank() || parsedAmount == null || parsedAmount <= 0

        if (isAmountInvalid) {
            _uiState.update {
                it.copy(
                    amountError = true,
                    shouldScrollToAmount = true,
                    isSavedSuccessfully = false,
                    errorMessage = "Please enter a valid expense amount."
                )
            }
            return
        }

        val expense = Expense(
            amount = parsedAmount,
            categoryName = currentState.selectedCategory,
            paymentMethod = currentState.selectedPaymentMethod,
            expenseType = currentState.selectedExpenseType,
            date = parseDateToTimestamp(currentState.date),
            note = currentState.note.trim(),
            createdAt = Timestamp.now()
        )

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = "",
                    isSavedSuccessfully = false
                )
            }

            val saveResult = financeRepository.addExpenseToFirestore(expense)

            if (saveResult) {
                _uiState.update {
                    it.copy(
                        amount = "",
                        amountError = false,
                        date = "",
                        note = "",
                        shouldScrollToAmount = false,
                        isLoading = false,
                        isSavedSuccessfully = true,
                        errorMessage = ""
                    )
                }

                loadExpenses()
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isSavedSuccessfully = false,
                        errorMessage = "Failed to save expense. Please check your connection and try again."
                    )
                }
            }
        }
    }

    fun loadExpenses() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = ""
                )
            }

            val expenses = financeRepository.getExpensesFromFirestore()

            _uiState.update {
                it.copy(
                    expenseList = expenses.sortedByDescending { expense -> expense.createdAt.seconds },
                    isLoading = false
                )
            }
        }
    }

    fun deleteExpense(expenseId: String) {
        viewModelScope.launch {
            val deleteResult = financeRepository.deleteExpenseFromFirestore(expenseId)

            if (deleteResult) {
                loadExpenses()
            } else {
                _uiState.update {
                    it.copy(
                        errorMessage = "Failed to delete expense."
                    )
                }
            }
        }
    }

    fun clearSuccessMessage() {
        _uiState.update {
            it.copy(
                isSavedSuccessfully = false
            )
        }
    }

    fun clearScrollRequests() {
        _uiState.update {
            it.copy(
                shouldScrollToAmount = false
            )
        }
    }

    private fun isValidDecimalInput(value: String): Boolean {
        return value.all { it.isDigit() || it == '.' } &&
                value.count { it == '.' } <= 1
    }

    private fun parseDateToTimestamp(dateText: String): Timestamp {
        return try {
            if (dateText.isBlank()) {
                Timestamp.now()
            } else {
                val localDate = LocalDate.parse(dateText)
                val instant = localDate
                    .atStartOfDay(ZoneId.systemDefault())
                    .toInstant()

                Timestamp(instant.epochSecond, 0)
            }
        } catch (exception: Exception) {
            Timestamp.now()
        }
    }
}