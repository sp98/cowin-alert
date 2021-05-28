package com.example.cowinalert

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface ResultDatabaseDao {
    @Insert
    fun insert(result: Result)

    @Query("SELECT * FROM cowin_result_table")
    fun getResults(): LiveData<List<Result>>

    @Query("DELETE FROM cowin_result_table WHERE resultID = :key")
    fun delete(key: Long)
}