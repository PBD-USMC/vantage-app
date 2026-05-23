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

class ExpenseViewModel : ViewModel() {

    private val financeRepository = FinanceRepository()

    private val _uiState = MutableStateFlow(ExpenseUiState())
    val uiState: StateFlow<ExpenseUiState> = _uiState.asStateFlow()

    val categories = listOf(
        "Food",
        "Transport",
        "Rent",
        "Utilities",
        "Subscriptions",
        "Entertainment",
        "Shopping",
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

    fun onAmountChange(newValue: String) {
            _uiState.update {
                it.copy(
                    amount = newValue,
                    amountError = false,
                    isSavedSuccessfully = false,
                    errorMessage = ""
                )
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


    }