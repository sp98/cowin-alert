package com.example.cowinalert

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
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
    selectedAlerts: List<Long>,
    onAlertSelect: (Long) -> Unit,
    onDeleteAlerts: () -> Unit,
    navController: NavController
) {
    CowinAlertTheme() {
        Surface(color = MaterialTheme.colors.background) {
            Scaffold(
                topBar = {
                    InsetAwareTopAppBar(
                        title = {
                            Text(
                                text = "Cowin Alert",
                                textAlign = TextAlign.Center
                            )
                        },
//                        modifier = Modifier
//                            .fillMaxWidth(),
//                        backgroundColor = MaterialTheme.colors.primary.copy(alpha = 0.9f)
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
                        Text(text = alert.name, style = MaterialTheme.typography.h4)
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
    val selectedAlerts = listOf<Long>(1)

    AlertList(alerts,
        resultMap,
        selectedAlerts,
        {},
        { _: String, _: Long -> }
    )
}

