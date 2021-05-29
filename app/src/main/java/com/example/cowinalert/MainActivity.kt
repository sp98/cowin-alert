package com.example.cowinalert

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.livedata.observeAsState
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController

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
        val alertViewModel = ViewModelProvider(this, alertViewModelFactory).get(AlertViewModel::class.java)

        setContent {
            val navController = rememberNavController()
            NavHost(navController = navController, startDestination = "home"){
                composable("home"){
                    val alerts = alertViewModel.alerts.observeAsState(listOf()).value
                    val results = alertViewModel.result.observeAsState(mapOf()).value
                    AlertScreen(
                        alerts = alerts,
                        results = results,
                        selectedAlerts = alertViewModel.selectedAlerts,
                        onAlertSelect = alertViewModel::updateSelectedAlerts,
                        onDeleteAlerts = alertViewModel::deleteAlerts,
                        navController = navController)
                }
                composable("createAlert"){
                    CreateAlertScreen(viewModel = createAlertViewModel, navController = navController)
                }
            }
        }
    }
}
