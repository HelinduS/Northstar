package com.example.northstar.domain.model

data class Expense(
    val id: String,
    val amount: Long,
    val currency: String = "LKR",
    val category: String,
    val expenseType: String,
    val paymentSource: String,
    val note: String?,
    val date: Long,
    val month: String,
    val createdAt: Long,
    val updatedAt: Long
)