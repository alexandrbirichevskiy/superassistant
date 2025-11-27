package com.example.superassistant.yandexgpt.presentation.models

import androidx.compose.ui.text.AnnotatedString

data class ChatMessageUi(
    val text: AnnotatedString,
    val isUser: Boolean,
)