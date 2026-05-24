package com.example.financeapp.ui.screens.goal

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.financeapp.ui.components.FinanceDatePickerField
import com.example.financeapp.ui.components.ScreenContainer

@OptIn(ExperimentalFoundationApi::class, ExperimentalMaterial3Api::class)
@Composable
fun GoalFormScreen(
    onGoalSaved: () -> Unit = {},
    viewModel: GoalViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val goalTitleRequester = remember { BringIntoViewRequester() }
    val targetAmountRequester = remember { BringIntoViewRequester() }
    val currentSavedAmountRequester = remember { BringIntoViewRequester() }
    val deadlineRequester = remember { BringIntoViewRequester() }

    val snackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(
        uiState.shouldScrollToGoalTitle,
        uiState.shouldScrollToTargetAmount,
        uiState.shouldScrollToCurrentSavedAmount,
        uiState.shouldScrollToDeadline
    ) {
        when {
            uiState.shouldScrollToGoalTitle -> {
                goalTitleRequester.bringIntoView()
                viewModel.resetScrollState()
            }

            uiState.shouldScrollToTargetAmount -> {
                targetAmountRequester.bringIntoView()
                viewModel.resetScrollState()
            }

            uiState.shouldScrollToCurrentSavedAmount -> {
                currentSavedAmountRequester.bringIntoView()
                viewModel.resetScrollState()
            }

            uiState.shouldScrollToDeadline -> {
                deadlineRequester.bringIntoView()
                viewModel.resetScrollState()
            }
        }
    }

    LaunchedEffect(uiState.isSavedSuccessfully) {
        if (uiState.isSavedSuccessfully) {
            snackbarHostState.showSnackbar(uiState.successMessage)
            viewModel.resetSuccessMessage()
            onGoalSaved()
        }
    }

    Scaffold(
        snackbarHost = {
            SnackbarHost(
                hostState = snackbarHostState,
                modifier = Modifier.imePadding()
            )
        }
    ) { innerPadding ->
        ScreenContainer(
            maxWidth = 560.dp
        ) {
            Column(
                modifier = Modifier.padding(innerPadding)
            ) {
                Text(
                    text = "Create Goal",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Create a savings goal and track your progress",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(20.dp))

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        OutlinedTextField(
                            value = uiState.goalTitle,
                            onValueChange = viewModel::onGoalTitleChange,
                            label = { Text("Goal Title") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Title,
                                    contentDescription = null
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .bringIntoViewRequester(goalTitleRequester),
                            singleLine = true,
                            isError = uiState.goalTitleError,
                            supportingText = {
                                if (uiState.goalTitleError) {
                                    Text("Goal title cannot be empty")
                                }
                            }
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = uiState.targetAmount,
                            onValueChange = viewModel::onTargetAmountChange,
                            label = { Text("Target Amount") },
                            prefix = { Text("LKR ") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.Payments,
                                    contentDescription = null
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .bringIntoViewRequester(targetAmountRequester),
                            singleLine = true,
                            isError = uiState.targetAmountError,
                            supportingText = {
                                if (uiState.targetAmountError) {
                                    Text("Please enter a valid target amount")
                                }
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal
                            )
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = uiState.currentSavedAmount,
                            onValueChange = viewModel::onCurrentSavedAmountChange,
                            label = { Text("Current Saved Amount") },
                            prefix = { Text("LKR ") },
                            leadingIcon = {
                                Icon(
                                    imageVector = Icons.Default.AccountBalanceWallet,
                                    contentDescription = null
                                )
                            },
                            modifier = Modifier
                                .fillMaxWidth()
                                .bringIntoViewRequester(currentSavedAmountRequester),
                            singleLine = true,
                            isError = uiState.currentSavedAmountError,
                            supportingText = {
                                if (uiState.currentSavedAmountError) {
                                    Text("Please enter a valid saved amount")
                                }
                            },
                            keyboardOptions = KeyboardOptions(
                                keyboardType = KeyboardType.Decimal
                            )
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        FinanceDatePickerField(
                            value = uiState.deadline,
                            label = "Deadline",
                            placeholder = "Example: 2027-05-10",
                            isError = uiState.deadlineError,
                            errorMessage = "Deadline cannot be empty",
                            onDateSelected = viewModel::onDeadlineChange,
                            modifier = Modifier.bringIntoViewRequester(deadlineRequester)
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        Text(
                            text = "Goal Status",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        GoalStatusChips(
                            selectedStatus = uiState.selectedGoalStatus,
                            onStatusSelected = viewModel::onGoalStatusChange
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = {
                                focusManager.clearFocus()
                                viewModel.saveGoal()
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Save,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )

                            Spacer(modifier = Modifier.size(8.dp))

                            Text(
                                text = "Save Goal",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedButton(
                            onClick = {
                                focusManager.clearFocus()
                                viewModel.onGoalTitleChange("")
                                viewModel.onTargetAmountChange("")
                                viewModel.onCurrentSavedAmountChange("")
                                viewModel.onDeadlineChange("")
                                viewModel.onGoalStatusChange("Active")
                            },
                            modifier = Modifier.fillMaxWidth()
                        ) {
                            Icon(
                                imageVector = Icons.Default.Refresh,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )

                            Spacer(modifier = Modifier.size(8.dp))

                            Text(
                                text = "Reset Form",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(24.dp))
            }
        }
    }
}

@Composable
private fun GoalStatusChips(
    selectedStatus: String,
    onStatusSelected: (String) -> Unit
) {
    Column {
        FilterChip(
            selected = selectedStatus == "Active",
            onClick = {
                onStatusSelected("Active")
            },
            label = {
                Text("Active")
            }
        )

        Spacer(modifier = Modifier.height(8.dp))

        FilterChip(
            selected = selectedStatus == "Paused",
            onClick = {
                onStatusSelected("Paused")
            },
            label = {
                Text("Paused")
            }
        )
    }
}