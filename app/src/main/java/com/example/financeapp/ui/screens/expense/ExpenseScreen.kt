package com.example.financeapp.ui.screens.expense

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
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.EditNote
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ElevatedCard
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.OutlinedTextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
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
import com.example.financeapp.data.model.Expense
import com.example.financeapp.ui.components.ScreenContainer

@Composable
fun ExpenseScreen(
    viewModel: ExpenseViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val snackbarHostState = rememberSnackbarHostState()
    val focusManager = LocalFocusManager.current

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
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                ScreenTitleWithIcon(
                    title = "Expense Module",
                    subtitle = "Record spending before you forget it",
                    icon = Icons.Default.ReceiptLong
                )

                AddExpenseCard(
                    uiState = uiState,
                    viewModel = viewModel,
                    onSaveClick = {
                        focusManager.clearFocus()
                        viewModel.onSaveExpenseClick()
                    }
                )

                ExpenseSummaryCard(
                    expenseList = uiState.expenseList
                )

                SectionTitle(text = "Saved Expense Records")

                if (uiState.isLoading) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center
                    ) {
                        CircularProgressIndicator()
                    }
                } else if (uiState.expenseList.isEmpty()) {
                    EmptyExpenseCard()
                } else {
                    uiState.expenseList.forEach { expense ->
                        ExpenseRecordCard(
                            expense = expense,
                            onDeleteClick = {
                                viewModel.deleteExpense(expense.expenseId)
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
private fun rememberSnackbarHostState(): SnackbarHostState {
    return androidx.compose.runtime.remember { SnackbarHostState() }
}

@Composable
private fun AddExpenseCard(
    uiState: ExpenseUiState,
    viewModel: ExpenseViewModel,
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
                modifier = Modifier.fillMaxWidth(),
                singleLine = true,
                isError = uiState.amountError,
                supportingText = {
                    if (uiState.amountError) {
                        Text("Please enter a valid amount")
                    } else {
                        Text("Enter the expense amount")
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

            OutlinedTextField(
                value = uiState.note,
                onValueChange = viewModel::onNoteChange,
                label = { Text("Note") },
                placeholder = { Text("Example: Coffee with friends") },
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
private fun ExpenseSummaryCard(
    expenseList: List<Expense>
) {
    val totalExpenses = expenseList.sumOf { expense -> expense.amount }
    val committedTotal = expenseList
        .filter { expense -> expense.expenseType == "Committed" }
        .sumOf { expense -> expense.amount }
    val discretionaryTotal = expenseList
        .filter { expense -> expense.expenseType == "Discretionary" }
        .sumOf { expense -> expense.amount }

    ElevatedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Expense Summary",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Total recorded expenses: LKR ${"%.2f".format(totalExpenses)}",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = "Committed: LKR ${"%.2f".format(committedTotal)}",
                fontSize = 14.sp
            )

            Text(
                text = "Discretionary: LKR ${"%.2f".format(discretionaryTotal)}",
                fontSize = 14.sp
            )

            Text(
                text = "Records: ${expenseList.size}",
                fontSize = 14.sp
            )
        }
    }
}

@Composable
private fun EmptyExpenseCard() {
    ElevatedCard(
        modifier = Modifier.fillMaxWidth()
    ) {
        Text(
            text = "No expense records yet. Add food, transport, rent, subscriptions, or other spending to understand where your money goes.",
            modifier = Modifier.padding(16.dp),
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun ExpenseRecordCard(
    expense: Expense,
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
                    text = expense.categoryName,
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold
                )

                Text(
                    text = "${expense.paymentMethod} • ${expense.expenseType}",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )

                if (expense.note.isNotBlank()) {
                    Text(
                        text = expense.note,
                        fontSize = 13.sp
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "LKR ${"%.2f".format(expense.amount)}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold
                )

                IconButton(
                    onClick = onDeleteClick
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete expense"
                    )
                }
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