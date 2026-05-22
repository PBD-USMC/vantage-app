package com.example.financeapp.ui.screens.income

import com.example.financeapp.data.model.Income

data class IncomeUiState(
    val amountReceived: String = "",
    val amountReceivedError: Boolean = false,

    val selectedIncomeSource: String = "Salary",
    val selectedIncomeType: String = "Fixed",

    val dateReceived: String = "",
    val currency: String = "LKR",

    val originalCurrency: String = "",
    val originalAmount: String = "",
    val originalAmountError: Boolean = false,

    val note: String = "",
    val noteLimit: Int = 120,

    val incomeList: List<Income> = emptyList(),

    val isLoading: Boolean = false,
    val errorMessage: String = "",
    val isSavedSuccessfully: Boolean = false
)