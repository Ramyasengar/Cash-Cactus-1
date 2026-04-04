package com.example.cashcactus.viewmodel

import androidx.lifecycle.ViewModel
import com.example.cashcactus.data.model.Investment
import com.example.cashcactus.data.remote.InvestmentData

class InvestmentViewModel : ViewModel() {

    val investments: List<Investment> = InvestmentData.getInvestments()
}