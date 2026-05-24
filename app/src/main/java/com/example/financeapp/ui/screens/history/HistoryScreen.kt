package com.example.financeapp.ui.screens.history

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.financeapp.data.model.Expense
import com.example.financeapp.data.model.Income
import com.example.financeapp.ui.components.ScreenContainer
import com.example.financeapp.ui.theme.FinanceGreen
import com.example.financeapp.ui.theme.FinanceRed
import com.example.financeapp.ui.utils.formatTimestampToDate

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadHistory()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    ScreenContainer(
        maxWidth = 560.dp
    ) {
        ScreenTitleWithIcon(
            title = "Transaction History",
            subtitle = "View income and expense records in one place",
            icon = Icons.Default.History
        )

        Spacer(modifier = Modifier.height(20.dp))

        SummaryCard(
            totalIncome = uiState.totalIncome,
            totalExpense = uiState.totalExpense,
            balance = uiState.balance
        )

        Spacer(modifier = Modifier.height(20.dp))

        HistoryFilterChips(
            selectedFilter = uiState.selectedFilter,
            onFilterSelected = viewModel::onFilterChange
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (uiState.message.isNotBlank()) {
            Text(
                text = uiState.message,
                color = MaterialTheme.colorScheme.error,
                fontWeight = FontWeight.Medium
            )

            Spacer(modifier = Modifier.height(12.dp))
        }

        if (uiState.isLoading) {
            Text(
                text = "Loading history...",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        } else {
            val filteredIncomes = viewModel.getFilteredIncomes()
            val filteredExpenses = viewModel.getFilteredExpenses()

            if (filteredIncomes.isEmpty() && filteredExpenses.isEmpty()) {
                EmptyHistoryCard()
            } else {
                if (filteredIncomes.isNotEmpty()) {
                    HistorySectionTitle(
                        title = "Income Records",
                        icon = Icons.Default.Payments
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    filteredIncomes.forEach { income ->
                        IncomeHistoryItem(
                            income = income,
                            onDeleteClick = {
                                viewModel.deleteIncome(income.incomeId)
                            }
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                }

                if (filteredExpenses.isNotEmpty()) {
                    HistorySectionTitle(
                        title = "Expense Records",
                        icon = Icons.Default.ShoppingCart
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    filteredExpenses.forEach { expense ->
                        ExpenseHistoryItem(
                            expense = expense,
                            onDeleteClick = {
                                viewModel.deleteExpense(expense.expenseId)
                            }
                        )

                        Spacer(modifier = Modifier.height(10.dp))
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun SummaryCard(
    totalIncome: Double,
    totalExpense: Double,
    balance: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "Financial Summary",
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(14.dp))

            SummaryRow(
                label = "Total Income",
                value = "LKR ${formatAmount(totalIncome)}",
                valueColor = FinanceGreen
            )

            SummaryRow(
                label = "Total Expense",
                value = "LKR ${formatAmount(totalExpense)}",
                valueColor = FinanceRed
            )

            SummaryRow(
                label = "Balance",
                value = "LKR ${formatAmount(balance)}",
                valueColor = if (balance >= 0) FinanceGreen else FinanceRed
            )
        }
    }
}

@Composable
private fun SummaryRow(
    label: String,
    value: String,
    valueColor: androidx.compose.ui.graphics.Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 7.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 15.sp,
            fontWeight = FontWeight.Medium
        )

        Text(
            text = value,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold,
            color = valueColor
        )
    }
}

@Composable
private fun HistoryFilterChips(
    selectedFilter: String,
    onFilterSelected: (String) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = selectedFilter == "All",
            onClick = {
                onFilterSelected("All")
            },
            label = {
                Text("All")
            }
        )

        FilterChip(
            selected = selectedFilter == "Income",
            onClick = {
                onFilterSelected("Income")
            },
            label = {
                Text("Income")
            }
        )

        FilterChip(
            selected = selectedFilter == "Expense",
            onClick = {
                onFilterSelected("Expense")
            },
            label = {
                Text("Expense")
            }
        )
    }
}

@Composable
private fun IncomeHistoryItem(
    income: Income,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.Payments,
                contentDescription = null,
                tint = FinanceGreen,
                modifier = Modifier.size(28.dp)
            )

            Spacer(modifier = Modifier.size(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = income.source.ifBlank { "Income" },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = "${income.incomeType.ifBlank { "Income" }} • ${formatTimestampToDate(income.date)}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )

                if (income.note.isNotBlank()) {
                    Spacer(modifier = Modifier.height(3.dp))

                    Text(
                        text = income.note,
                        fontSize = 13.sp
                    )
                }
            }

            Column(
                horizontalAlignment = Alignment.End
            ) {
                Text(
                    text = "+ LKR ${formatAmount(income.amount)}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = FinanceGreen
                )

                IconButton(
                    onClick = onDeleteClick
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete income",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
private fun ExpenseHistoryItem(
    expense: Expense,
    onDeleteClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.ShoppingCart,
                contentDescription = null,
                tint = FinanceRed,
                modifier = Modifier.size(28.dp)
            )

            Spacer(modifier = Modifier.size(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = expense.categoryName.ifBlank { "Expense" },
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(3.dp))

                Text(
                    text = "${expense.expenseType.ifBlank { "Expense" }} • ${expense.paymentMethod.ifBlank { "Payment" }} • ${formatTimestampToDate(expense.date)}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )

                if (expense.note.isNotBlank()) {
                    Spacer(modifier = Modifier.height(3.dp))

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
                    text = "- LKR ${formatAmount(expense.amount)}",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = FinanceRed
                )

                IconButton(
                    onClick = onDeleteClick
                ) {
                    Icon(
                        imageVector = Icons.Default.Delete,
                        contentDescription = "Delete expense",
                        tint = MaterialTheme.colorScheme.error
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyHistoryCard() {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = "No transaction history found",
                fontSize = 16.sp,
                fontWeight = FontWeight.Bold
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = "Add income or expense records to see them here.",
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium
            )
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
private fun HistorySectionTitle(
    title: String,
    icon: ImageVector
) {
    Row(
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            tint = MaterialTheme.colorScheme.primary,
            modifier = Modifier.size(22.dp)
        )

        Spacer(modifier = Modifier.size(8.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

private fun formatAmount(value: Double): String {
    return "%,.0f".format(value)
}