package com.example.northstar.domain.model

data class Income(
    val id: String,
    val sourceType: String,
    val projectName: String?,
    val amount: Long,
    val currency: String = "LKR",
    val amountLKR: Long,
    val exchangeRate: Double,
    val receivedDate: Long,
    val month: String,
    val note: String?,
    val createdAt: Long,
    val updatedAt: Long
)