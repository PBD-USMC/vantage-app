package com.example.financeapp.ui.screens.history

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.FilterList
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
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
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.financeapp.ui.components.ScreenContainer
import com.example.financeapp.ui.theme.FinanceBlue
import com.example.financeapp.ui.theme.FinanceGreen
import com.example.financeapp.ui.theme.FinanceRed

@Composable
fun HistoryScreen(
    viewModel: HistoryViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val filteredTransactions by viewModel.filteredTransactions.collectAsState(
        initial = uiState.transactions
    )

    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadTransactions()
            }
        }

        lifecycleOwner.lifecycle.addObserver(observer)

        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
        }
    }

    ScreenContainer(
        maxWidth = 620.dp
    ) {
        HistoryTopSection(
            monthLabel = uiState.monthLabel,
            onPreviousMonthClick = viewModel::onPreviousMonthClick,
            onNextMonthClick = viewModel::onNextMonthClick,
            onCurrentMonthClick = viewModel::onCurrentMonthClick
        )

        Spacer(modifier = Modifier.height(20.dp))

        SummaryCard(
            totalIncome = uiState.totalIncome,
            totalExpense = uiState.totalExpense,
            balance = uiState.balance
        )

        Spacer(modifier = Modifier.height(20.dp))

        FilterCard(
            filters = viewModel.filters,
            selectedFilter = uiState.selectedFilter,
            onFilterSelected = viewModel::onFilterSelected
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

        TransactionHistoryCard(
            transactions = filteredTransactions,
            selectedFilter = uiState.selectedFilter,
            isLoading = uiState.isLoading,
            onDeleteClick = { transaction ->
                if (transaction.isIncome) {
                    viewModel.deleteIncome(transaction.transactionId)
                } else {
                    viewModel.deleteExpense(transaction.transactionId)
                }
            }
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun HistoryTopSection(
    monthLabel: String,
    onPreviousMonthClick: () -> Unit,
    onNextMonthClick: () -> Unit,
    onCurrentMonthClick: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Box(
                modifier = Modifier.weight(1f)
            ) {
                ScreenTitleWithIcon(
                    title = "History",
                    subtitle = "Review your income and expense records",
                    icon = Icons.Default.History
                )
            }

            IconButton(
                onClick = onCurrentMonthClick,
                modifier = Modifier.size(48.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Today,
                    contentDescription = "Current month",
                    tint = MaterialTheme.colorScheme.primary
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(
                    onClick = onPreviousMonthClick
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowLeft,
                        contentDescription = "Previous month"
                    )
                }

                Text(
                    text = monthLabel,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                IconButton(
                    onClick = onNextMonthClick
                ) {
                    Icon(
                        imageVector = Icons.Default.KeyboardArrowRight,
                        contentDescription = "Next month"
                    )
                }
            }
        }
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
                text = "Monthly Summary",
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
    valueColor: Color
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
private fun FilterCard(
    filters: List<String>,
    selectedFilter: String,
    onFilterSelected: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.FilterList,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )

                Spacer(modifier = Modifier.size(8.dp))

                Text(
                    text = "Filter Records",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                filters.forEach { filter ->
                    HistoryFilterChip(
                        text = filter,
                        selected = filter == selectedFilter,
                        onClick = {
                            onFilterSelected(filter)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun TransactionHistoryCard(
    transactions: List<HistoryTransaction>,
    selectedFilter: String,
    isLoading: Boolean,
    onDeleteClick: (HistoryTransaction) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.ReceiptLong,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.size(22.dp)
                )

                Spacer(modifier = Modifier.size(8.dp))

                Text(
                    text = "Transactions",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            when {
                isLoading -> {
                    Text(
                        text = "Loading history...",
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                transactions.isEmpty() -> {
                    Text(
                        text = when (selectedFilter) {
                            "Income" -> "No income records found for this month."
                            "Expense" -> "No expense records found for this month."
                            else -> "No transaction records found for this month."
                        },
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                else -> {
                    transactions.forEach { transaction ->
                        HistoryTransactionItem(
                            transaction = transaction,
                            onDeleteClick = {
                                onDeleteClick(transaction)
                            }
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun HistoryTransactionItem(
    transaction: HistoryTransaction,
    onDeleteClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            modifier = Modifier.weight(1f),
            verticalAlignment = Alignment.CenterVertically
        ) {
            TransactionIcon(
                isIncome = transaction.isIncome
            )

            Spacer(modifier = Modifier.size(10.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = transaction.title,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.SemiBold
                )

                Spacer(modifier = Modifier.height(2.dp))

                Text(
                    text = "${transaction.date} • ${transaction.category}",
                    fontSize = 13.sp,
                    fontWeight = FontWeight.Medium
                )

                if (transaction.note.isNotBlank()) {
                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = transaction.note,
                        fontSize = 13.sp
                    )
                }
            }
        }

        Column(
            horizontalAlignment = Alignment.End
        ) {
            Text(
                text = transaction.amount,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold,
                color = if (transaction.isIncome) FinanceGreen else FinanceRed
            )

            IconButton(
                onClick = onDeleteClick
            ) {
                Icon(
                    imageVector = Icons.Default.Delete,
                    contentDescription = "Delete transaction",
                    tint = MaterialTheme.colorScheme.error
                )
            }
        }
    }
}

@Composable
private fun TransactionIcon(
    isIncome: Boolean
) {
    val icon = if (isIncome) Icons.Default.ArrowUpward else Icons.Default.ArrowDownward
    val color = if (isIncome) FinanceGreen else FinanceRed

    Box(
        modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(color.copy(alpha = 0.14f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = color
        )
    }
}

@Composable
private fun HistoryFilterChip(
    text: String,
    selected: Boolean,
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
            selectedContainerColor = FinanceBlue,
            selectedLabelColor = Color.White
        )
    )
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

private fun formatAmount(value: Double): String {
    return "%,.0f".format(value)
}