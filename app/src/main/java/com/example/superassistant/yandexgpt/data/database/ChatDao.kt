package com.example.superassistant.yandexgpt.data.database

import RequestDBO
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface ChatDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertRequest(request: RequestDBO): Long

    @Query("SELECT * FROM request_dbo WHERE id = :id")
    suspend fun getRequestById(id: Long): RequestDBO?

    @Query("SELECT * FROM request_dbo")
    suspend fun getAllRequests(): List<RequestDBO>
}
