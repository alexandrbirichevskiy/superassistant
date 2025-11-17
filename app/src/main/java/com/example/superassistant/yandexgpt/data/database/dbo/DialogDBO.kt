package com.example.superassistant.yandexgpt.data.database.dbo

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class DialogDBO(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val name: String,
    val service: String,
    val model: String
)
