package com.example.superassistant.presentation

import com.example.superassistant.data.Message

object SystemMessage {

    private const val REQUEST = "Ты консультант," +
            "Ты можешь задать только один вопрос, пока не получишь ответ. Запрещено писать что-то кроме вопроса." +
            "После 5 вопросов ты должен выдать консультацию пользователю"


    fun get() = Message(
        role = "system",
        text = REQUEST
    )
}