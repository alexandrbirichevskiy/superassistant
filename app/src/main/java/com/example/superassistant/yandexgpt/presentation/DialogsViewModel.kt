package com.example.superassistant.yandexgpt.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.superassistant.SuperAssistantRetrofit
import com.example.superassistant.yandexgpt.data.DialogsRepository
import com.example.superassistant.yandexgpt.data.database.DialogsDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DialogsViewModel(private val dao: DialogsDao) : ViewModel() {

    private val repository by lazy { DialogsRepository(dao) }

    val dialogs = repository.getDialogs().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )
    val services = MutableStateFlow(listOf("YandexGPT"))
    val models = MutableStateFlow(listOf("yandexgpt/latest", "yandexgpt-lite"))

    fun addDialog(name: String, service: String, model: String) {
        viewModelScope.launch {
            repository.addDialog(
                Dialog(
                    id = 0,
                    name = name,
                    service = service,
                    model = model,
                )

            )
        }
    }

    fun deleteDialog(dialog: Dialog) {
        viewModelScope.launch { repository.delete(dialog) }
    }
}

data class Dialog(
    val id: Int,
    val name: String,
    val service: String,
    val model: String
)