package com.example.cowinalert

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.navigate
import com.example.cowinalert.ui.theme.CowinAlertTheme
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewmodel.compose.viewModel


@Composable
fun AlertScreen(
    alerts: List<Alert>,
    selectedAlerts: List<Long>,
    onAlertSelect: (Long) -> Unit,
    onDeleteAlerts: () -> Unit,
    navController: NavController
) {
    CowinAlertTheme() {
        Surface(color = MaterialTheme.colors.background) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = {
                            Text(
                                text = "Cowin Alert",
                                textAlign = TextAlign.Center
                            )
                        },
                    )
                },
                bottomBar = {
                    if (selectedAlerts.isNotEmpty()) {
                        Button(
                            onClick = onDeleteAlerts,
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth()
                                .background(MaterialTheme.colors.error)

                        ) {
                            Text("DELETE")
                        }
                    } else {
                        Button(
                            onClick = { navController.navigate("CreateAlert") },
                            modifier = Modifier
                                .padding(8.dp)
                                .fillMaxWidth()

                        ) {
                            Text("CREATE")
                        }
                    }
                }

            ) {
                AlertList(
                    alerts,
                    selectedAlerts,
                    onAlertSelect,
                )
            }
        }
    }
}


@Composable
fun AlertList(
    alerts: List<Alert>,
    selectedAlerts: List<Long>,
    onAlertSelect: (Long) -> Unit
) {
    LazyColumn(
        //contentPadding = PaddingValues(top = 10.dp)
    ) {
        items(items = alerts) { alert ->
            val vaccines: String = getVaccines(alert)
            val ageGroup: String = getAgeGroups(alert)
            val selectedAlertBackground = if (selectedAlerts.contains(alert.alertID)) {
                MaterialTheme.colors.primary.copy(alpha = 0.12f)
            } else {
                MaterialTheme.colors.background
            }
            println("Hi $selectedAlertBackground")
            Column(modifier = Modifier
                .selectable(
                    selected = selectedAlerts.contains(alert.alertID),
                    onClick = {
                        onAlertSelect(alert.alertID)
                        println("Hello $selectedAlerts")
                    }
                )
                .background(selectedAlertBackground)) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(10.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = "name: " + alert.name)
                    Text(text = "pin: " + alert.pinCode)
                }

                if (vaccines != "" || ageGroup != "") {
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(10.dp),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(text = "vaccine: $vaccines")
                        Text(text = "age: $ageGroup")
                    }
                }
            }

            Divider()
        }

    }
}


fun getVaccines(alert: Alert): String {
    var vaccines: String = ""
    if (alert.isCovishield && alert.isCovaxin) {
        vaccines = "all"
    } else {
        if (alert.isCovaxin) {
            vaccines += "covaxin"
        }
        if (alert.isCovishield) {
            vaccines = if (vaccines == "") vaccines + "covishield" else "$vaccines,covishield"
        }
    }
    return vaccines

}

fun getAgeGroups(alert: Alert): String {
    var ageGroup: String = ""
    if (alert.above45 && alert.below45) {
        ageGroup = "all"
    } else {
        if (alert.above45) {
            ageGroup += ">45"
        }
        if (alert.below45) {
            ageGroup = if (ageGroup == "") "$ageGroup<45" else "$ageGroup,<45"
        }
    }
    return ageGroup
}


@Preview(name = "Alert List")
@Composable
fun PreviewHomeScreen() {
    val alerts = listOf(
        Alert(alertID = 1, name = "alert1", pinCode = "123"),
        Alert(alertID = 2, name = "alert2", pinCode = "123"),
        Alert(alertID = 3, name = "alert3", pinCode = "123"),
    )
    val selectedItems: MutableList<Long> = mutableListOf(1, 2)
    // AlertList(alerts, selectedItems)
}