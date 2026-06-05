package com.example.financeapp.ui.screens.dashboard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowDownward
import androidx.compose.material.icons.filled.ArrowUpward
import androidx.compose.material.icons.filled.BarChart
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.KeyboardArrowLeft
import androidx.compose.material.icons.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Remove
import androidx.compose.material.icons.filled.Today
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import com.example.financeapp.ui.theme.FinanceAmber
import com.example.financeapp.ui.theme.FinanceGreen
import com.example.financeapp.ui.theme.FinanceRed

@Composable
fun DashboardScreen(
    onIncomeClick: () -> Unit = {},
    onExpenseClick: () -> Unit = {},
    onGoalClick: () -> Unit = {},
    onHistoryClick: () -> Unit = {},
    onProfileClick: () -> Unit = {},
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
        maxWidth = 620.dp
    ) {
        DashboardTopSection(
            monthLabel = uiState.monthLabel,
            onPreviousMonthClick = viewModel::onPreviousMonthClick,
            onNextMonthClick = viewModel::onNextMonthClick,
            onCurrentMonthClick = viewModel::onCurrentMonthClick,
            onProfileClick = onProfileClick
        )

        Spacer(modifier = Modifier.height(20.dp))

        SummaryCardsRow(
            totalIncome = uiState.totalIncome,
            totalExpenses = uiState.totalExpenses,
            balance = uiState.balance
        )

        Spacer(modifier = Modifier.height(20.dp))

        SavingsGoalCard(
            goalTitle = uiState.goalTitle,
            goalDeadlineLabel = uiState.goalDeadlineLabel,
            goalProgress = uiState.goalProgress,
            savedAmount = uiState.savedAmount,
            remainingAmount = uiState.remainingAmount,
            monthlySaving = uiState.monthlySaving,
            onClick = onGoalClick
        )

        Spacer(modifier = Modifier.height(20.dp))

        SpendingSummaryCard(
            committedExpenses = uiState.committedExpenses,
            discretionaryExpenses = uiState.discretionaryExpenses,
            savedBalance = uiState.savedBalance
        )

        Spacer(modifier = Modifier.height(20.dp))

        RecentTransactionsCard(
            transactions = uiState.recentTransactions,
            onClick = onHistoryClick
        )

        Spacer(modifier = Modifier.height(20.dp))

        QuickActionsRow(
            onIncomeClick = onIncomeClick,
            onExpenseClick = onExpenseClick
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun DashboardTopSection(
    monthLabel: String,
    onPreviousMonthClick: () -> Unit,
    onNextMonthClick: () -> Unit,
    onCurrentMonthClick: () -> Unit,
    onProfileClick: () -> Unit
) {
    Column {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.Top
        ) {
            Column {
                Text(
                    text = "Dashboard",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(4.dp))

                Text(
                    text = "Monthly overview",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Medium
                )
            }

            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
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

                IconButton(
                    onClick = onProfileClick,
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Person,
                        contentDescription = "Account details",
                        tint = MaterialTheme.colorScheme.primary
                    )
                }
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
private fun SummaryCardsRow(
    totalIncome: Double,
    totalExpenses: Double,
    balance: Double
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        SummaryMiniCard(
            title = "Income",
            currency = "LKR",
            amount = formatAmount(totalIncome),
            icon = Icons.Default.ArrowUpward,
            iconColor = FinanceGreen,
            modifier = Modifier.weight(1f)
        )

        SummaryMiniCard(
            title = "Expenses",
            currency = "LKR",
            amount = formatAmount(totalExpenses),
            icon = Icons.Default.ArrowDownward,
            iconColor = FinanceRed,
            modifier = Modifier.weight(1f)
        )

        SummaryMiniCard(
            title = "Balance",
            currency = "LKR",
            amount = formatAmount(balance),
            icon = Icons.Default.AccountBalanceWallet,
            iconColor = MaterialTheme.colorScheme.primary,
            modifier = Modifier.weight(1f)
        )
    }
}

@Composable
private fun SummaryMiniCard(
    title: String,
    currency: String,
    amount: String,
    icon: ImageVector,
    iconColor: Color,
    modifier: Modifier = Modifier
) {
    Card(
        modifier = modifier.height(150.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 8.dp, vertical = 14.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            IconBadge(
                icon = icon,
                tint = iconColor
            )

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(6.dp))

            Text(
                text = currency,
                fontSize = 13.sp,
                fontWeight = FontWeight.Medium,
                maxLines = 1
            )

            Spacer(modifier = Modifier.height(2.dp))

            Text(
                text = amount,
                fontSize = 17.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1
            )
        }
    }
}

@Composable
private fun IconBadge(
    icon: ImageVector,
    tint: Color
) {
    Box(
        modifier = Modifier
            .size(34.dp)
            .clip(CircleShape)
            .background(tint.copy(alpha = 0.14f)),
        contentAlignment = Alignment.Center
    ) {
        Icon(
            imageVector = icon,
            contentDescription = null,
            modifier = Modifier.size(20.dp),
            tint = tint
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
            modifier = Modifier.size(22.dp),
            tint = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.size(8.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun SectionHeaderWithAction(
    title: String,
    icon: ImageVector,
    actionText: String
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier.size(22.dp),
                tint = MaterialTheme.colorScheme.primary
            )

            Spacer(modifier = Modifier.size(8.dp))

            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )
        }

        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = actionText,
                fontSize = 13.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary
            )

            Icon(
                imageVector = Icons.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.primary,
                modifier = Modifier.size(20.dp)
            )
        }
    }
}

