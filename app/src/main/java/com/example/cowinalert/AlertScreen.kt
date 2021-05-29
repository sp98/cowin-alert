package com.example.cowinalert

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.ExperimentalAnimationApi
import androidx.compose.animation.slideIn
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import androidx.navigation.compose.navigate
import com.example.cowinalert.ui.theme.CowinAlertTheme
import com.google.android.material.color.MaterialColors


@ExperimentalAnimationApi
@Composable
fun AlertScreen(
    alerts: List<Alert>,
    results: Map<Long, List<Result>>,
    selectedAlerts: List<Long>,
    onAlertSelect: (Long) -> Unit,
    onDeleteAlerts: () -> Unit,
    expandedAlertID: Long,
    onExpandAlertStateChange: (Long) -> Unit,
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
                            onClick = { navController.navigate("CreateAlert")},
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
                    results,
                    selectedAlerts,
                    expandedAlertID,
                    onExpandAlertStateChange,
                    onAlertSelect
                )
            }
        }
    }
}


@ExperimentalAnimationApi
@Composable
fun AlertList(
    alerts: List<Alert>,
    resultMap: Map<Long, List<Result>>,
    selectedAlerts: List<Long>,
    expandedAlertID: Long,
    onExpandAlertStateChange: (Long) -> Unit,
    onAlertSelect: (Long) -> Unit,
) {
    LazyColumn(
        //contentPadding = PaddingValues(top = 10.dp)
    ) {
        items(items = alerts) { alert ->
            val results = resultMap[alert.alertID]
            val expand = expandedAlertID == alert.alertID
            val totalResults = results?.let { results.size } ?: 0
            val vaccines: String = getVaccines(alert)
            val ageGroup: String = getAgeGroups(alert)
            val selectedAlertBackground = if (selectedAlerts.contains(alert.alertID)) {
                MaterialTheme.colors.primary.copy(alpha = 0.12f)
            } else {
                MaterialTheme.colors.background
            }
            val expandArrowRotation = if (expand) 90f else 0f
            val cardElevation = if (expand) 15.dp else 3.dp

            Card(
                modifier = Modifier
                    .padding(8.dp),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, Color.LightGray),
                elevation = cardElevation
            ) {
                Column() {
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
                                            onExpandAlertStateChange(alert.alertID)
                                        }
                                    }
                                )
                            },
                    ) {
                        Column(
                            modifier = Modifier.weight(1f)
                        ) {
                            Row(
                                modifier = Modifier.padding(10.dp),
                            ) {
                                Text(
                                    text = "$totalResults",
                                    fontSize = 40.sp,
                                )
                                CardArrow(expandArrowRotation)
                            }

                        }

                        Column(
                            modifier = Modifier
                                .weight(2f),
                            verticalArrangement = Arrangement.Center,

                        ) {

                            Text(text = "Name:  ${alert.name}", style = MaterialTheme.typography.h5)
                        }
                    }

                    Row(
                        modifier = Modifier
                            .padding(10.dp)
                    ) {
                        AnimatedVisibility(
                            visible = alert.alertID == expandedAlertID,
                        ) {
                            Column(){
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .padding(10.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Text(text = "Pincode:  ${alert.pinCode}")
                                    Text(text = "Vaccines: $vaccines")
                                    Text(text = "Age limit: $ageGroup")
                                }
                                Divider(thickness = 3.dp, )
                                Column() {
                                    if (totalResults > 0) {
                                        ResultList(results!!)
                                    } else {
                                        Text(
                                            "No alerts triggered",
                                            modifier = Modifier.padding(10.dp)
                                        )
                                    }
                                }
                            }

                        }
                    }

                }
            }
        }

    }
}


