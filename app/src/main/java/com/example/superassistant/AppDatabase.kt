package com.example.superassistant

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.superassistant.yandexgpt.data.database.DialogsDao
import com.example.superassistant.yandexgpt.data.database.dbo.DialogDBO

@Database(entities = [DialogDBO::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun dialogsDao(): DialogsDao
}
