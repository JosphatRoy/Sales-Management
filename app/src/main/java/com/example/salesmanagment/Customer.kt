package com.example.salesmanagment

data class Customer(
    val id: String,
    val name: String,
    val phone: String,
    val email: String,
    val totalPurchases: Double = 0.0
)