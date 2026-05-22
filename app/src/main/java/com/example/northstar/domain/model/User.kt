package com.example.northstar.domain.model

data class User(
    val uid: String,
    val displayName: String,
    val email: String,
    val currency: String = "LKR",
    val createdAt: Long,
    val updatedAt: Long,
    val activeGoalId: String?
)