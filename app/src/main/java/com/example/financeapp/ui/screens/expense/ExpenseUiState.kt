package com.example.financeapp.ui.screens.expense

import com.example.financeapp.data.model.Expense

data class ExpenseUiState(
    val amount: String = "",
    val amountError: Boolean = false,

    val selectedCategory: String = "Food",
    val selectedPaymentMethod: String = "Cash",
    val selectedExpenseType: String = "Discretionary",

    val date: String = "",

    val note: String = "",
    val noteLimit: Int = 120,

    val expenseList: List<Expense> = emptyList(),

    val shouldScrollToAmount: Boolean = false,

    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val isSavedSuccessfully: Boolean = false
)