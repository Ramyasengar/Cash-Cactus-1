package com.example.cashcactus.data.remote

import com.example.cashcactus.data.model.Investment

object InvestmentData {

    fun getInvestments(): List<Investment> {
        return listOf(

            Investment(
                name = "SBI Bluechip Fund",
                type = "Mutual Fund",
                risk = "Medium",
                returns = "12% avg",
                link = "https://www.sbimf.com"
            ),

            Investment(
                name = "Reliance Industries",
                type = "Stock",
                risk = "High",
                returns = "15% avg",
                link = "https://www.nseindia.com"
            ),

            Investment(
                name = "Digital Gold",
                type = "Gold",
                risk = "Low",
                returns = "8% avg",
                link = "https://www.paytm.com/gold"
            )
        )
    }
}