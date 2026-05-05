package com.example.northstar.domain.model

data class Expense(
    val id: String = "",
    val amount: Long = 0L,             // in LKR paisa
    val category: String = "",         // RENT, FOOD, TRANSPORT, etc.
    val expenseType: String = "",      // COMMITTED or DISCRETIONARY
    val paymentMethod: String = "",    // CARD, CASH, BANK_TRANSFER, OTHER
    val description: String? = null,
    val date: Long = 0L,
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
)