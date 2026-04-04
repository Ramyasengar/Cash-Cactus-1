package com.example.cashcactus.data.repository

import com.example.cashcactus.data.model.StockItem

class InvestmentRepository {

    suspend fun getTopStocks(): List<StockItem> {

        return listOf(
            StockItem("Reliance", 2500.0),
            StockItem("TCS", 3500.0),
            StockItem("Infosys", 1500.0),
            StockItem("HDFC Bank", 1600.0),
            StockItem("ICICI Bank", 900.0)
        )
    }
}