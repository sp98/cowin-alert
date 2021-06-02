package com.example.cowinalert

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*

class AlertViewModel(
    val database: AlertDatabaseDao
) : ViewModel() {

    lateinit var alerts: LiveData<List<Alert>>
    lateinit var result: LiveData<Map<Long, List<Result>>>
    lateinit var pincodesUsed: LiveData<List<String>>

    val maxPincodesAllowed = 2

    private val viewModelJob = Job()
    private val uiscope = CoroutineScope(Dispatchers.Main + viewModelJob)

    var selectedAlerts: List<Long> by mutableStateOf(listOf())
        private set

    var expandedAlert: Long by mutableStateOf(-1)
        private set

    init {
        initialize()
    }


    private fun initialize() {
        uiscope.launch {
            initializeAlerts()
        }
    }

    fun updateSelectedAlerts(id: Long) {
        selectedAlerts = if (selectedAlerts.contains(id)) {
            selectedAlerts.toMutableList().also {
                it.remove(id)
            }
        } else {
            selectedAlerts + listOf(id)
        }
    }


    private suspend fun initializeAlerts() {
        withContext(Dispatchers.IO) {
            alerts = database.getAllAlerts()
            val pincodes = database.getUniqueAlerts()
            val resultList = database.getAllResults()
            val resultMap = Transformations.map(resultList) { it ->
                it.groupBy({ it.alertID }, { it })
            }
            val pins = Transformations.map(pincodes) { it ->
                it.map {
                    it.pinCode.toString()
                }
            }
            result = resultMap
            pincodesUsed = pins
        }
    }

    fun deleteAlerts() {
        uiscope.launch {
            delete()
        }
    }

    private suspend fun delete() {
        withContext(Dispatchers.IO) {
            for (alertID in selectedAlerts) {
                database.deleteAlert(alertID)
                database.deleteResult(alertID)
                updateSelectedAlerts(alertID)
            }
        }
    }

    fun dummyResults(id: Long) {
        val r = Result(
            alertID = id,
            // resultID = 1,
            hospitalName = "Mithibai hospital",
            address = "2c/46",
            stateName = "Haryana",
            districtName = "faridabad",
            blockName = "NIT-2",
            feeType = "paid",
            dose1Capacity = 10,
            dose2Capacity = 45,
            availableCapacity = 20,
        )

        insertResult(r)
    }

    private fun insertResult(r: Result) {
        uiscope.launch {
            insertResult2(r)
        }
    }

    private suspend fun insertResult2(r: Result) {
        withContext(Dispatchers.IO) {
            database.insertResult(r)
        }
    }
}