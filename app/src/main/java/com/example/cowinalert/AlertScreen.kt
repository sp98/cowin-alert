package com.example.cowinalert

import android.content.Context
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


@Composable
fun AlertScreen(
    alerts: List<Alert>,
    results: Map<Long, List<Result>>,
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
                    onAlertSelect,
                    {name, id->
                        println("navigation args $name , $id")
                        navController.navigate("results/$name/$id")}
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
        //contentPadding = PaddingValues(top = 10.dp)
    ) {
        items(items = alerts) { alert ->
            val results = resultMap[alert.alertID]
            val totalResults = results?.let { results.size } ?: 0
            val vaccines: String = getVaccines(alert)
            val ageGroup: String = getAgeGroups(alert)
            val selectedAlertBackground = if (selectedAlerts.contains(alert.alertID)) {
                MaterialTheme.colors.primary.copy(alpha = 0.12f)
            } else {
                MaterialTheme.colors.background
            }

            Card(
                modifier = Modifier
                    .padding(8.dp),
                shape = RoundedCornerShape(10.dp),
                border = BorderStroke(1.dp, Color.LightGray),
                elevation = 5.dp
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
                                            onSeeResults(alert.name, alert.alertID)
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
                            }

                        }

                        Column(
                            modifier = Modifier
                                .weight(2f),
                            verticalArrangement = Arrangement.Center,

                        ) {

                            Text(text = "Name:  ${alert.name}", style = MaterialTheme.typography.h5)
                            Text(text = "Pincode:  ${alert.pinCode}")
                            Text(text = "Vaccines: $vaccines")
                            Text(text = "Age limit: $ageGroup")
                        }
                    }

                }
            }
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
        Alert(alertID = 1, name = "alert1", pinCode = 123),
        Alert(alertID = 2, name = "alert2", pinCode = 123),
        Alert(alertID = 3, name = "alert3", pinCode = 123),
    )

    val resultMap: Map<Long, List<Result>> = mapOf()
    val selectedAlerts  = listOf<Long>(1)

//    AlertList(alerts,
//        resultMap,
//        selectedAlerts,
//        {},
//        {alerts[0].name) -> Unit}
}

