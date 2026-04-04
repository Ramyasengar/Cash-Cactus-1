package com.example.cashcactus.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.mutableStateListOf
import com.example.cashcactus.data.local.VaultDatabase
import com.example.cashcactus.data.model.VaultData
import com.example.cashcactus.data.repository.VaultRepository
import kotlinx.coroutines.launch

class VaultViewModel(application: Application) : AndroidViewModel(application) {

    private val dao = VaultDatabase.getDatabase(application).vaultDao()
    private val repository = VaultRepository(dao)

    var vaultList = mutableStateListOf<VaultData>()  // ✅ fixed

    fun insert(data: VaultData) {
        viewModelScope.launch {
            repository.insert(data)
            loadData()
        }
    }

    fun loadData() {
        viewModelScope.launch {
            vaultList.clear()
            vaultList.addAll(repository.getAll())
        }
    }

    fun delete(data: VaultData) {
        viewModelScope.launch {
            repository.delete(data)
            loadData()
        }
    }
}