package com.example.financeapp.ui.screens.dashboard

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.Payments
import androidx.compose.material.icons.filled.ShoppingCart
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.financeapp.data.model.Expense
import com.example.financeapp.data.model.Goal
import com.example.financeapp.data.model.Income
import com.example.financeapp.ui.components.ScreenContainer
import com.example.financeapp.ui.theme.FinanceGreen
import com.example.financeapp.ui.theme.FinanceRed
import com.example.financeapp.ui.utils.formatTimestampToDate

@Composable
fun DashboardScreen(
    onIncomeClick: () -> Unit = {},
    onExpenseClick: () -> Unit = {},
    onGoalClick: () -> Unit = {},
    onHistoryClick: () -> Unit = {},
    viewModel: DashboardViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadDashboardData()
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
            title = "Dashboard",
            subtitle = "Your monthly financial overview",
            icon = Icons.Default.AccountBalanceWallet
        )

        Spacer(modifier = Modifier.height(20.dp))

        if (uiState.isLoading) {
            Text(
                text = "Loading dashboard...",
                fontSize = 16.sp,
                fontWeight = FontWeight.Medium
            )
        } else {
            SummaryCards(
                totalIncome = uiState.totalIncome,
                totalExpense = uiState.totalExpense,
                balance = uiState.balance
            )

            Spacer(modifier = Modifier.height(20.dp))

            QuickActionsCard(
                onIncomeClick = onIncomeClick,
                onExpenseClick = onExpenseClick,
                onGoalClick = onGoalClick,
                onHistoryClick = onHistoryClick
            )

            Spacer(modifier = Modifier.height(20.dp))

            GoalProgressCard(
                goal = uiState.activeGoal,
                progressPercentage = uiState.goalProgressPercentage,
                remainingAmount = uiState.goalRemainingAmount,
                onGoalClick = onGoalClick
            )

            Spacer(modifier = Modifier.height(20.dp))

            RecentRecordsCard(
                recentIncomes = uiState.recentIncomes,
                recentExpenses = uiState.recentExpenses,
                onHistoryClick = onHistoryClick
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun SummaryCards(
    totalIncome: Double,
    totalExpense: Double,
    balance: Double
) {
    Column(
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        SummaryCard(
            title = "Total Income",
            amount = "LKR ${formatAmount(totalIncome)}",
            icon = Icons.Default.Payments,
            amountColor = FinanceGreen
        )

        SummaryCard(
            title = "Total Expenses",
            amount = "LKR ${formatAmount(totalExpense)}",
            icon = Icons.Default.ShoppingCart,
            amountColor = FinanceRed
        )

        SummaryCard(
            title = "Current Balance",
            amount = "LKR ${formatAmount(balance)}",
            icon = Icons.Default.AccountBalanceWallet,
            amountColor = if (balance >= 0) FinanceGreen else FinanceRed
        )
    }
}

@Composable
private fun SummaryCard(
    title: String,
    amount: String,
    icon: ImageVector,
    amountColor: Color
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(30.dp)
            )

            Spacer(modifier = Modifier.size(12.dp))

            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(
                    text = title,
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = amount,
                    fontSize = 22.sp,
                    fontWeight = FontWeight.Bold,
                    color = amountColor
                )
            }
        }
    }
}

@Composable
private fun QuickActionsCard(
    onIncomeClick: () -> Unit,
    onExpenseClick: () -> Unit,
    onGoalClick: () -> Unit,
    onHistoryClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            SectionHeader(
                title = "Quick Actions",
                icon = Icons.Default.Add
            )

            Spacer(modifier = Modifier.height(14.dp))

            Button(
                onClick = onIncomeClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Payments,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(modifier = Modifier.size(8.dp))

                Text("Add Income")
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = onExpenseClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.ShoppingCart,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(modifier = Modifier.size(8.dp))

                Text("Add Expense")
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = onGoalClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.Flag,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(modifier = Modifier.size(8.dp))

                Text("View Goals")
            }

            Spacer(modifier = Modifier.height(8.dp))

            OutlinedButton(
                onClick = onHistoryClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(
                    imageVector = Icons.Default.History,
                    contentDescription = null,
                    modifier = Modifier.size(18.dp)
                )

                Spacer(modifier = Modifier.size(8.dp))

                Text("View History")
            }
        }
    }
}

@Composable
private fun GoalProgressCard(
    goal: Goal?,
    progressPercentage: Int,
    remainingAmount: Double,
    onGoalClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            SectionHeader(
                title = "Goal Progress",
                icon = Icons.Default.TrackChanges
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (goal == null) {
                Text(
                    text = "No active goal found",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Create a savings goal to track your progress from the dashboard.",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = onGoalClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Create Goal")
                }
            } else {
                Text(
                    text = goal.title.ifBlank { "Savings Goal" },
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "$progressPercentage% completed",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Bold,
                    color = FinanceGreen
                )

                Spacer(modifier = Modifier.height(8.dp))

                DashboardProgressBar(
                    progress = progressPercentage / 100f
                )

                Spacer(modifier = Modifier.height(12.dp))

                DashboardInfoRow(
                    label = "Remaining",
                    value = "LKR ${formatAmount(remainingAmount)}"
                )

                DashboardInfoRow(
                    label = "Monthly Saving",
                    value = "LKR ${formatAmount(goal.monthlyRequiredSaving)}"
                )

                DashboardInfoRow(
                    label = "Deadline",
                    value = formatTimestampToDate(goal.deadline)
                )

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedButton(
                    onClick = onGoalClick,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("View Goal Details")
                }
            }
        }
    }
}

@Composable
private fun DashboardProgressBar(
    progress: Float
) {
    androidx.compose.foundation.layout.Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .padding(horizontal = 0.dp)
    ) {
        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(8.dp),
        ) {
            androidx.compose.foundation.layout.Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
            )
        }

        androidx.compose.foundation.layout.Box(
            modifier = Modifier
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .height(8.dp)
        )
    }
}

@Composable
private fun RecentRecordsCard(
    recentIncomes: List<Income>,
    recentExpenses: List<Expense>,
    onHistoryClick: () -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            SectionHeader(
                title = "Recent Records",
                icon = Icons.Default.History
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (recentIncomes.isEmpty() && recentExpenses.isEmpty()) {
                Text(
                    text = "No recent records found.",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            } else {
                recentIncomes.forEach { income ->
                    RecentRecordRow(
                        title = income.source.ifBlank { "Income" },
                        subtitle = formatTimestampToDate(income.date),
                        amount = "+ LKR ${formatAmount(income.amount)}",
                        amountColor = FinanceGreen
                    )
                }

                recentExpenses.forEach { expense ->
                    RecentRecordRow(
                        title = expense.categoryName.ifBlank { "Expense" },
                        subtitle = formatTimestampToDate(expense.date),
                        amount = "- LKR ${formatAmount(expense.amount)}",
                        amountColor = FinanceRed
                    )
                }
            }

            Spacer(modifier = Modifier.height(12.dp))

            OutlinedButton(
                onClick = onHistoryClick,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Open Full History")
            }
        }
    }
}

@Composable
private fun RecentRecordRow(
    title: String,
    subtitle: String,
    amount: String,
    amountColor: Color
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 7.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = subtitle,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Text(
            text = amount,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold,
            color = amountColor
        )
    }
}

@Composable
private fun DashboardInfoRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 5.dp),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = label,
            fontSize = 14.sp,
            fontWeight = FontWeight.Medium
        )

        Text(
            text = value,
            fontSize = 14.sp,
            fontWeight = FontWeight.Bold
        )
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
private fun SectionHeader(
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