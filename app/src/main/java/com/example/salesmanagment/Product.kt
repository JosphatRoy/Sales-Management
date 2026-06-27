package com.example.salesmanagment

data class Product(
    val id: String = "",
    val name: String = "",
    val category: String = "",
    val price: Double = 0.0,
    val stock: Int = 0
)