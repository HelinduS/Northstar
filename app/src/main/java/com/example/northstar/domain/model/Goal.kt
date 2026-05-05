package com.example.northstar.domain.model

data class Goal(
    val id: String = "",
    val name: String = "",
    val targetAmount: Long = 0L,       // in LKR paisa
    val savedAmount: Long = 0L,        // in LKR paisa
    val targetDate: Long = 0L,
    val isActive: Boolean = true,
    val createdAt: Long = 0L
)