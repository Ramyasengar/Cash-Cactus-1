package com.example.cashcactus.data.model

data class Investment(
    val name: String,
    val type: String, // "Stock", "Mutual Fund", "Gold"
    val risk: String,
    val returns: String,
    val link: String
)