package com.example.northstar.domain.model

data class Income(
    val id: String = "",
    val sourceType: String = "",       // SALARY, FREELANCE, ADSENSE, CRYPTO, OTHER
    val projectName: String? = null,   // for FREELANCE only
    val originalAmount: Long = 0L,
    val originalCurrency: String = "LKR",
    val lkrAmount: Long = 0L,
    val exchangeRate: Double = 1.0,
    val date: Long = 0L,
    val notes: String? = null,
    val createdAt: Long = 0L,
    val updatedAt: Long = 0L
)