package com.example.superassistant.yandexgpt.data.database

import androidx.room.Database
import androidx.room.RoomDatabase

import androidx.room.TypeConverters

@Database(
    entities = [RequestDBO::class],
    version = 1,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class ChatDatabase : RoomDatabase() {
    abstract fun requestDao(): ChatDao
}

