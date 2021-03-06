package com.santoshpillai.cowinalert.ui.resultscreen

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Share
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.app.ShareCompat
import androidx.core.content.ContextCompat.startActivity
import com.santoshpillai.cowinalert.R
import com.santoshpillai.cowinalert.data.model.Result
import com.santoshpillai.cowinalert.ui.common.CowinDivider
import com.santoshpillai.cowinalert.ui.common.InsetAwareTopAppBar
import com.santoshpillai.cowinalert.ui.theme.CowinAlertTheme

@Composable
fun ResultScreen(
    alertName: String?,
    results: List<Result>,
    onCancel: () -> Unit,
) {
    val context = LocalContext.current
    val alertName = alertName?.take(20)
    CowinAlertTheme() {
        Scaffold(
            topBar = {
                InsetAwareTopAppBar(
                    title = {
                        Text(
                            text = "Results: $alertName",
                            textAlign = TextAlign.Center,
                        )
                    },
                    elevation = 12.dp,
                    navigationIcon = {
                        IconButton(
                            onClick = onCancel
                        ) {
                            Icon(Icons.Filled.ArrowBack, stringResource(R.string.back))
                        }
                    }
                )
            },
            floatingActionButton = {
                if (results.isNotEmpty()) {
                    Register(context)
                }
            }
        ) {

            if (results.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        "No results found",
                        style = MaterialTheme.typography.h5,
                        modifier = Modifier.padding(20.dp)
                    )
                }


            } else {
                LazyColumn() {

                    items(items = results) { result ->
                        Card(
                            modifier = Modifier
                                .padding(10.dp)
                                .fillMaxWidth(),
                            border = BorderStroke(1.dp, Color.LightGray),
                            shape = MaterialTheme.shapes.small,
                        ) {
                            Column(
                                modifier = Modifier
                                    .padding(10.dp)
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                ) {
                                    Text(
                                        text = "${result.triggeredOn}",
                                        style = MaterialTheme.typography.h6
                                    )
                                    IconButton(onClick = { shareData(context, result) }) {
                                        Icon(Icons.Filled.Share, stringResource(R.string.share))
                                    }

                                }
                                Text(
                                    text = "Hospital: ${result.hospitalName}",
                                    style = MaterialTheme.typography.body1
                                )
                                Text(
                                    text = "Vaccination Date: ${result.availableOn}",
                                    style = MaterialTheme.typography.body1
                                )

                                CowinDivider()
                                Text(
                                    text = "Address: ${result.address}",
                                    style = MaterialTheme.typography.overline
                                )

                                Text(
                                    text = "Vaccine : ${result.vaccine}",
                                    style = MaterialTheme.typography.overline
                                )

                                Text(
                                    text = "Age Group: ${result.ageGroup}+",
                                    style = MaterialTheme.typography.overline
                                )

                                Text(
                                    text = "Fee Type: ${result.feeType}",
                                    style = MaterialTheme.typography.overline
                                )

                                Text(
                                    text = "Available Capacity: ${result.availableCapacity}",
                                    style = MaterialTheme.typography.overline
                                )

                                CowinDivider()
                                Spacer(modifier = Modifier.height(4.dp))
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    val d1backgroundColor =
                                        getAvailableDoseBgColor(result.dose1Capacity)
                                    val d2backgroundColor =
                                        getAvailableDoseBgColor(result.dose2Capacity)

                                    Text(
                                        text = "Dose 1 Capacity: ${result.dose1Capacity}",
                                        style = MaterialTheme.typography.body2,
                                        modifier = Modifier
                                            .background(d1backgroundColor)
                                    )

                                    Text(
                                        text = "Dose 2 Capacity: ${result.dose2Capacity}",
                                        style = MaterialTheme.typography.body2,
                                        modifier = Modifier
                                            .background(d2backgroundColor)
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

@Composable
fun Register(context: Context) {
    val cowinLoginURL = stringResource(R.string.cowin_registration_url)
    val intent = remember {
        Intent(
            Intent.ACTION_VIEW,
            Uri.parse(cowinLoginURL)
        )
    }
    FloatingActionButton(
        onClick = { context.startActivity(intent) },
        shape = RoundedCornerShape(20.dp)
    ) {
        Text(
            stringResource(R.string.register),
            modifier = Modifier.padding(horizontal = 10.dp),
            style = MaterialTheme.typography.body2
        )
    }
}


fun getAvailableDoseBgColor(capacity: Int): Color {
    return when (capacity) {
        in 1..10 -> Color.Yellow.copy(alpha = 0.5f)
        0 -> Color.Red.copy(alpha = 0.3f)
        else -> Color.Green.copy(alpha = 0.3f)
    }
}

@Preview(name = "Result Detail Screen")
@Composable
fun PreviewResultDetails() {
    val alertID: Long = 0
    val resultMap = mapOf(
        alertID to listOf<Result>(
            Result(
                resultID = 1,
                alertID = alertID,
                hospitalName = "test hospital",
                address = "2c/46",
                stateName = "Haryana",
                districtName = "faridabad",
                blockName = "NIT-2",
                vaccine = "Covishield",
                feeType = "paid",
                availableCapacity = 9,
                dose1Capacity = 9,
                dose2Capacity = 0,
                triggeredOn = "08-06-2020 08:45"
            ),
            Result(
                resultID = 2,
                alertID = alertID,
                hospitalName = "test hospital",
                address = "2c/46",
                stateName = "Haryana",
                districtName = "faridabad",
                blockName = "NIT-2",
                vaccine = "covaxin",
                feeType = "paid",
                availableCapacity = 0,
                dose1Capacity = 0,
                dose2Capacity = 0,
                triggeredOn = "08-06-2020 08:45"
            ),
            Result(
                alertID = alertID,
                resultID = 3,
                hospitalName = "test hospital",
                address = "2c/46",
                stateName = "Haryana",
                districtName = "faridabad",
                blockName = "NIT-2",
                vaccine = "covaxin",
                feeType = "paid",
                dose1Capacity = 10,
                availableCapacity = 20,
                dose2Capacity = 20,
                triggeredOn = "08-06-2020 08:45"
            ),
            Result(
                alertID = alertID,
                resultID = 18,
                hospitalName = "test hospital",
                address = "2c/46",
                stateName = "Haryana",
                districtName = "faridabad",
                blockName = "NIT-2",
                feeType = "paid",
                availableCapacity = 20,
                dose1Capacity = 10,
                dose2Capacity = 20,
                triggeredOn = "08-06-2020 08:45"
            ),
            Result(
                alertID = alertID,
                resultID = 4,
                hospitalName = "test hospital",
                address = "2c/46",
                stateName = "Haryana",
                districtName = "faridabad",
                blockName = "NIT-2",
                feeType = "paid",
                dose1Capacity = 10,
                availableCapacity = 20,
                dose2Capacity = 20,
                triggeredOn = "08-06-2020 08:45"
            ),
            Result(
                alertID = alertID,
                resultID = 5,
                hospitalName = "test hospital",
                address = "2c/46",
                stateName = "Haryana",
                districtName = "faridabad",
                blockName = "NIT-2",
                feeType = "paid",
                dose1Capacity = 10,
                availableCapacity = 20,
                dose2Capacity = 20,
                triggeredOn = "08-06-2020 08:45"
            ),
            Result(
                alertID = alertID,
                resultID = 9,
                hospitalName = "test hospital",
                address = "2c/46",
                stateName = "Haryana",
                districtName = "faridabad",
                blockName = "NIT-2",
                feeType = "paid",
                availableCapacity = 20,
                dose1Capacity = 10,
                dose2Capacity = 20,
                triggeredOn = "08-06-2020 08:45"
            ),
        )
    )


    resultMap[0]?.get(0)?.let {
        ResultScreen(alertName = "my alert 1", results = resultMap[0]!!) {}
    }
}

private fun getShareIntent(context: Context, result: Result): Intent {
    return ShareCompat.IntentBuilder.from(context as Activity)
        .setText(
            "Vaccination Available:\n" +
                    "Hospital Name:  ${result.hospitalName}\n" +
                    "Address: ${result.address}\n" +
                    "Vaccine: ${result.vaccine}\n" +
                    "Age Group:  ${result.ageGroup}+\n" +
                    "Date:  ${result.availableOn}\n" +
                    "Total Doses:  ${result.availableCapacity}\n" +
                    "Dose 1:  ${result.dose1Capacity}\n" +
                    "Dose 2:  ${result.dose2Capacity}\n\n" +
                    "Register: https://selfregistration.cowin.gov.in/"
        )
        .setType("text/plain")
        .intent

}

private fun shareData(context: Context, result: Result) {
    startActivity(context, getShareIntent(context, result), null)
}

