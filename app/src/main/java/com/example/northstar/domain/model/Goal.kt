package com.example.northstar.domain.model

data class Goal(
    val id: String,
    val name: String,
    val targetAmount: Long,
    val savedAmount: Long,
    val targetDate: Long,
    val currency: String = "LKR",
    val isActive: Boolean,
    val createdAt: Long
)