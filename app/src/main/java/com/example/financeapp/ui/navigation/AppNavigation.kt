package com.example.financeapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.financeapp.ui.screens.auth.LoginScreen
import com.example.financeapp.ui.screens.auth.RegisterScreen
import com.example.financeapp.ui.screens.dashboard.DashboardScreen
import com.example.financeapp.ui.screens.expense.ExpenseScreen
import com.example.financeapp.ui.screens.goal.GoalFormScreen
import com.example.financeapp.ui.screens.goal.GoalScreen
import com.example.financeapp.ui.screens.history.HistoryScreen
import com.example.financeapp.ui.screens.income.IncomeScreen

sealed class Screen(val route: String) {
    object Login : Screen("login")
    object Register : Screen("register")
    object Dashboard : Screen("dashboard")
    object Income : Screen("income")
    object Expense : Screen("expense")
    object Goal : Screen("goal")
    object GoalForm : Screen("goal_form")
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
            DashboardScreen(
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
            ExpenseScreen()
        }

        composable(Screen.Goal.route) {
            GoalScreen(
                onAddNewGoalClick = {
                    navController.navigate(Screen.GoalForm.route)
                },
                onGoalClick = {
                    navController.navigate(Screen.GoalForm.route)
                }
            )
        }

        composable(Screen.GoalForm.route) {
            GoalFormScreen(
                onGoalSaved = {
                    navController.popBackStack()
                }
            )
        }

        composable(Screen.History.route) {
            HistoryScreen()
        }
    }
}