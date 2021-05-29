package com.example.cowinalert

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface AlertDatabaseDao {
    @Insert
    fun insertAlert(alert: Alert)

    @Query("SELECT * FROM cowin_alert_table ORDER BY  alertID DESC")
    fun getAllAlerts(): LiveData<List<Alert>>

    @Query("DELETE FROM cowin_alert_table WHERE alertID = :key")
    fun deleteAlert(key: Long)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertResult(result: Result)

    @Query("SELECT * FROM cowin_result_table")
    fun getAllResults(): LiveData<List<Result>>

    @Query("DELETE FROM cowin_result_table WHERE resultID = :key")
    fun deleteResult(key: Long)
}