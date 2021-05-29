package com.example.cowinalert

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import kotlinx.coroutines.*

class AlertViewModel(
    val database: AlertDatabaseDao
) : ViewModel() {

    lateinit var alerts: LiveData<List<Alert>>
    lateinit var result: LiveData<Map<Long, List<Result>>>

    private val viewModelJob = Job()
    private val uiscope = CoroutineScope(Dispatchers.Main + viewModelJob)

    var selectedAlerts: List<Long> by mutableStateOf(listOf())
        private set

    var expandedAlert:Long by mutableStateOf(-1)
        private set

    init {
        initialize()
    }


    private fun initialize() {
        uiscope.launch {
            initializeAlerts()
        }
    }

    fun updateExpandedAlert(id: Long){
        expandedAlert = if (expandedAlert == id) -1 else id
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
            val resultList = database.getAllResults()
            var op: Map<Long, List<Result>> = mapOf()
            val resultMap = Transformations.map(resultList) {
                for (r in it) {
                    if (op.containsKey(r.alertID)) {
                        if (op[r.alertID]?.contains(it) != true) {
                            op[r.alertID]?.toMutableList()?.add(r)
                        }
                    } else {
                        op[r.alertID]?.toMutableList()?.add(r)
                    }
                }
                op
            }

            println(" hello -- $resultMap")
            result = resultMap


        }
    }

    fun deleteAlerts() {
        println("deleting alerts $selectedAlerts")
        uiscope.launch {
            delete()
        }
    }

    private suspend fun delete() {
        withContext(Dispatchers.IO) {
            for (alertID in selectedAlerts) {
                database.deleteAlert(alertID)
                updateSelectedAlerts(alertID)
            }
        }
    }
}