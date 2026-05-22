package com.example.financeapp.ui.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.financeapp.ui.screens.auth.LoginScreen
import com.example.financeapp.ui.screens.auth.RegisterScreen
import com.example.financeapp.ui.screens.income.IncomeScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Dashboard : Screen("dashboard")
    object Income : Screen("income")
    object Expense : Screen("expense")
    object Goal : Screen("goal")
    object GoalForm : Screen("goal_form?goalId={goalId}") {
        fun createRoute(goalId: String = ""): String {
            return "goal_form?goalId=$goalId"
        }
    }
    object History : Screen("history")
}

@Composable
fun AppNavigation() {
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = Screen.Login.route
    ) {
        composable(Screen.Login.route) {
            LoginScreen(
                onLoginSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) {
                            inclusive = true
                        }
                    }
                },
                onCreateAccountClick = {
                    navController.navigate(Screen.Register.route)
                }
            )
        }

        composable(Screen.Register.route) {
            RegisterScreen(
                onRegisterSuccess = {
                    navController.navigate(Screen.Dashboard.route) {
                        popUpTo(Screen.Login.route) {
                            inclusive = true
                        }
                    }
                },
                onLoginClick = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.Dashboard.route) {
            TemporaryDashboardScreen(
                onIncomeClick = {
                    navController.navigate(Screen.Income.route)
                },
                onExpenseClick = {
                    navController.navigate(Screen.Expense.route)
                },
                onGoalClick = {
                    navController.navigate(Screen.Goal.route)
                },
                onHistoryClick = {
                    navController.navigate(Screen.History.route)
                }
            )
        }

        composable(Screen.Income.route) {
            IncomeScreen()
        }

        composable(Screen.Expense.route) {
            TemporaryScreen(title = "Expense Screen")
        }

        composable(Screen.Goal.route) {
            TemporaryScreen(title = "Goal Screen")
        }

        composable(Screen.GoalForm.route) {
            TemporaryScreen(title = "Goal Form Screen")
        }

        composable(Screen.History.route) {
            TemporaryScreen(title = "History Screen")
        }
    }
}

@Composable
private fun TemporaryDashboardScreen(
    onIncomeClick: () -> Unit,
    onExpenseClick: () -> Unit,
    onGoalClick: () -> Unit,
    onHistoryClick: () -> Unit
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = "Dashboard")

        Button(onClick = onIncomeClick) {
            Text(text = "Add Income")
        }

        Button(onClick = onExpenseClick) {
            Text(text = "Add Expense")
        }

        Button(onClick = onGoalClick) {
            Text(text = "Goal")
        }

        Button(onClick = onHistoryClick) {
            Text(text = "History")
        }
    }
}

@Composable
private fun TemporaryScreen(title: String) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(text = title)
    }
}