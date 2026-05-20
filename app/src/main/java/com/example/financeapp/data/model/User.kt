package com.example.financeapp.data.model

data class User(
    val userId: String = "",
    val name: String = "",
    val email: String = "",
    val baseCurrency: String = "LKR",
    val createdAt: Long = System.currentTimeMillis()
)