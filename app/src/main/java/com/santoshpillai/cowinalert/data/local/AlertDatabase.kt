package com.santoshpillai.cowinalert.data.local

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.santoshpillai.cowinalert.data.model.Alert
import com.santoshpillai.cowinalert.data.model.Result

@Database(entities = [Alert::class, Result::class], version = 3, exportSchema = false)
abstract class AlertDatabase : RoomDatabase() {

    abstract val alertDatabaseDao: AlertDatabaseDao

    companion object {
        @Volatile
        private var INSTANCE: AlertDatabase? = null

        fun getInstance(context: Context): AlertDatabase {
            synchronized(this) {
                var instance = INSTANCE
                if (instance == null) {
                    instance = Room.databaseBuilder(
                        context.applicationContext,
                        AlertDatabase::class.java,
                        "cowin_alert_database"
                    )
                        .fallbackToDestructiveMigration()
                        .build()
                    INSTANCE = instance
                }
                return instance
            }
        }
    }
}