package com.santoshpillai.cowinalert

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query

@Dao
interface AlertDatabaseDao {
    @Insert
    fun insertAlert(alert: Alert)

    @Query("SELECT * FROM cowin_alert_table ORDER BY  alertID DESC")
    fun getAllAlerts(): LiveData<List<Alert>>

    @Query("SELECT * FROM cowin_alert_table GROUP BY pincode")
    fun getUniqueAlerts(): LiveData<List<Alert>>

    @Query("SELECT * FROM cowin_alert_table WHERE status = 'enabled' GROUP BY pincode")
    fun getUniqueAlertList(): List<Alert>

    @Query("SELECT * FROM cowin_alert_table ORDER BY  alertID DESC")
    fun getAlertList(): List<Alert>

    @Query("SELECT * FROM cowin_alert_table WHERE status = 'enabled' ORDER BY alertID DESC")
    fun getEnabledAlertList(): List<Alert>

    @Query("DELETE FROM cowin_alert_table WHERE alertID = :key")
    fun deleteAlert(key: Long)

    @Query("UPDATE cowin_alert_table SET status = 'disabled' WHERE alertID = :key")
    fun disableAlert(key: Long)

    @Query("UPDATE cowin_alert_table SET status = 'enabled' WHERE alertID = :key")
    fun enableAlert(key: Long)

    @Insert()
    fun insertResult(result: Result)

    @Query("SELECT * FROM cowin_result_table ORDER BY resultID DESC")
    fun getAllResults(): LiveData<List<Result>>

    @Query("DELETE FROM cowin_result_table WHERE alertID = :key")
    fun deleteResult(key: Long)
}