@Composable
fun ResultList(
    results: List<Result>
) {
    LazyRow() {
        items(items = results) { result ->

            Card(
                modifier = Modifier
                    .padding(8.dp),
                shape = RoundedCornerShape(20.dp),
                border = BorderStroke(2.dp, Color.LightGray),
                elevation = 10.dp
            ) {
                Column(
                    modifier = Modifier
                        .padding(10.dp)
                ) {
                    Text(
                        text = "Hospital: ${result.hospitalName}"
                    )
                    Text(
                        text = "Address: ${result.address}"
                    )
                    Text(
                        text = "Fee Type: ${result.feeType}"
                    )
                    Text(
                        text = "Capacity: ${result.availableCapacity}"
                    )
                    Text(
                        text = "Dose 1: ${result.dose1Capacity}"
                    )
                    Text(
                        text = "Dose 2: ${result.dose2Capacity}"
                    )

                }
            }
        }
    }


}

@Composable
fun CardArrow(
    degree: Float,
) {
    Icon(
        painter = painterResource(id = R.drawable.ic_expand),
        contentDescription = "arrow",
        modifier = Modifier.rotate(degree)
    )
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


@ExperimentalAnimationApi
@Preview(name = "Alert List")
@Composable
fun PreviewHomeScreen() {
    val alerts = listOf(
        Alert(alertID = 1, name = "alert1", pinCode = 123),
        Alert(alertID = 2, name = "alert2", pinCode = 123),
        Alert(alertID = 3, name = "alert3", pinCode = 123),
    )

    val selectedAlerts = listOf<Long>()
    val resultMap = mapOf(
        alerts[0].alertID to listOf<Result>(
            Result(
                alertID = alerts[0].alertID,
                hospitalName = "test hospital",
                address = "2c/46",
                stateName = "Haryana",
                districtName = "faridabad",
                blockName = "NIT-2",
                feeType = "paid",
                availableCapacity = 20,
                dose1Capacity = 10,
                dose2Capacity = 20,
            ),
            Result(
                alertID = alerts[0].alertID,
                hospitalName = "test hospital",
                address = "2c/46",
                stateName = "Haryana",
                districtName = "faridabad",
                blockName = "NIT-2",
                feeType = "paid",
                availableCapacity = 20,
                dose1Capacity = 10,
                dose2Capacity = 20,
            ),
            Result(
                alertID = alerts[0].alertID,
                hospitalName = "test hospital",
                address = "2c/46",
                stateName = "Haryana",
                districtName = "faridabad",
                blockName = "NIT-2",
                feeType = "paid",
                dose1Capacity = 10,
                availableCapacity = 20,
                dose2Capacity = 20,
            ),
            Result(
                alertID = alerts[0].alertID,
                hospitalName = "test hospital",
                address = "2c/46",
                stateName = "Haryana",
                districtName = "faridabad",
                blockName = "NIT-2",
                feeType = "paid",
                availableCapacity = 20,
                dose1Capacity = 10,
                dose2Capacity = 20,
            ),
            Result(
                alertID = alerts[0].alertID,
                hospitalName = "test hospital",
                address = "2c/46",
                stateName = "Haryana",
                districtName = "faridabad",
                blockName = "NIT-2",
                feeType = "paid",
                dose1Capacity = 10,
                availableCapacity = 20,
                dose2Capacity = 20,
            ),
            Result(
                alertID = alerts[0].alertID,
                hospitalName = "test hospital",
                address = "2c/46",
                stateName = "Haryana",
                districtName = "faridabad",
                blockName = "NIT-2",
                feeType = "paid",
                dose1Capacity = 10,
                availableCapacity = 20,
                dose2Capacity = 20,
            ),
            Result(
                alertID = alerts[0].alertID,
                hospitalName = "test hospital",
                address = "2c/46",
                stateName = "Haryana",
                districtName = "faridabad",
                blockName = "NIT-2",
                feeType = "paid",
                availableCapacity = 20,
                dose1Capacity = 10,
                dose2Capacity = 20,
            ),
        )
    )

    AlertList(alerts, resultMap, selectedAlerts, alerts[0].alertID, {}, {})
}

