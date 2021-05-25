package com.example.cowinalert

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.*

class AlertViewModel(
    val database: AlertDatabaseDao
) : ViewModel() {

    lateinit var alerts: LiveData<List<Alert>>

    private val viewModelJob = Job()
    private val uiscope = CoroutineScope(Dispatchers.Main + viewModelJob)

    var selectedAlerts: List<Long> by mutableStateOf(listOf())
        private set

    init {
        initialize()
    }

    private fun initialize() {
        uiscope.launch {
            initializeAlerts()
        }
    }

    fun updateSelectedAlerts(id: Long){
        if (selectedAlerts.contains(id)){
            selectedAlerts = selectedAlerts.toMutableList().also {
                it.remove(id)
            }
        } else {
            selectedAlerts = selectedAlerts + listOf(id)
        }
    }


    private suspend fun initializeAlerts(){
        withContext(Dispatchers.IO){
            alerts = database.getAllAlerts()
        }
    }

    fun deleteAlerts(){
        println("deleting alerts $selectedAlerts")
        uiscope.launch{
            delete()
        }
    }

    private suspend fun delete(){
        withContext(Dispatchers.IO){
            for(alertID in selectedAlerts){
                database.delete(alertID)
                updateSelectedAlerts(alertID)
            }
        }
    }
}