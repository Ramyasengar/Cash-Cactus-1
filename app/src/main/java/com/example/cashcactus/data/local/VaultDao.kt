package com.example.cashcactus.data.local

import androidx.room.*
import com.example.cashcactus.data.model.VaultData

@Dao
interface VaultDao {

    @Insert
    suspend fun insert(data: VaultData)

    @Query("SELECT * FROM vault_data")
    suspend fun getAll(): List<VaultData>

    @Delete
    suspend fun delete(data: VaultData)
}