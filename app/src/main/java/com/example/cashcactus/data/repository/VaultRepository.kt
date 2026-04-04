package com.example.cashcactus.data.repository

import com.example.cashcactus.data.local.VaultDao
import com.example.cashcactus.data.model.VaultData

class VaultRepository(private val dao: VaultDao) {

    suspend fun insert(data: VaultData) {
        dao.insert(data)
    }

    suspend fun getAll(): List<VaultData> {
        return dao.getAll()
    }

    suspend fun delete(data: VaultData) {
        dao.delete(data)
    }
}