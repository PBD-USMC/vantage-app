package com.example.financeapp.data.model

import com.google.firebase.Timestamp

data class Goal(
    val goalId: String = "",
    val title: String = "",
    val targetAmount: Double = 0.0,
    val currentAmount: Double = 0.0,
    val deadline: Timestamp = Timestamp.now(),
    val monthlyRequiredSaving: Double = 0.0,
    val status: String = "Active",
    val createdAt: Timestamp = Timestamp.now()
)