package com.example.financeapp.data.model

import com.google.firebase.Timestamp

data class Expense(
    val expenseId: String = "",
    val amount: Double = 0.0,
    val categoryName: String = "",
    val paymentMethod: String = "",
    val expenseType: String = "",
    val date: Timestamp = Timestamp.now(),
    val note: String = "",
    val createdAt: Timestamp = Timestamp.now()
)