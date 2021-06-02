package com.example.cowinalert

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import kotlinx.coroutines.coroutineScope
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime
import org.threeten.bp.format.DateTimeFormatter

data class MyResult(val result: List<com.example.cowinalert.Result>, val alertNames: List<String>)

class QueryWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    companion object {
        const val WORK_NAME = "QueryCowinAPI"
    }

    override suspend fun doWork(): Result = coroutineScope {
        try {
            val database = AlertDatabase.getInstance(applicationContext)
            val filters = database.alertDatabaseDao.getAlertList()

            if (isCorrectTime() && filters.isNotEmpty()) {
                val today = getFormattedTime("dd-MM-yyyy")
                val uniqueAlerts = database.alertDatabaseDao.getUniqueAlertList()
                val pincodes = uniqueAlerts.map {
                    it.pinCode.toString()
                }
                var results: List<com.example.cowinalert.Result> = ArrayList()
                var resultAlerts: List<String> = ArrayList()
                for (pincode in pincodes) {
                    val centers =
                        CowinAPI.retrofitService.getCowinData(pincode, today).execute().body()
                    if (centers != null) {
                        val test = matchFilter(filters, centers.centers)
                        if (test.result.isNotEmpty()) {
                            results = results + test.result
                        }
                        if (test.alertNames.isNotEmpty()) {
                            resultAlerts = resultAlerts + test.alertNames
                        }
                    }
                }

                if (results.isNotEmpty()) {
                    for (result in results) {
                        database.alertDatabaseDao.insertResult(result)
                    }
                    val msg = "Triggered: ${resultAlerts.joinToString(" ,")}"
                    sendNotification(msg)
                }
            }
            Result.retry()
        } catch (e: Exception) {
            println("failed with exception ${e.message}")
            Result.retry()
        }

    }


    private fun matchFilter(
        filters: List<Alert>,
        centers: List<Center>
    ): MyResult {
        var results: List<com.example.cowinalert.Result> = ArrayList()
        var triggeredAlertNames: List<String> = ArrayList()
        for (center in centers) {
            val sessions = center.sessions
            for (session in sessions) {
                if (session.availableCapacity > 0) {
                    for (filter in filters) {
                        if (filter.pinCode == center.pincode) {
                            // TODO add more matchers.
                            val result = Result(
                                alertID = filter.alertID,
                                hospitalName = center.name,
                                address = center.address,
                                stateName = center.stateName,
                                districtName = center.districtName,
                                blockName = center.blockName,
                                feeType = center.feeType,
                                availableCapacity = session.availableCapacity,
                                dose1Capacity = session.availableCapacityDose1,
                                dose2Capacity = session.availableCapacityDose2,
                                triggeredOn = getFormattedTime("dd-MM-yyyy HH:mm")
                            )
                            results = results + listOf<com.example.cowinalert.Result>(result)
                            triggeredAlertNames = triggeredAlertNames + listOf(filter.name)
                        }
                    }
                }
            }
        }
        return MyResult(results, triggeredAlertNames)
    }

    private fun isCorrectTime(): Boolean {
        val now = LocalTime.now()
        return now.isAfter(LocalTime.of(8, 0, 0)) && now.isBefore(LocalTime.of(18, 0, 0))
    }

    private fun getFormattedTime(pattern: String): String {
        val current = LocalDateTime.now()
        val formatter = DateTimeFormatter.ofPattern(pattern)
        return current.format(formatter)
    }


    private fun sendNotification(content: String) {
        val intent = Intent(applicationContext, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(applicationContext, 0, intent, 0)
        val notificationManager = NotificationManagerCompat.from(applicationContext)
        val notificationBuilder = NotificationCompat.Builder(applicationContext, "CowinChannel")
            .setSmallIcon(R.drawable.icon)
            .setContentTitle("Cowin Alert")
            .setContentText(content)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        notificationManager.notify(1001, notificationBuilder.build())
    }
}



