package com.example.financeapp.ui.screens.goal

import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Title
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.financeapp.ui.components.FinanceDatePickerField
import com.example.financeapp.ui.components.ScreenContainer

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun GoalFormScreen(
    goalId: String = "",
    onGoalSaved: () -> Unit = {},
    viewModel: GoalViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val isEditingExistingGoal = goalId.isNotBlank()

    val goalTitleRequester = remember { BringIntoViewRequester() }
    val targetAmountRequester = remember { BringIntoViewRequester() }
    val currentSavedAmountRequester = remember { BringIntoViewRequester() }
    val deadlineRequester = remember { BringIntoViewRequester() }

    val snackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(goalId) {
        viewModel.loadGoalForForm(goalId)
    }

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
                    text = if (isEditingExistingGoal) "Update Goal" else "Create Goal",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = if (isEditingExistingGoal) {
                        "Edit, update, or delete this savings goal"
                    } else {
                        "Create a new savings goal and track progress"
                    },
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
                            placeholder = { Text("Example: New laptop") },
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
                            placeholder = { Text("Example: 490000") },
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
                            placeholder = { Text("Example: 11200") },
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
                                    Text("Saved amount must be valid and not exceed target")
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
                            errorMessage = "Deadline cannot be empty or in the past",
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

                        GoalStatusChipRow(
                            selectedStatus = uiState.selectedGoalStatus,
                            onStatusSelected = viewModel::onGoalStatusChange
                        )

                        Spacer(modifier = Modifier.height(20.dp))

                        Button(
                            onClick = {
                                focusManager.clearFocus()
                                viewModel.onSaveGoalClick()
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
                                text = if (isEditingExistingGoal) "Update Goal" else "Save Goal",
                                fontSize = 16.sp,
                                fontWeight = FontWeight.SemiBold
                            )
                        }

                        if (isEditingExistingGoal) {
                            Spacer(modifier = Modifier.height(8.dp))

                            OutlinedButton(
                                onClick = {
                                    focusManager.clearFocus()
                                    viewModel.onDeleteSelectedGoalClick()
                                },
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Delete,
                                    contentDescription = null,
                                    modifier = Modifier.size(18.dp),
                                    tint = Color(0xFFD32F2F)
                                )

                                Spacer(modifier = Modifier.size(8.dp))

                                Text(
                                    text = "Delete Goal",
                                    fontSize = 16.sp,
                                    fontWeight = FontWeight.SemiBold,
                                    color = Color(0xFFD32F2F)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(8.dp))

                        OutlinedButton(
                            onClick = {
                                focusManager.clearFocus()
                                viewModel.onResetClick()
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
private fun GoalStatusChipRow(
    selectedStatus: String,
    onStatusSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        GoalStatusChip(
            text = "Active",
            selected = selectedStatus == "Active",
            selectedColor = MaterialTheme.colorScheme.primary,
            onClick = {
                onStatusSelected("Active")
            }
        )

        GoalStatusChip(
            text = "Pause",
            selected = selectedStatus == "Pause",
            selectedColor = Color(0xFFF9A825),
            onClick = {
                onStatusSelected("Pause")
            }
        )

        GoalStatusChip(
            text = "Completed",
            selected = selectedStatus == "Completed",
            selectedColor = MaterialTheme.colorScheme.secondary,
            onClick = {
                onStatusSelected("Completed")
            }
        )
    }
}

@Composable
private fun GoalStatusChip(
    text: String,
    selected: Boolean,
    selectedColor: Color,
    onClick: () -> Unit
) {
    FilterChip(
        selected = selected,
        onClick = onClick,
        label = {
            Text(
                text = text,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
        },
        colors = FilterChipDefaults.filterChipColors(
            selectedContainerColor = selectedColor,
            selectedLabelColor = Color.White
        )
    )
}