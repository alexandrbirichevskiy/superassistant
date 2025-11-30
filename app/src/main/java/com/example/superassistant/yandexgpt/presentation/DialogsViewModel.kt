package com.example.superassistant.yandexgpt.presentation

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.superassistant.GPTModels
import com.example.superassistant.SuperAssistantRetrofit
import com.example.superassistant.chatgpt.ChatGptRepository
import com.example.superassistant.yandexgpt.data.DialogsRepository
import com.example.superassistant.yandexgpt.data.database.DialogsDao
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class DialogsViewModel(private val dao: DialogsDao) : ViewModel() {

    private val repository by lazy { DialogsRepository(SuperAssistantRetrofit(), dao) }
//    private val repositoryGpt by lazy { ChatGptRepository(SuperAssistantRetrofit(), dao) }

    val dialogs = repository.getDialogs().stateIn(
        viewModelScope,
        SharingStarted.WhileSubscribed(5000),
        emptyList()
    )
    val services = MutableStateFlow(listOf("YandexGPT", "OpenAI"))
    val models = MutableStateFlow<List<String>>(listOf("gpt-4o-mini"))

//    init {
//        viewModelScope.launch {
//            models.update { repositoryGpt.getModels() }
//        }
//    }

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