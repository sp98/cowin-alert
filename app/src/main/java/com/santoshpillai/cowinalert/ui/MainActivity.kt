package com.santoshpillai.cowinalert.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.navArgument
import androidx.navigation.compose.rememberNavController
import com.santoshpillai.cowinalert.data.local.AlertDatabase
import com.santoshpillai.cowinalert.data.model.Result
import com.santoshpillai.cowinalert.ui.alertscreen.AlertScreen
import com.santoshpillai.cowinalert.ui.alertscreen.AlertViewModel
import com.santoshpillai.cowinalert.ui.alertscreen.AlertViewModelFactory
import com.santoshpillai.cowinalert.ui.createscreen.CreateAlertScreen
import com.santoshpillai.cowinalert.ui.createscreen.CreateAlertViewModel
import com.santoshpillai.cowinalert.ui.createscreen.CreateAlertViewModelFactory
import com.santoshpillai.cowinalert.ui.resultscreen.ResultScreen

class MainActivity : ComponentActivity() {
    @ExperimentalComposeUiApi
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
                    ResultScreen(
                        alertName = alertName,
                        results = results,
                        onCancel = {
                            navController.navigate("home") {
                                popUpTo("home") { inclusive = true }
                            }
                        },

                        )
                }
            }
        }
    }
}
