package com.santoshpillai.cowinalert.ui.alertscreen

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import com.santoshpillai.cowinalert.R
import com.santoshpillai.cowinalert.data.model.Alert
import com.santoshpillai.cowinalert.data.model.Result
import com.santoshpillai.cowinalert.ui.common.CowinDivider
import com.santoshpillai.cowinalert.ui.common.InsetAwareTopAppBar
import com.santoshpillai.cowinalert.ui.theme.CowinAlertTheme


@Composable
fun AlertScreen(
    alerts: List<Alert>,
    results: Map<Long, List<Result>>,
    selectedAlerts: List<Long>,
    onAlertSelect: (Long) -> Unit,
    onClearAllSelections: () -> Unit,
    onDisableAlerts: (List<Long>) -> Unit,
    onEnableAlerts: (List<Long>) -> Unit,
    onDeleteAlerts: () -> Unit,
    navController: NavController
) {

    val isSelected = selectedAlerts.isNotEmpty()
    val title = if (isSelected) "(${selectedAlerts.size}) Selected" else "Cowin Alert"
    val showAddIcon = selectedAlerts.isEmpty()
    var expanded by remember { mutableStateOf(false) }
    CowinAlertTheme() {
        Surface(color = MaterialTheme.colors.background) {
            Scaffold(
                floatingActionButtonPosition = FabPosition.End,
                floatingActionButton = {
                    if (showAddIcon) {
                        FloatingActionButton(
                            modifier = Modifier
                                .padding(5.dp),
                            shape = CircleShape,
                            onClick = {
                                navController.navigate("CreateAlert")
                            },
                            backgroundColor = MaterialTheme.colors.primary,
                            elevation = FloatingActionButtonDefaults.elevation(8.dp),
                        ) {
                            Icon(
                                Icons.Filled.Add,
                                contentDescription = "add"
                            )
                        }
                    }
                },
                topBar = {
                    InsetAwareTopAppBar(
                        title = {
                            Text(
                                text = title,
                                textAlign = TextAlign.Center
                            )
                        },
                        navigationIcon = {
                            if (isSelected) {
                                IconButton(
                                    onClick = onClearAllSelections
                                ) {
                                    Icon(Icons.Filled.ArrowBack, stringResource(R.string.back))
                                }
                            } else {
                                IconButton(
                                    onClick = {},
                                    modifier = Modifier.padding(2.dp)
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.icon),
                                        modifier = Modifier.size(40.dp),
                                        contentDescription = "icon"
                                    )
                                }

                            }
                        },
                        actions = {
                            IconButton(
                                enabled = selectedAlerts.isNotEmpty(),
                                onClick = onDeleteAlerts,
                            ) {
                                Icon(
                                    Icons.Filled.Delete,
                                    contentDescription = stringResource(R.string.delete)
                                )
                            }

                            Box() {
                                IconButton(
                                    enabled = selectedAlerts.isNotEmpty(),
                                    onClick = { expanded = true },
                                ) {
                                    Icon(
                                        Icons.Filled.MoreVert,
                                        contentDescription = stringResource(R.string.overflow_menu)
                                    )
                                }
                                DropdownMenu(
                                    expanded = expanded,
                                    onDismissRequest = { expanded = false })
                                {
                                    DropdownMenuItem(
                                        onClick = {
                                            onDisableAlerts(selectedAlerts)
                                            expanded = false

                                        },
                                    ) {
                                        Text(stringResource(R.string.disable))
                                    }
                                    DropdownMenuItem(
                                        onClick = {
                                            onEnableAlerts(selectedAlerts)
                                            expanded = false
                                        }) {
                                        Text(stringResource(R.string.enable))
                                    }
                                }
                            }
                        }
                    )
                }

            ) {
                AlertList(
                    alerts,
                    results,
                    selectedAlerts,
                    onAlertSelect,
                    { name, id ->
                        navController.navigate("results/$name/$id")
                    }
                )
            }
        }
    }
}


@Composable
fun AlertList(
    alerts: List<Alert>,
    resultMap: Map<Long, List<Result>>,
    selectedAlerts: List<Long>,
    onAlertSelect: (Long) -> Unit,
    onSeeResults: (String, Long) -> Unit,
) {
    LazyColumn(
    ) {
        items(items = alerts) { alert ->
            val results = resultMap[alert.alertID]
            val resultCount = results?.let { results.size } ?: 0
            AlertDetail(
                resultCount = resultCount,
                alert = alert,
                selectedAlerts = selectedAlerts,
                onAlertSelect = onAlertSelect,
                onSeeResults = onSeeResults
            )
        }
    }
}

@Composable
fun AlertDetail(
    resultCount: Int,
    alert: Alert,
    selectedAlerts: List<Long>,
    onAlertSelect: (Long) -> Unit,
    onSeeResults: (String, Long) -> Unit
) {

    val selectedAlertBackground = if (selectedAlerts.contains(alert.alertID)) {
        MaterialTheme.colors.primary.copy(alpha = 0.12f)
    } else {
        MaterialTheme.colors.background
    }

    Card(
        modifier = Modifier
            .padding(8.dp),
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(1.dp, Color.LightGray),
        elevation = 5.dp
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .background(selectedAlertBackground)
                .pointerInput(selectedAlerts) {
                    detectTapGestures(
                        onLongPress = {
                            onAlertSelect(alert.alertID)
                        },
                        onTap = {
                            if (selectedAlerts.isNotEmpty()) {
                                onAlertSelect(alert.alertID)
                            } else {
                                onSeeResults(alert.name, alert.alertID)
                            }
                        }
                    )
                },
        ) {
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.CenterHorizontally

            ) {
                Card(
                    modifier = Modifier
                        .size(80.dp)
                        .padding(5.dp)
                        .fillMaxWidth(),
                    shape = CircleShape,
                ) {
                    Text(
                        text = "$resultCount",
                        style = MaterialTheme.typography.h4,
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(10.dp)
                    )
                }

            }


            Column(
                modifier = Modifier
                    .weight(3f)
                    .padding(10.dp)
                //verticalArrangement = Arrangement.Center,
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Text(text = alert.name.take(20), style = MaterialTheme.typography.h6)
                    if (alert.status == "disabled") {
                        Icon(
                            Icons.Filled.Lock,
                            contentDescription = "disabled"
                        )
                    }
                }

                CowinDivider()

                AlertDetailText("Pincode: ${alert.pinCode}")
                AlertDetailText("Vaccines: ${alert.getVaccines()}")
                AlertDetailText("Age Limit: ${alert.getAgeGroups()}")
                AlertDetailText("Dose: ${alert.getDoseType()}")
                AlertDetailText("Fee Type: ${alert.getFeeType()}")
            }
        }

    }

}

@Composable
fun AlertDetailText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.overline
    )
}


@Preview(name = "Alert List")
@Composable
fun PreviewHomeScreen() {
    val alerts = listOf(
        Alert(alertID = 1, name = "alert1", pinCode = 123),
        Alert(alertID = 2, name = "alert2", pinCode = 123),
        Alert(alertID = 3, name = "alert3", pinCode = 123),
    )

    val resultMap: Map<Long, List<Result>> = mapOf()
    val selectedAlerts = listOf<Long>(1)

    AlertList(alerts,
        resultMap,
        selectedAlerts,
        {},
        { _: String, _: Long -> }
    )
}

