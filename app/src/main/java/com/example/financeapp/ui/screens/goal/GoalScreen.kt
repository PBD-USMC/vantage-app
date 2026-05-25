package com.example.financeapp.ui.screens.goal

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Flag
import androidx.compose.material.icons.filled.TrackChanges
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.financeapp.data.model.Goal
import com.example.financeapp.ui.components.ScreenContainer
import com.example.financeapp.ui.theme.FinanceGreen
import com.example.financeapp.ui.utils.formatTimestampToDate

@Composable
fun GoalScreen(
    onAddNewGoalClick: () -> Unit = {},
    onGoalClick: (String) -> Unit = {},
    viewModel: GoalViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val lifecycleOwner = LocalLifecycleOwner.current

    val displayGoal = uiState.savedGoals.firstOrNull { goal ->
        goal.status == "Active"
    } ?: uiState.savedGoals.firstOrNull()

    DisposableEffect(lifecycleOwner) {
        val observer = LifecycleEventObserver { _, event ->
            if (event == Lifecycle.Event.ON_RESUME) {
                viewModel.loadGoals()
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
            title = "Savings Goal",
            subtitle = "Track multiple goals and update progress clearly",
            icon = Icons.Default.TrackChanges
        )

        Spacer(modifier = Modifier.height(20.dp))

        CurrentGoalCard(
            goal = displayGoal
        )

        Spacer(modifier = Modifier.height(20.dp))

        SavedGoalsCard(
            goals = uiState.savedGoals,
            onGoalClick = onGoalClick
        )

        Spacer(modifier = Modifier.height(20.dp))

        Button(
            onClick = onAddNewGoalClick,
            modifier = Modifier.fillMaxWidth()
        ) {
            Icon(
                imageVector = Icons.Default.Add,
                contentDescription = null,
                modifier = Modifier.size(18.dp)
            )

            Spacer(modifier = Modifier.size(8.dp))

            Text(
                text = "Add New Goal",
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold
            )
        }

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun CurrentGoalCard(
    goal: Goal?
) {
    val target = goal?.targetAmount ?: 0.0
    val saved = goal?.currentAmount ?: 0.0

    val progress = if (target > 0) {
        (saved / target).coerceIn(0.0, 1.0).toFloat()
    } else {
        0f
    }

    val remaining = (target - saved).coerceAtLeast(0.0)

    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            SectionHeader(
                title = "Current Goal",
                icon = Icons.Default.Flag
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (goal == null) {
                Text(
                    text = "No savings goal yet",
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )

                Spacer(modifier = Modifier.height(6.dp))

                Text(
                    text = "Create your first savings goal to track progress.",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium
                )
            } else {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.Top
                ) {
                    Column(
                        modifier = Modifier.weight(1f)
                    ) {
                        Text(
                            text = goal.title.ifBlank { "Savings Goal" },
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(4.dp))

                        Text(
                            text = "${goal.status} goal",
                            fontSize = 14.sp,
                            color = FinanceGreen,
                            fontWeight = FontWeight.Medium
                        )
                    }

                    Text(
                        text = "${(progress * 100).toInt()}%",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = FinanceGreen
                    )
                }

                Spacer(modifier = Modifier.height(14.dp))

                GoalProgressBar(progress = progress)

                Spacer(modifier = Modifier.height(16.dp))

                GoalInfoRow(
                    label = "Target",
                    value = "LKR ${formatAmount(target)}"
                )

                GoalInfoRow(
                    label = "Saved",
                    value = "LKR ${formatAmount(saved)}"
                )

                GoalInfoRow(
                    label = "Remaining",
                    value = "LKR ${formatAmount(remaining)}"
                )

                GoalInfoRow(
                    label = "Deadline",
                    value = formatTimestampToDate(goal.deadline)
                )

                GoalInfoRow(
                    label = "Monthly Saving",
                    value = "LKR ${formatAmount(goal.monthlyRequiredSaving)}"
                )
            }
        }
    }
}

@Composable
private fun SavedGoalsCard(
    goals: List<Goal>,
    onGoalClick: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            SectionHeader(
                title = "Saved Goals",
                icon = Icons.Default.Flag
            )

            Spacer(modifier = Modifier.height(12.dp))

            if (goals.isEmpty()) {
                Text(
                    text = "No saved goals yet.",
                    fontSize = 15.sp,
                    fontWeight = FontWeight.Medium
                )
            } else {
                goals.forEach { goal ->
                    SavedGoalItem(
                        goal = goal,
                        onClick = {
                            onGoalClick(goal.goalId)
                        }
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                }
            }
        }
    }
}

@Composable
private fun SavedGoalItem(
    goal: Goal,
    onClick: () -> Unit
) {
    val progress = if (goal.targetAmount > 0) {
        (goal.currentAmount / goal.targetAmount).coerceIn(0.0, 1.0).toFloat()
    } else {
        0f
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onClick()
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(12.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = goal.title.ifBlank { "Savings Goal" },
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )

                    Spacer(modifier = Modifier.height(2.dp))

                    Text(
                        text = "${goal.status} • Deadline: ${formatTimestampToDate(goal.deadline)}",
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Medium
                    )
                }

                Text(
                    text = "${(progress * 100).toInt()}%",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Bold,
                    color = FinanceGreen
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            GoalProgressBar(progress = progress)
        }
    }
}

@Composable
private fun GoalProgressBar(
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
private fun GoalInfoRow(
    label: String,
    value: String
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

private fun formatAmount(
    value: Double
): String {
    return "%,.0f".format(value)
}