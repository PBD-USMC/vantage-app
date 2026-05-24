package com.example.financeapp.ui.screens.income

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.TrendingUp
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
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
import com.example.financeapp.data.model.Income
import com.example.financeapp.ui.components.FinanceDatePickerField
import com.example.financeapp.ui.components.ScreenContainer
import java.text.SimpleDateFormat
import java.util.Locale

@Composable
fun IncomeScreen(
    viewModel: IncomeViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }
    val focusManager = LocalFocusManager.current

    LaunchedEffect(uiState.isSavedSuccessfully) {
        if (uiState.isSavedSuccessfully) {
            snackbarHostState.showSnackbar("Income saved successfully")
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
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ScreenTitleWithIcon()

                AddIncomeCard(
                    uiState = uiState,
                    viewModel = viewModel,
                    onSaveClick = {
                        focusManager.clearFocus()
                        viewModel.onSaveIncomeClick()
                    }
                )

                IncomeSummaryCard(
                    incomeList = uiState.incomeList
                )

                SectionTitle(text = "Saved Income Records")

                if (uiState.isLoading) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else if (uiState.incomeList.isEmpty()) {
                    EmptyIncomeCard()
                } else {
                    uiState.incomeList.forEach { income ->
                        IncomeRecordCard(
                            income = income,
                            onDeleteClick = {
                                viewModel.deleteIncome(income.incomeId)
                            }
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))
            }
        }
    }
}

@Composable
private fun AddIncomeCard(
    uiState: IncomeUiState,
    viewModel: IncomeViewModel,
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
                value = uiState.amountReceived,
                onValueChange = viewModel::onAmountReceivedChange,
                label = { Text("Amount Received") },
                placeholder = { Text("Example: 135000") },
                prefix = { Text("LKR ") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Payments,
                        contentDescription = null
                    )
                },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = uiState.amountReceivedError,
                supportingText = {
                    if (uiState.amountReceivedError) {
                        Text("Please enter a valid amount")
                    } else {
                        Text("Enter the final amount received for analysis")
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            SectionTitle(text = "Income Source")

            Spacer(modifier = Modifier.height(8.dp))

            ChipRows(
                items = viewModel.incomeSources,
                selectedItem = uiState.selectedIncomeSource,
                selectedColor = MaterialTheme.colorScheme.primary,
                onItemSelected = viewModel::onIncomeSourceSelected
            )

            Spacer(modifier = Modifier.height(16.dp))

            SectionTitle(text = "Income Type")

            Spacer(modifier = Modifier.height(8.dp))

            ChipRows(
                items = viewModel.incomeTypes,
                selectedItem = uiState.selectedIncomeType,
                selectedColor = if (uiState.selectedIncomeType.contains("Loss")) {
                    MaterialTheme.colorScheme.error
                } else {
                    MaterialTheme.colorScheme.primary
                },
                onItemSelected = viewModel::onIncomeTypeSelected
            )

            Spacer(modifier = Modifier.height(16.dp))

            FinanceDatePickerField(
                value = uiState.dateReceived,
                label = "Date Received",
                placeholder = "Select income received date",
                onDateSelected = viewModel::onDateReceivedChange
            )

            Spacer(modifier = Modifier.height(16.dp))

            CurrencyDropdownField(
                selectedCurrency = uiState.currency,
                currencies = viewModel.currencies,
                label = "Recorded Currency",
                onCurrencySelected = viewModel::onCurrencyChange
            )

            Spacer(modifier = Modifier.height(16.dp))

            SectionTitle(text = "Foreign Income Details")

            Spacer(modifier = Modifier.height(4.dp))

            Text(
                text = "Optional: use these fields for AdSense, USD payments, or crypto income.",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(12.dp))

            CurrencyDropdownField(
                selectedCurrency = uiState.originalCurrency.ifBlank { "Select original currency" },
                currencies = viewModel.currencies,
                label = "Original Currency",
                onCurrencySelected = viewModel::onOriginalCurrencyChange
            )

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedTextField(
                value = uiState.originalAmount,
                onValueChange = viewModel::onOriginalAmountChange,
                label = { Text("Original Amount") },
                placeholder = { Text("Example: 50") },
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = uiState.originalAmountError,
                supportingText = {
                    if (uiState.originalAmountError) {
                        Text("Please enter a valid original amount")
                    } else {
                        Text("Optional field")
                    }
                },
                keyboardOptions = KeyboardOptions(
                    keyboardType = KeyboardType.Decimal
                )
            )

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = uiState.note,
                onValueChange = viewModel::onNoteChange,
                label = { Text("Note") },
                placeholder = { Text("Example: May salary with bonus") },
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
                    text = if (uiState.isLoading) "Saving..." else "Save Income",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}

@Composable
private fun IncomeSummaryCard(
    incomeList: List<Income>
) {
    val totalIncome = incomeList.sumOf { income -> income.amount }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Income Summary",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Total recorded income: LKR ${"%.2f".format(totalIncome)}",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = "Records: ${incomeList.size}",
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun EmptyIncomeCard() {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "No income records yet. Add salary, freelance, AdSense, or crypto income to understand actual monthly earnings.",
            modifier = Modifier.padding(16.dp),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun IncomeRecordCard(
    income: Income,
    onDeleteClick: () -> Unit
) {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = income.source,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = income.incomeType,
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )

                Text(
                    text = "Date: ${formatIncomeDate(income)}",
                    fontSize = 13.sp
                )

                if (income.note.isNotBlank()) {
                    Text(
                        text = income.note,
                        fontSize = 13.sp
                    )
                }

                if (!income.originalCurrency.isNullOrBlank() && income.originalAmount != null) {
                    Text(
                        text = "Original: ${income.originalAmount} ${income.originalCurrency}",
                        fontSize = 13.sp
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "${income.currency} ${"%.2f".format(income.amount)}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )

                IconButton(
                    onClick = onDeleteClick
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete income"
                    )
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun CurrencyDropdownField(
    selectedCurrency: String,
    currencies: List<String>,
    label: String,
    onCurrencySelected: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val focusManager = LocalFocusManager.current

    ExposedDropdownMenuBox(
        expanded = expanded,
        onExpandedChange = {
            focusManager.clearFocus()
            expanded = !expanded
        },
        modifier = Modifier.fillMaxWidth()
    ) {
        OutlinedTextField(
            value = selectedCurrency,
            onValueChange = {},
            label = { Text(label) },
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = expanded
                )
            },
            modifier = Modifier
                .menuAnchor(
                    type = MenuAnchorType.PrimaryNotEditable,
                    enabled = true
                )
                .fillMaxWidth(),
            singleLine = true
        )

        ExposedDropdownMenu(
            expanded = expanded,
            onDismissRequest = {
                expanded = false
            }
        ) {
            currencies.forEach { currency ->
                DropdownMenuItem(
                    text = {
                        Text(currency)
                    },
                    onClick = {
                        onCurrencySelected(currency)
                        expanded = false
                    }
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
    val rows = items.chunked(2)
    val focusManager = LocalFocusManager.current

    Column(
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        rows.forEach { rowItems ->
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                rowItems.forEach { item ->
                    IncomeFilterChip(
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
    title: String = "Income Module",
    subtitle: String = "Record salary, freelance, AdSense, crypto, and other income sources",
    icon: ImageVector = Icons.AutoMirrored.Filled.TrendingUp
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
private fun IncomeFilterChip(
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

private fun formatIncomeDate(
    income: Income
): String {
    val date = income.date.toDate()
    val formatter = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    return formatter.format(date)
}