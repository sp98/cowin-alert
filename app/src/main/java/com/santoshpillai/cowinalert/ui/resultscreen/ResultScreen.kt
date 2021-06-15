package com.santoshpillai.cowinalert.ui.resultscreen

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
    selectedResult: Result,
    onSelectResult: (Result) -> Unit
) {
    val context = LocalContext.current
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
                    actions = {
                        if (selectedResult != Result()) {
                            IconButton(onClick = { shareData(context, selectedResult) }) {
                                Icon(
                                    painter = painterResource(id = R.drawable.ic_share),
                                    contentDescription = "share"
                                )
                            }
                        }
                        IconButton(onClick = onCancel) {
                            Icon(
                                painter = painterResource(id = R.drawable.ic_close),
                                contentDescription = "close"
                            )
                        }
                    }
                )
            }
        ) {

            if (results.isEmpty()) {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    verticalArrangement = Arrangement.Center,
                    horizontalAlignment = Alignment.CenterHorizontally
                ){
                    Text(
                        "No results found",
                        style = MaterialTheme.typography.h5,
                        modifier = Modifier.padding(20.dp)
                    )
                }


            } else {
                LazyColumn() {

                    items(items = results) { result ->
                        val selectedAlertBackground = if (selectedResult == result) {
                            MaterialTheme.colors.primary.copy(alpha = 0.12f)
                        } else {
                            MaterialTheme.colors.background
                        }
                        Card(
                            modifier = Modifier
                                .padding(10.dp)
                                .fillMaxWidth()
                                .pointerInput(selectedResult) {
                                    detectTapGestures(
                                        onTap = {
                                            if (selectedResult == result) {
                                                onSelectResult(Result())
                                            } else {
                                                onSelectResult(result)
                                            }

                                        }
                                    )
                                },
                            border = BorderStroke(1.dp, Color.LightGray),
                            shape = MaterialTheme.shapes.medium,
                        ) {
                            Column(
                                modifier = Modifier
                                    .background(selectedAlertBackground)
                                    .padding(10.dp)
                            ) {
                                Text(
                                    text = "Triggered On: ${result.triggeredOn}",
                                    style = MaterialTheme.typography.h6
                                )
                                Text(
                                    text = "Hospital: ${result.hospitalName}",
                                    style = MaterialTheme.typography.body1
                                )
                                Text(
                                    text = "Available on: ${result.availableOn}",
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
                                    text = "Age Group: ${result.ageGroup}",
                                    style = MaterialTheme.typography.overline
                                )

                                Text(
                                    text = "Available Capacity: ${result.availableCapacity}",
                                    style = MaterialTheme.typography.overline
                                )

                                Text(
                                    text = "Fee Type: ${result.feeType}",
                                    style = MaterialTheme.typography.overline
                                )

                                
                                Text(
                                    text = "Dose 1 Capacity: ${result.dose1Capacity}",
                                    style = MaterialTheme.typography.overline
                                )

                                Text(
                                    text = "Dose 2 Capacity: ${result.dose2Capacity}",
                                    style = MaterialTheme.typography.overline
                                )

                            }
                        }
                    }

                }
            }
        }

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
                availableCapacity = 20,
                dose1Capacity = 10,
                dose2Capacity = 20,
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
                availableCapacity = 20,
                dose1Capacity = 10,
                dose2Capacity = 20,
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
        ResultScreen(alertName = "my alert 1", results = resultMap[0]!!, {},
            it, {})
    }
}


private fun getShareIntent(context: Context, result: Result): Intent {
    return ShareCompat.IntentBuilder.from(context as Activity)
        .setText(
            "Vaccination Available:\n" +
                    "Hospital Name:  ${result.hospitalName}\n" +
                    "Address: ${result.address}\n" +
                    "Age Group:  ${result.ageGroup}+\n" +
                    "Date:  ${result.availableOn}\n" +
                    "Total Doses:  ${result.availableCapacity}\n" +
                    "Dose 1:  ${result.dose1Capacity}\n" +
                    "Dose 2:  ${result.dose2Capacity}\n"
        )
        .setType("text/plain")
        .intent

}

private fun shareData(context: Context, result: Result) {
    startActivity(context, getShareIntent(context, result), null)
}
