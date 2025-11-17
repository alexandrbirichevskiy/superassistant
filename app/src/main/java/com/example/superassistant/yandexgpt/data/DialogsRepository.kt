package com.example.superassistant.yandexgpt.data

import android.util.Log
import com.example.superassistant.yandexgpt.data.database.DialogsDao
import com.example.superassistant.yandexgpt.data.database.dbo.DialogDBO
import com.example.superassistant.yandexgpt.presentation.Dialog
import kotlinx.coroutines.flow.map

class DialogsRepository(
    private val dao: DialogsDao
) {

    fun getDialogs() = dao.getAll().map { it.map { dbo -> dbo.toModel() } }

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
}