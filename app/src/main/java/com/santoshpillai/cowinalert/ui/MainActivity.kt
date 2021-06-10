package com.santoshpillai.cowinalert.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavType
import androidx.navigation.compose.*
import com.santoshpillai.cowinalert.data.local.AlertDatabase
import com.santoshpillai.cowinalert.ui.alertscreen.AlertScreen
import com.santoshpillai.cowinalert.ui.alertscreen.AlertViewModel
import com.santoshpillai.cowinalert.ui.alertscreen.AlertViewModelFactory
import com.santoshpillai.cowinalert.ui.createscreen.CreateAlertScreen
import com.santoshpillai.cowinalert.ui.createscreen.CreateAlertViewModel
import com.santoshpillai.cowinalert.ui.createscreen.CreateAlertViewModelFactory
import com.santoshpillai.cowinalert.ui.resultscreen.ResultScreen
import com.santoshpillai.cowinalert.data.model.Result
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val application = requireNotNull(this).application
        val dataSource = AlertDatabase.getInstance(application).alertDatabaseDao
        val createAlertViewModelFactory = CreateAlertViewModelFactory(dataSource)
        val createAlertViewModel = ViewModelProvider(
            this,
            createAlertViewModelFactory
        ).get(CreateAlertViewModel::class.java)
        val alertViewModelFactory = AlertViewModelFactory(dataSource)
        val alertViewModel =
            ViewModelProvider(this, alertViewModelFactory).get(AlertViewModel::class.java)

        setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = "home") {
                composable("home") {
                    val alerts = alertViewModel.alerts.observeAsState(listOf()).value
                    val results = alertViewModel.result.observeAsState(mapOf()).value
                    AlertScreen(
                        alerts = alerts,
                        results = results,
                        selectedAlerts = alertViewModel.selectedAlerts,
                        onAlertSelect = alertViewModel::updateSelectedAlerts,
                        onDeleteAlerts = alertViewModel::deleteAlerts,
                        onClearAllSelections = alertViewModel::clearSelectedAlerts,
                        onDisableAlerts = alertViewModel::disableSelectedAlerts,
                        onEnableAlerts = alertViewModel::enableSelectedAlerts,
                        navController = navController
                    )
                }
                composable("createAlert") {
                    val pincodesUsed =
                        createAlertViewModel.pincodesUsed.observeAsState(listOf()).value
                    CreateAlertScreen(
                        viewModel = createAlertViewModel,
                        pincodesUsed = pincodesUsed,
                        navController = navController
                    )
                }

                composable(
                    "results/{alertName}/{alertID}",
                    arguments = listOf(
                        navArgument("alertName") { type = NavType.StringType },
                        navArgument("alertID") { type = NavType.LongType }
                    )
                ) {
                    val alertName = it.arguments?.getString("alertName")
                    val alertID = it.arguments?.getLong("alertID")
                    val results = alertViewModel.result.value?.get(alertID) ?: listOf<Result>()
                    var selectedResult by remember { mutableStateOf(Result()) }
                    ResultScreen(
                        alertName = alertName,
                        results = results,
                        onCancel = { navController.navigate("home") },
                        selectedResult = selectedResult,
                        onSelectResult = { selectedResult = it }

                    )
                }
            }
        }
    }
}