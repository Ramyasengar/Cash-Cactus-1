package com.example.cashcactus.data.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "vault_data")
data class VaultData(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val title: String,
    val encryptedContent: String,
    val iv: String
)