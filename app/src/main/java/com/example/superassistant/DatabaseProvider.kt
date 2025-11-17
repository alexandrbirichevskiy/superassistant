package com.example.superassistant

import android.content.Context
import androidx.room.Room

object DatabaseProvider {

    private var INSTANCE: AppDatabase? = null

    fun getDatabase(context: Context): AppDatabase {
        return INSTANCE ?: Room.databaseBuilder(
            context,
            AppDatabase::class.java,
            "dialogs.db"
        ).build().also { INSTANCE = it }
    }
}
