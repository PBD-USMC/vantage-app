package com.example.financeapp.ui.screens.expense

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
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
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
fun ExpenseScreen(
    viewModel: ExpenseViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val amountRequester = remember { BringIntoViewRequester() }
    val snackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(uiState.shouldScrollToAmount) {
        if (uiState.shouldScrollToAmount) {
            amountRequester.bringIntoView()
            viewModel.clearScrollRequests()
        }
    }

    LaunchedEffect(uiState.isSavedSuccessfully) {
        if (uiState.isSavedSuccessfully) {
            snackbarHostState.showSnackbar("Expense saved successfully")
            viewModel.clearSuccessMessage()
        }
    }

    LaunchedEffect(uiState.errorMessage) {
        if (uiState.errorMessage.isNotBlank()) {
            snackbarHostState.showSnackbar(uiState.errorMessage)
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
                modifier = Modifier.padding(innerPadding),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                ScreenTitleWithIcon(
                    title = "Add Expense",
                    subtitle = "Record spending before you forget it",
                    icon = Icons.Default.ReceiptLong
                )

                AddExpenseCard(
                    uiState = uiState,
                    viewModel = viewModel,
                    amountRequester = amountRequester,
                    onSaveClick = {
                        focusManager.clearFocus()
                        viewModel.onSaveExpenseClick()
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalFoundationApi::class)
@Composable
private fun AddExpenseCard(
    uiState: ExpenseUiState,
    viewModel: ExpenseViewModel,
    amountRequester: BringIntoViewRequester,
    onSaveClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            OutlinedTextField(
                value = uiState.amount,
                onValueChange = viewModel::onAmountChange,
                label = { Text("Amount") },
                placeholder = { Text("Example: 8500") },
                prefix = { Text("LKR ") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Payments,
                        contentDescription = null
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .bringIntoViewRequester(amountRequester),
                singleLine = true,
                isError = uiState.amountError,
                supportingText = {
                    if (uiState.amountError) {
                        Text("Please enter a valid amount")
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            SectionTitle(text = "Category")

            Spacer(modifier = Modifier.height(8.dp))

            ChipRows(
                items = viewModel.categories,
                selectedItem = uiState.selectedCategory,
                selectedColor = MaterialTheme.colorScheme.primary,
                onItemSelected = viewModel::onCategorySelected
            )

            Spacer(modifier = Modifier.height(16.dp))

            SectionTitle(text = "Payment Method")

            Spacer(modifier = Modifier.height(8.dp))

            ChipRows(
                items = viewModel.paymentMethods,
                selectedItem = uiState.selectedPaymentMethod,
                selectedColor = MaterialTheme.colorScheme.primary,
                onItemSelected = viewModel::onPaymentMethodSelected
            )

            Spacer(modifier = Modifier.height(16.dp))

            SectionTitle(text = "Expense Type")

            Spacer(modifier = Modifier.height(8.dp))

            ChipRows(
                items = viewModel.expenseTypes,
                selectedItem = uiState.selectedExpenseType,
                selectedColor = if (uiState.selectedExpenseType == "Committed") {
                    MaterialTheme.colorScheme.secondary
                } else {
                    MaterialTheme.colorScheme.primary
                },
                onItemSelected = viewModel::onExpenseTypeSelected
            )

            Spacer(modifier = Modifier.height(16.dp))

            FinanceDatePickerField(
                value = uiState.date,
                label = "Date",
                placeholder = "Select date",
                onDateSelected = viewModel::onDateChange
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.note,
                onValueChange = viewModel::onNoteChange,
                label = { Text("Note") },
                placeholder = { Text("Example: Lunch, transport, or subscription payment") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.EditNote,
                        contentDescription = null
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                minLines = 2,
                maxLines = 4,
                supportingText = {
                    Text("${uiState.note.length}/${uiState.noteLimit}")
                }
            )

            Spacer(modifier = Modifier.height(20.dp))

            Button(
                onClick = onSaveClick,
                modifier = Modifier.fillMaxWidth(),
                enabled = !uiState.isLoading
            ) {
                Icon(
                    imageVector = Icons.Default.Save,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(modifier = Modifier.size(8.dp))

                Text(
                    text = if (uiState.isLoading) "Saving..." else "Save Expense",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun ChipRows(
    items: List<String>,
    selectedItem: String,
    selectedColor: Color,
    onItemSelected: (String) -> Unit
) {
    val rows = items.chunked(3)
    val focusManager = LocalFocusManager.current

    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        rows.forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowItems.forEach { item ->
                    ExpenseFilterChip(
                        text = item,
                        selected = item == selectedItem,
                        selectedColor = selectedColor,
                        onClick = {
                            focusManager.clearFocus()
                            onItemSelected(item)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun ScreenTitleWithIcon(
    title: String,
    subtitle: String,
    icon: ImageVector
) {
    Column {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(30.dp)
            )

            Spacer(modifier = Modifier.size(10.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Spacer(modifier = Modifier.height(4.dp))

        Text(
            text = subtitle,
            fontSize = 16.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun SectionTitle(
    text: String
) {
    Text(
        text = text,
        fontSize = 16.sp,
        fontWeight = FontWeight.Bold
    )
}

@Composable
private fun ExpenseFilterChip(
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