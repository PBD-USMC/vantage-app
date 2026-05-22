package com.example.financeapp.data.model

import com.google.firebase.Timestamp

data class Income(
    val incomeId: String = "",
    val source: String = "",
    val amount: Double = 0.0,
    val currency: String = "LKR",
    val originalCurrency: String? = null,
    val originalAmount: Double? = null,
    val incomeType: String = "",
    val date: Timestamp = Timestamp.now(),
    val note: String = "",
    val createdAt: Timestamp = Timestamp.now()
)