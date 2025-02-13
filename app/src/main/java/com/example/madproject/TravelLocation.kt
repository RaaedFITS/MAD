package com.example.madproject

data class TravelLocation(
    val id: String = "",
    val category: String = "",
    val locationName: String = "",
    val rating: Double = 0.0,
    val description: String = "",
    val imageRes: Int = 0 // <--- Add this field for a local drawable resource
)

