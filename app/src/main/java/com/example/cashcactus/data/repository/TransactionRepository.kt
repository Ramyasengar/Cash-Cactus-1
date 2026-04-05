package com.example.cashcactus.data.repository

import com.example.cashcactus.data.local.AppDao
import com.example.cashcactus.data.model.Transaction
import kotlinx.coroutines.flow.Flow

class TransactionRepository(
    private val appDao: AppDao
) {

    fun getAllTransactions(): Flow<List<Transaction>> {
        return appDao.getAllTransactions()
    }

    fun getExpenses(): Flow<List<Transaction>> {
        return appDao.getTransactionsByType("expense")
    }

    fun getIncome(): Flow<List<Transaction>> {
        return appDao.getTransactionsByType("income")
    }

    suspend fun insert(transaction: Transaction) {
        appDao.insertTransaction(transaction)
    }

    suspend fun delete(transaction: Transaction) {
        appDao.deleteTransaction(transaction)
    }

    suspend fun update(transaction: Transaction) {
        appDao.updateTransaction(transaction)
    }
}