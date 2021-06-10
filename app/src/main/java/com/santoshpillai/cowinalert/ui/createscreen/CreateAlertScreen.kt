package com.santoshpillai.cowinalert.ui.createscreen

import androidx.compose.foundation.*
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.navigate
import com.santoshpillai.cowinalert.ui.alertscreen.InsetAwareTopAppBar
import com.santoshpillai.cowinalert.ui.alertscreen.showToastMsg
import com.santoshpillai.cowinalert.ui.theme.CowinAlertTheme

@Composable
fun CreateAlertScreen(
    viewModel: CreateAlertViewModel,
    navController: NavController,
    pincodesUsed: List<String>
) {
    val context = LocalContext.current
    val pincodeRegex = Regex("^[1-9]{1}[0-9]{2}[0-9]{3}$")
    val scrollState = rememberScrollState()

    CowinAlertTheme() {
        Surface() {
            Scaffold(
                bottomBar = {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Button(
                            onClick = { navController.navigate("Home") },
                            modifier = Modifier
                                .padding(8.dp)
                        ) {
                            Text(text = "CANCEL")
                        }

                        Button(
                            enabled = viewModel.name.isNotEmpty() && viewModel.pin.isNotEmpty(),
                            onClick = {
                                if (!pincodeRegex.matches(viewModel.pin)) {
                                    showToastMsg(context, "Invalid pincode ${viewModel.pin}")
                                } else if (pincodesUsed.size == viewModel.maxPincodesAllowed
                                    && !pincodesUsed.contains(viewModel.pin)
                                ) {
                                    val msg =
                                        "Maximum ${viewModel.maxPincodesAllowed} pincodes allowed. " +
                                                "Pincodes in use - ${pincodesUsed.joinToString(" and ")} "
                                    showToastMsg(context, msg)
                                } else {
                                    viewModel.onCreate(navController)
                                }
                            },
                            modifier = Modifier
                                .padding(8.dp)
                        ) {
                            Text(text = "SAVE")
                        }
                    }

                },
                topBar = {
                    InsetAwareTopAppBar(
                        title = {
                            Text(
                                text = "Create Alert",
                                textAlign = TextAlign.Center
                            )
                        }
                    )
                }
            ) {
                Box(modifier = Modifier.padding(it)) {

                    Column(
                        Modifier
                            .padding(20.dp)
                            .verticalScroll(scrollState)
                    ) {

                        // alert name text field
                        AlertTextField(
                            value = viewModel.name,
                            onValueChange = viewModel::onNameChange,
                            placeholder = "Alert Name"
                        )

                        Spacer(modifier = Modifier.padding(4.dp))

                        // pin name text field
                        AlertTextField(
                            value = viewModel.pin,
                            onValueChange = viewModel::onPinChange,
                            placeholder = "Pincode"
                        )

                        Spacer(modifier = Modifier.padding(4.dp))

                        Text("Select Vaccine:", style = MaterialTheme.typography.caption)

                        // Covishield checkbox
                        CheckboxComponent(
                            checked = viewModel.isCovishield,
                            onCheckedChange = viewModel::onCovishieldCheck,
                            checkBoxText = "Covishield"
                        )

                        // Covaxin Checkbox
                        CheckboxComponent(
                            checked = viewModel.isCovaxin,
                            onCheckedChange = viewModel::onCovaxinCheck,
                            checkBoxText = "Covaxin"
                        )

                        Spacer(modifier = Modifier.padding(4.dp))

                        Text(text = "Select age group:", style = MaterialTheme.typography.caption)

                        // above 45 checkbox
                        CheckboxComponent(
                            checked = viewModel.isAbove45,
                            onCheckedChange = viewModel::onAbove45check,
                            checkBoxText = "Above 45"
                        )

                        // below 45 checkbox
                        CheckboxComponent(
                            checked = viewModel.isBelow45,
                            onCheckedChange = viewModel::onBelow45Check,
                            checkBoxText = "Below 45"
                        )

                        Spacer(modifier = Modifier.padding(4.dp))

                        Text(text = "Select dose:", style = MaterialTheme.typography.caption)

                        CheckboxComponent(
                            checked = viewModel.isDose1,
                            onCheckedChange = viewModel::onDose1Check,
                            checkBoxText = "Dose 1"
                        )

                        CheckboxComponent(
                            checked = viewModel.isDose2,
                            onCheckedChange = viewModel::onDose2Check,
                            checkBoxText = "Dose 2"
                        )

                        Spacer(modifier = Modifier.padding(4.dp))

                        Text(text = "Select fee type:", style = MaterialTheme.typography.caption)

                        CheckboxComponent(
                            checked = viewModel.isFree,
                            onCheckedChange = viewModel::onFreeCheck,
                            checkBoxText = "Free"
                        )

                        CheckboxComponent(
                            checked = viewModel.isPaid,
                            onCheckedChange = viewModel::onPaidCheck,
                            checkBoxText = "Paid"
                        )

                    }

                }
            }

        }

    }


}


@Composable
fun AlertTextField(value: String, onValueChange: (String) -> Unit, placeholder: String) {
    OutlinedTextField(
        value = value,
        onValueChange = onValueChange,
        label = { Text(placeholder) },
        modifier = Modifier.fillMaxWidth(),
        singleLine = true,
    )
}

@Composable
fun CheckboxComponent(
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
    checkBoxText: String
) {

    val checkboxBorderColor = if (checked) {
        MaterialTheme.colors.primary.copy(alpha = 0.5f)
    } else {
        MaterialTheme.colors.primary.copy(alpha = 0.12f)
    }

    val checkboxBackgroundColor = if (checked) {
        MaterialTheme.colors.primary.copy(alpha = 0.12f)
    } else {
        MaterialTheme.colors.background
    }

    Surface(
        shape = MaterialTheme.shapes.small,
        border = BorderStroke(
            width = 1.dp,
            color = checkboxBorderColor
        ),
        modifier = Modifier
            .padding(vertical = 8.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .background(checkboxBackgroundColor)
                .clickable(
                    onClick = {
                        onCheckedChange(!checked)
                    }
                )
                .padding(vertical = 16.dp, horizontal = 16.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween,
        ) {
            Text(text = checkBoxText)
            Checkbox(
                checked = checked,
                onCheckedChange = onCheckedChange,
                colors = CheckboxDefaults.colors(
                    checkedColor = MaterialTheme.colors.primary
                )
            )
        }
    }
}


@Preview(name = "Create Alert")
@Composable
fun PreviewCreateAlertScreen() {
    //CreateAlertScreen(CreateAlertViewModel())
}