package com.santoshpillai.cowinalert.workers

import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.work.CoroutineWorker
import androidx.work.WorkerParameters
import com.santoshpillai.cowinalert.R
import com.santoshpillai.cowinalert.data.local.AlertDatabase
import com.santoshpillai.cowinalert.data.model.Alert
import com.santoshpillai.cowinalert.data.model.Center
import com.santoshpillai.cowinalert.data.service.CowinAPI
import com.santoshpillai.cowinalert.ui.MainActivity
import kotlinx.coroutines.coroutineScope
import org.threeten.bp.LocalDateTime
import org.threeten.bp.LocalTime
import org.threeten.bp.format.DateTimeFormatter
import com.santoshpillai.cowinalert.data.model.Result as CowinResult

data class MyResult(val result: List<CowinResult>, val alertNames: List<String>)

class QueryWorker(appContext: Context, workerParams: WorkerParameters) :
    CoroutineWorker(appContext, workerParams) {

    companion object {
        const val WORK_NAME = "QueryCowinAPI"
    }

    override suspend fun doWork(): Result = coroutineScope {
        try {
            Log.i("testing", "starting worker")
            val database = AlertDatabase.getInstance(applicationContext)
            val filters = database.alertDatabaseDao.getEnabledAlertList()

            if (isCorrectTime() && filters.isNotEmpty()) {
                val today = getFormattedTime("dd-MM-yyyy")
                val uniqueAlerts = database.alertDatabaseDao.getUniqueAlertList()
                val pincodes = uniqueAlerts.map {
                    it.pinCode.toString()
                }
                var results: List<CowinResult> = ArrayList()
                var resultAlerts: List<String> = ArrayList()
                for (pincode in pincodes) {
                    val centers =
                        CowinAPI.retrofitService.getCowinData(pincode, today).execute().body()
                    if (centers != null) {
                        println("centers - ${centers.centers}")
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
            Result.success()
        } catch (e: Exception) {
            println("failed with exception ${e.message}")
            Result.retry()
        }

    }


    private fun matchFilter(
        filters: List<Alert>,
        centers: List<Center>
    ): MyResult {
        var results: List<CowinResult> = ArrayList()
        var triggeredAlertNames: List<String> = ArrayList()
        for (center in centers) {
            val sessions = center.sessions
            for (session in sessions) {
                if (session.availableCapacity > 0) {
                    for (filter in filters) {
                        if (filter.pinCode == center.pincode) {
                            val isValidAgeGroup =
                                validAgeLimit(
                                    filter.below45, filter.above45,
                                    session.minAgeLimit
                                )
                            val isValidVaccine =
                                validVaccine(
                                    filter.isCovishield, filter.isCovaxin,
                                    filter.isSuptnikV, session.vaccine
                                )
                            val isValidDose = validDose(
                                filter.dose1,
                                filter.dose2,
                                session.availableCapacityDose1,
                                session.availableCapacityDose2
                            )
                            val isValidFeeType =
                                validFeeType(filter.free, filter.paid, center.feeType)
                            if (isValidAgeGroup && isValidVaccine && isValidDose && isValidFeeType) {
                                val result = CowinResult(
                                    alertID = filter.alertID,
                                    hospitalName = center.name,
                                    address = center.address,
                                    stateName = center.stateName,
                                    districtName = center.districtName,
                                    blockName = center.blockName,
                                    vaccine = session.vaccine,
                                    feeType = center.feeType,
                                    availableCapacity = session.availableCapacity,
                                    dose1Capacity = session.availableCapacityDose1,
                                    dose2Capacity = session.availableCapacityDose2,
                                    triggeredOn = getFormattedTime("dd-MM-yyyy HH:mm"),
                                    availableOn = session.date,
                                    ageGroup = session.minAgeLimit
                                )
                                results += listOf(result)
                                if (!triggeredAlertNames.contains(filter.name)) {
                                    triggeredAlertNames += listOf(filter.name)
                                }
                            }
                        }
                    }
                }
            }
        }
        return MyResult(results, triggeredAlertNames)
    }


    private fun validAgeLimit(isBelow45: Boolean, isAbove45: Boolean, actual: Int): Boolean {
        var isValid = false

        if (isBelow45 && actual == 18) {
            isValid = true
        }

        if (isAbove45 && actual == 45) {
            isValid = true
        }

        return isValid

    }

    private fun validDose(
        isDose1: Boolean,
        isDose2: Boolean,
        dose1Count: Int,
        dose2Count: Int
    ): Boolean {
        var isValid = false

        if (isDose1 && dose1Count > 0) {
            isValid = true
        }

        if (isDose2 && dose2Count > 0) {
            isValid = true
        }

        return isValid
    }

    private fun validVaccine(
        isCovishield: Boolean, isCovaxin: Boolean,
        isSuptnikV: Boolean, actual: String
    ): Boolean {
        var isValid = false

        if (isCovishield && actual.equals("COVISHIELD",true)) {
            isValid = true
        }

        if (isCovaxin && actual.equals("COVAXIN",true)) {
            isValid = true
        }

        if (isSuptnikV && actual.equals("Sputnik V",true)) {
            isValid = true
        }

        return isValid
    }

    private fun validFeeType(isFree: Boolean, isPaid: Boolean, actual: String): Boolean {
        var isValid = false

        if (isFree && actual == "Free") {
            isValid = true
        }

        if (isPaid && actual == "Paid") {
            isValid = true
        }

        return isValid
    }

    private fun isCorrectTime(): Boolean {
        val now = LocalTime.now()
        return now.isAfter(LocalTime.of(6, 0, 0)) && now.isBefore(LocalTime.of(20, 0, 0))
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



