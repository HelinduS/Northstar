package com.example.northstar.domain.model

data class User(
    val uid: String = "",
    val displayName: String = "",
    val email: String = "",
    val createdAt: Long = 0L,
    val defaultCurrency: String = "LKR",
    val activeGoalId: String? = null
)