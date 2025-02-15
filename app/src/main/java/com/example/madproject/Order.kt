package com.example.madproject

data class Order(
    val orderId: String = "",
    val userKey: String = "",
    val placeId: String = "",
    val price: Double = 0.0,
    val timestamp: Long = 0L,
    val status: String = ""
)
