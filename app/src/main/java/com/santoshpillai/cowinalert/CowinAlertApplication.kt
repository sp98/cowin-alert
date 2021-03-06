package com.santoshpillai.cowinalert

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import androidx.work.*
import com.jakewharton.threetenabp.AndroidThreeTen
import com.santoshpillai.cowinalert.workers.QueryWorker
import com.santoshpillai.cowinalert.workers.QueryWorker.Companion.WORK_NAME
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.TimeUnit

class CowinAlertApplication : Application() {
    val channelID = "CowinChannel"
    private val applicationScope = CoroutineScope(Dispatchers.Default)
    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        Timber.plant(Timber.DebugTree())
        createNotificationChannels()
        delayedInit()
    }

    private fun createNotificationChannels() {
        // check if android oreo or higher because notifications channels can not be create below Oreo
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {

            // define notification channel
            val notificationChannel = NotificationChannel(
                channelID,
                "cowin alert",
                NotificationManager.IMPORTANCE_HIGH
            )
            notificationChannel.enableLights(true)
            notificationChannel.enableVibration(true)

            // get reference to the notification manager and create notification channels
            val notificationManager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(notificationChannel)
        }

    }

    private fun delayedInit() {
        applicationScope.launch {
            setupRecurringWork()
        }
    }

    private fun setupRecurringWork() {
        val constraints = Constraints.Builder()
            .setRequiresBatteryNotLow(true)
            .setRequiredNetworkType(NetworkType.CONNECTED)
            .build()

        val periodicRequest = PeriodicWorkRequestBuilder<QueryWorker>(15, TimeUnit.MINUTES)
            .setConstraints(constraints)
            .addTag(WORK_NAME)
            .build()

        WorkManager.getInstance(applicationContext).enqueueUniquePeriodicWork(
            WORK_NAME,
            ExistingPeriodicWorkPolicy.KEEP,
            periodicRequest
        )
    }
}