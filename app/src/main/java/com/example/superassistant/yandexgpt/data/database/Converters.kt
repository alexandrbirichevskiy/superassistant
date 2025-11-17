package com.example.superassistant.yandexgpt.data.database

import CompletionOptionsDBO
import MessageDBO
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {

    private val gson = Gson()

    @TypeConverter
    fun fromMessagesList(value: List<MessageDBO>): String =
        gson.toJson(value)

    @TypeConverter
    fun toMessagesList(value: String): List<MessageDBO> {
        val type = object : TypeToken<List<MessageDBO>>() {}.type
        return gson.fromJson(value, type)
    }

    @TypeConverter
    fun fromCompletionOptions(value: CompletionOptionsDBO): String =
        gson.toJson(value)

    @TypeConverter
    fun toCompletionOptions(value: String): CompletionOptionsDBO {
        val type = object : TypeToken<CompletionOptionsDBO>() {}.type
        return gson.fromJson(value, type)
    }
}
