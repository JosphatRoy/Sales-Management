package com.example.salesmanagment

import java.util.Date

data class Sale(
    val id: String = "",
    val productName: String = "",
    val quantity: Double = 0.0,
    val unitPrice: Double = 0.0,
    val totalAmount: Double = 0.0,
    val customerName: String? = null,
    val paymentMode: String? = null,
    val timestamp: Date = Date()
)