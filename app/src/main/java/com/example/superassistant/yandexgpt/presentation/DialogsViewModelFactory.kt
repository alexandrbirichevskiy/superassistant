package com.example.superassistant.yandexgpt.presentation

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.superassistant.yandexgpt.data.database.DialogsDao

class DialogsViewModelFactory(
    private val dao: DialogsDao
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(DialogsViewModel::class.java)) {
            return DialogsViewModel(dao) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