@Composable
private fun SavingsGoalCard(
    goalTitle: String,
    goalDeadlineLabel: String,
    goalProgress: Float,
    savedAmount: Double,
    remainingAmount: Double,
    monthlySaving: Double,
    onClick: () -> Unit
) {
    val hasGoal = goalTitle.isNotBlank()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            SectionHeaderWithAction(
                title = "Savings Goal",
                icon = Icons.Default.Flag,
                actionText = if (hasGoal) "View Goal" else "Create Goal"
            )

            Spacer(modifier = Modifier.height(10.dp))

            if (!hasGoal) {
                Text(
                    text = "No savings goal yet",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Create your first savings goal to track your progress.",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = goalTitle,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.SemiBold
                        )

                        Text(
                            text = "Deadline: $goalDeadlineLabel",
                            fontSize = 14.sp
                        )
                    }

                    Text(
                        text = "${(goalProgress * 100).toInt()}%",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = FinanceGreen
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                FinanceProgressBar(progress = goalProgress)

                Spacer(modifier = Modifier.height(12.dp))

                GoalAmountRow(
                    label = "Saved",
                    value = "LKR ${formatAmount(savedAmount)}"
                )

                GoalAmountRow(
                    label = "Remaining",
                    value = "LKR ${formatAmount(remainingAmount)}"
                )

                GoalAmountRow(
                    label = "Monthly Saving",
                    value = "LKR ${formatAmount(monthlySaving)}"
                )
            }
        }
    }
}

@Composable
private fun FinanceProgressBar(
    progress: Float
) {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(8.dp)
            .clip(MaterialTheme.shapes.small)
            .background(MaterialTheme.colorScheme.background)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth(progress.coerceIn(0f, 1f))
                .height(8.dp)
                .clip(MaterialTheme.shapes.small)
                .background(FinanceGreen)
        )
    }
}

@Composable
private fun GoalAmountRow(
    label: String,
    value: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 6.dp),
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
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun SpendingSummaryCard(
    committedExpenses: Double,
    discretionaryExpenses: Double,
    savedBalance: Double
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            SectionHeader(
                title = "Spending Summary",
                icon = Icons.Default.BarChart
            )

            Spacer(modifier = Modifier.height(12.dp))

            SpendingRow(
                color = FinanceRed,
                category = "Committed",
                amount = "LKR ${formatAmount(committedExpenses)}"
            )

            SpendingRow(
                color = FinanceAmber,
                category = "Discretionary",
                amount = "LKR ${formatAmount(discretionaryExpenses)}"
            )

            SpendingRow(
                color = FinanceGreen,
                category = "Saved Balance",
                amount = "LKR ${formatAmount(savedBalance)}"
            )
        }
    }
}

@Composable
private fun SpendingRow(
    color: Color,
    category: String,
    amount: String
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 7.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(10.dp)
                    .clip(CircleShape)
                    .background(color)
            )

            Spacer(modifier = Modifier.size(10.dp))

            Text(
                text = category,
                fontSize = 15.sp,
                fontWeight = FontWeight.Medium
            )
        }

        Text(
            text = amount,
            fontSize = 15.sp,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun RecentTransactionsCard(
    transactions: List<DashboardTransaction>,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            SectionHeaderWithAction(
                title = "Recent Transactions",
                icon = Icons.Default.History,
                actionText = "View All"
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (transactions.isEmpty()) {
                Text(
                    text = "No recent transactions yet.",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            } else {
                transactions.forEach { transaction ->
                    TransactionRow(
                        title = transaction.title,
                        date = transaction.date,
                        category = transaction.category,
                        amount = transaction.amount,
                        isIncome = transaction.isIncome
                    )
                }
            }
        }
    }
}

@Composable
private fun TransactionRow(
    title: String,
    date: String,
    category: String,
    amount: String,
    isIncome: Boolean
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 9.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = title,
                fontSize = 15.sp,
                fontWeight = FontWeight.SemiBold
            )

            Text(
                text = "$date • $category",
                fontSize = 13.sp
            )
        }

        Text(
            text = amount,
            fontSize = 15.sp,
            color = if (isIncome) FinanceGreen else FinanceRed,
            fontWeight = FontWeight.Bold
        )
    }
}

@Composable
private fun QuickActionsRow(
    onIncomeClick: () -> Unit,
    onExpenseClick: () -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Button(
            onClick = onIncomeClick,
            modifier = Modifier
                .weight(1f)
                .height(52.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )

            Spacer(modifier = Modifier.size(6.dp))

            Text(
                text = "Income",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )
        }

        OutlinedButton(
            onClick = onExpenseClick,
            modifier = Modifier
                .weight(1f)
                .height(52.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Remove,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )

            Spacer(modifier = Modifier.size(6.dp))

            Text(
                text = "Expense",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                maxLines = 1
            )
        }
    }
}

private fun formatAmount(value: Double): String {
    return "%,.0f".format(value)
}