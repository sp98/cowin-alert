package com.example.cowinalert

import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.navigate
import com.example.cowinalert.ui.theme.CowinAlertTheme
import com.google.accompanist.insets.navigationBarsPadding
import com.google.accompanist.insets.statusBarsPadding


@Composable
fun AlertScreen(
    alerts: List<Alert>,
    results: Map<Long, List<Result>>,
    maxAllowedPins: Int,
    pincodesUsed: List<String>,
    selectedAlerts: List<Long>,
    onAlertSelect: (Long) -> Unit,
    onClearAllSelections: () -> Unit,
    onDisableAlerts: (List<Long>) -> Unit,
    onEnableAlerts: (List<Long>) -> Unit,
    onDeleteAlerts: () -> Unit,
    navController: NavController
) {

    val context = LocalContext.current
    val isSelected = selectedAlerts.isNotEmpty()
    val title = if (isSelected) "(${selectedAlerts.size}) Selected" else "Cowin Alert"
    var expanded by remember { mutableStateOf(false) }
    CowinAlertTheme() {
        Surface(color = MaterialTheme.colors.background) {
            Scaffold(
                topBar = {
                    InsetAwareTopAppBar(
                        title = {
                            Text(
                                text = title,
                                textAlign = TextAlign.Center
                            )
                        },
                        actions = {
                            if (isSelected) {
                                IconButton(
                                    onClick = onClearAllSelections,
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_back_arrow),
                                        contentDescription = "back"
                                    )
                                }
                                IconButton(
                                    onClick = onDeleteAlerts,
                                ) {
                                    Icon(
                                        painter = painterResource(id = R.drawable.ic_delete),
                                        contentDescription = "delete"
                                    )
                                }

                                Box() {
                                    IconButton(onClick = { expanded = true }) {
                                        Icon(
                                            Icons.Filled.MoreVert,
                                            contentDescription = "overflow menu"
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
                                            Text("disable")
                                        }
                                        DropdownMenuItem(
                                            onClick = {
                                                onEnableAlerts(selectedAlerts)
                                                expanded = false
                                            }) {
                                            Text("enable")
                                        }
                                    }
                                }

                            }
                        }
                    )
                },
                bottomBar = {
                    Button(
                        enabled = selectedAlerts.isEmpty(),
                        onClick = {
                            if (pincodesUsed.size == maxAllowedPins) {
                                val msg = "Maximum $maxAllowedPins pincodes allowed"
                                showToastMsg(context, msg)
                            } else {
                                navController.navigate("CreateAlert")
                            }
                        },
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth()

                    ) {
                        Text("CREATE")
                    }
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
fun MyMenu(
    onEnable: () -> Unit,
    onDisable: () -> Unit,
    onClearAllSelections: () -> Unit,

    ) {
    var expanded by remember { mutableStateOf(false) }



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
            val feeType: String = getFeeType(alert)
            val doseType: String = getDoseType(alert)
            val selectedAlertBackground = if (selectedAlerts.contains(alert.alertID)) {
                MaterialTheme.colors.primary.copy(alpha = 0.12f)
            } else {
                MaterialTheme.colors.background
            }

            Card(
                modifier = Modifier
                    .padding(8.dp),
                shape = MaterialTheme.shapes.medium,
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
                                text = "$totalResults",
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
                        ){
                            Text(text = alert.name, style = MaterialTheme.typography.h4)
                            if (alert.status == "disabled") {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_disable),
                                    contentDescription = "disabled"
                                )
                            }
                        }
                        CowinAlertDivider()
                        Text(
                            text = "Pincode: ${alert.pinCode}",
                            style = MaterialTheme.typography.overline
                        )
                        Text(
                            text = "Vaccines: $vaccines",
                            style = MaterialTheme.typography.overline
                        )
                        Text(
                            text = "Age limit: $ageGroup",
                            style = MaterialTheme.typography.overline
                        )
                        Text(
                            text = "Dose: $doseType",
                            style = MaterialTheme.typography.overline
                        )
                        Text(
                            text = "Fee type: $feeType",
                            style = MaterialTheme.typography.overline
                        )
                    }
                }

            }
        }
    }
}

@Composable
fun CowinAlertDivider() {
    Divider(
        modifier = Modifier.padding(top = 8.dp, bottom = 3.dp),
        color = MaterialTheme.colors.onSurface.copy(alpha = 0.08f)
    )
}

@Composable
fun InsetAwareTopAppBar(
    title: @Composable () -> Unit,
    modifier: Modifier = Modifier,
    navigationIcon: @Composable (() -> Unit)? = null,
    actions: @Composable RowScope.() -> Unit = {},
    backgroundColor: Color = MaterialTheme.colors.primaryVariant,
    contentColor: Color = contentColorFor(backgroundColor),
    elevation: Dp = 4.dp
) {
    Surface(
        color = backgroundColor,
        elevation = elevation,
        modifier = modifier
    ) {
        TopAppBar(
            title = title,
            navigationIcon = navigationIcon,
            actions = actions,
            backgroundColor = Color.Transparent,
            contentColor = contentColor,
            elevation = 0.dp,
            modifier = Modifier
                .statusBarsPadding()
                .navigationBarsPadding(bottom = false)
        )
    }
}

fun showToastMsg(context: Context, msg: String) {
    Toast.makeText(context, msg, Toast.LENGTH_LONG).show()

}

fun getVaccines(alert: Alert): String {
    var vaccines: String = ""
    if (alert.isCovishield && alert.isCovaxin) {
        vaccines = "any"
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

fun getFeeType(alert: Alert): String {
    var feeType: String = ""
    if (alert.paid && alert.free) {
        feeType = "any"
    } else {
        if (alert.free) {
            feeType += "free"
        }
        if (alert.paid) {
            feeType = if (feeType == "") feeType + "paid" else "$feeType,paid"
        }
    }
    return feeType
}

fun getDoseType(alert: Alert): String {
    var doseType: String = ""
    if (alert.dose1 && alert.dose2) {
        doseType = "any"
    } else {
        if (alert.dose1) {
            doseType += "dose1"
        }
        if (alert.dose2) {
            doseType = if (doseType == "") doseType + "dose2" else "$doseType,dose2"
        }
    }
    return doseType
}

fun getAgeGroups(alert: Alert): String {
    var ageGroup: String = ""
    if (alert.above45 && alert.below45) {
        ageGroup = "any"
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
    val selectedAlerts = listOf<Long>(1)

    AlertList(alerts,
        resultMap,
        selectedAlerts,
        {},
        { _: String, _: Long -> }
    )
}

