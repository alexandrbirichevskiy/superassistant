package com.example.superassistant.yandexgpt.data

import McpWebSocketClient
import android.util.Log
import com.example.superassistant.Keys
import com.example.superassistant.SuperAssistantRetrofit
import com.example.superassistant.yandexgpt.data.database.DialogsDao
import com.example.superassistant.yandexgpt.data.database.dbo.DialogDBO
import com.example.superassistant.yandexgpt.data.network.ServerApi
import com.example.superassistant.yandexgpt.presentation.Dialog
import kotlinx.coroutines.flow.map

class DialogsRepository(
    private val retrofit: SuperAssistantRetrofit,
    private val dao: DialogsDao
) {

    private val api by lazy {
        retrofit.createApi(
            "Api-Key ${Keys.SECURE_KEY}",
            BASE_URL,
            ServerApi::class.java
        )
    }

    fun getDialogs() = dao.getAll().map { it.map { dbo -> dbo.toModel() } }

    fun connect() {
        McpWebSocketClient().connect()
    }

    suspend fun addDialog(dialog: Dialog) {
        dao.insert(dialog.toDBO())
    }

    suspend fun delete(dialog: Dialog) {
        dao.delete(dialog.toDBO())
    }

    private fun Dialog.toDBO() = DialogDBO(
        id = id,
        name = name,
        service = service,
        model = model
    )

    private fun DialogDBO.toModel() = Dialog(
        id = id,
        name = name,
        service = service,
        model = model
    )

    private companion object {
        const val BASE_URL = "https://db8q0hs0b2n17u0evthm.ibiatp37.mcpgw.serverless.yandexcloud.net/"
    }
}