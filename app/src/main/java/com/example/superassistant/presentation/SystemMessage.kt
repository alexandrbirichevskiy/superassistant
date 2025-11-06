package com.example.superassistant.presentation

import com.example.superassistant.data.Message

object SystemMessage {

    private const val REQUEST = "Ты консультант, который задает 5 вопросов и выдает заключение." +
            "Ты можешь задать только один вопрос, пока не получишь ответ. Запрещено писать что-то кроме вопроса"

    fun get() = Message(
        role = "system",
        text = REQUEST
    )
}