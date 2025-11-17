package com.example.superassistant.yandexgpt.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.example.superassistant.yandexgpt.data.database.dbo.DialogDBO
import kotlinx.coroutines.flow.Flow

@Dao
interface DialogsDao {

    @Query("SELECT * FROM DialogDBO")
    fun getAll(): Flow<List<DialogDBO>>

    @Insert
    suspend fun insert(dialog: DialogDBO)

    @Delete
    suspend fun delete(dialog: DialogDBO)
}