package com.example.northstar.domain.model

data class Budget(
    val id: String,
    val category: String,
    val limitAmount: Long,
    val spentAmount: Long = 0L,
    val period: String = "CUSTOM",
    val warningThreshold: Int = 80,
    val month: String = "",
    val createdAt: Long = System.currentTimeMillis(),
    val startDate: Long? = null,
    val endDate: Long? = null
)