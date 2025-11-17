package com.example.superassistant

import android.content.Context
import androidx.room.Room
import com.example.superassistant.yandexgpt.data.database.ChatDatabase

object ChatDatabaseProvider {

    @Volatile
    private var INSTANCE: ChatDatabase? = null

    fun getDatabase(context: Context): ChatDatabase =
        INSTANCE ?: synchronized(this) {
            INSTANCE ?: Room.databaseBuilder(
                context.applicationContext,
                ChatDatabase::class.java,
                "app_ad_db"
            ).build().also { INSTANCE = it }
        }
}
