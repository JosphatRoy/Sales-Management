package com.example.salesmanagment

import java.util.Date

data class Sale(
    val id: String,
    val productName: String,
    val quantity: Double,
    val unitPrice: Double,
    val totalAmount: Double,
    val customerName: String?,
    val paymentMode: String? = null,
    val timestamp: Date = Date()
)