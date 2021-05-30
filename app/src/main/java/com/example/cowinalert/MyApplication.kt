package com.example.cowinalert

import android.app.Application
import androidx.work.*
import com.jakewharton.threetenabp.AndroidThreeTen
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.util.concurrent.TimeUnit

class MyApplication : Application() {
    private val applicationScope = CoroutineScope(Dispatchers.Default)
    override fun onCreate() {
        super.onCreate()
        AndroidThreeTen.init(this)
        Timber.plant(Timber.DebugTree())
        delayedInit()
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

        val repeatingRequest = OneTimeWorkRequestBuilder<QueryWorker>()
            .setConstraints(constraints)
            .addTag("CowinQuery")
            .setBackoffCriteria(
                BackoffPolicy.LINEAR,
                5,
                TimeUnit.MINUTES
            )
            .build()

        WorkManager.getInstance(applicationContext).enqueue(repeatingRequest)
    }
}