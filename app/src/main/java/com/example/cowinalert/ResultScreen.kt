package com.example.cowinalert

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController

@Composable
fun ResultScreen(
    alertName: String?,
    results: List<Result>,
    onCancel: () -> Unit
){

    Scaffold(
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = "Results: $alertName",
                        textAlign = TextAlign.Center,
                    )
                },
                elevation = 12.dp,
                actions = {
                    IconButton(onClick = onCancel) {
                        Icon(
                            painter = painterResource(id = R.drawable.ic_close) ,
                            contentDescription = "close")
                    }
                }
            )
        }
    ) {

        if (results.isEmpty()){

            Text("No results found",
            modifier = Modifier.padding(20.dp))

        }else {
            LazyColumn() {
                items(items = results) { result ->

                    Card(
                        modifier = Modifier
                            .padding(8.dp)
                            .fillMaxWidth(),
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


    }
}


@Preview(name = "Result Detail Screen")
@Composable
fun PreviewResultDetails(){
    val alertID: Long = 0
    val resultMap = mapOf(
        alertID to listOf<Result>(
            Result(
                alertID = alertID,
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
                alertID = alertID,
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
                alertID = alertID,
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
                alertID = alertID,
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
                alertID = alertID,
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
                alertID = alertID,
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
                alertID = alertID,
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

    ResultScreen(alertName = "my alert 1", results = resultMap[0]!!, {})
}

