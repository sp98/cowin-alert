package com.santoshpillai.cowinalert.ui.createscreen

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.LiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import androidx.navigation.NavController
import androidx.navigation.compose.navigate
import com.santoshpillai.cowinalert.data.local.AlertDatabaseDao
import com.santoshpillai.cowinalert.data.model.Alert
import kotlinx.coroutines.*

class CreateAlertViewModel(
    private val database: AlertDatabaseDao
) : ViewModel() {
    // ViewModel Job for coroutines
    private var viewModelJob = Job()
    private val uiscope = CoroutineScope(Dispatchers.Main + viewModelJob)

    override fun onCleared() {
        super.onCleared()
        // cancel all coroutines
        viewModelJob.cancel()
    }


    lateinit var pincodesUsed: LiveData<List<String>>
    val maxPincodesAllowed = 2

    init {
        initialize()
    }

    private fun initialize() {
        uiscope.launch {
            getUsedPins()
        }
    }

    private suspend fun getUsedPins() {
        withContext(Dispatchers.IO) {
            val pincodes = database.getUniqueAlerts()
            val pins = Transformations.map(pincodes) { it ->
                it.map {
                    it.pinCode.toString()
                }
            }
            pincodesUsed = pins
        }
    }


    var name: String by mutableStateOf("")

    var pin: String by mutableStateOf("")

    var isCovishield: Boolean by mutableStateOf(false)

    var isCovaxin: Boolean by mutableStateOf(false)

    var isAbove45: Boolean by mutableStateOf(false)

    var isBelow45: Boolean by mutableStateOf(false)

    var isDose1: Boolean by mutableStateOf(false)

    var isDose2: Boolean by mutableStateOf(false)

    var isPaid: Boolean by mutableStateOf(false)

    var isFree: Boolean by mutableStateOf(false)

    fun onNameChange(newName: String) {
        name = newName
    }

    fun onPinChange(newPin: String) {
        pin = newPin
    }

    fun onCovishieldCheck(covishield: Boolean) {
        isCovishield = covishield
    }

    fun onCovaxinCheck(covaxin: Boolean) {
        isCovaxin = covaxin
    }

    fun onAbove45check(above45: Boolean) {
        isAbove45 = above45
    }

    fun onBelow45Check(below45: Boolean) {
        isBelow45 = below45
    }

    fun onDose1Check(dose1: Boolean) {
        isDose1 = dose1
    }

    fun onDose2Check(dose2: Boolean) {
        isDose2 = dose2
    }

    fun onFreeCheck(free: Boolean) {
        isFree = free
    }

    fun onPaidCheck(paid: Boolean) {
        isPaid = paid
    }

    private fun reset() {
        name = ""
        pin = ""
        isCovishield = false
        isCovaxin = false
        isAbove45 = false
        isBelow45 = false
        isDose1 = false
        isDose2 = false
        isFree = false
        isPaid = false
    }

    fun onCreate(navController: NavController) {
        // create instance of alert data class
        val alert = Alert(
            name = name,
            pinCode = pin.toLong(),
            isCovishield = isCovishield,
            isCovaxin = isCovaxin,
            above45 = isAbove45,
            below45 = isBelow45,
            dose1 = isDose1,
            dose2 = isDose2,
            free = isFree,
            paid = isPaid
        )

        // save alert to database
        onInsert(alert)

        // reset
        reset()

        navController.navigate("Home")
    }


    private fun onInsert(alert: Alert) {
        uiscope.launch {
            insert(alert)
        }
    }

    private suspend fun insert(alert: Alert) {
        withContext(Dispatchers.IO) {
            database.insertAlert(alert)
        }
    }

}