package com.example.cashcactus.data.remote

data class StockResponse(
    val quoteResponse: QuoteResponse
)

data class QuoteResponse(
    val result: List<StockItem>
)

data class StockItem(
    val regularMarketPrice: Double,
    val regularMarketChangePercent: Double
)