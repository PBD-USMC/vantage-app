package com.example.financeapp.ui.screens.goal

import com.example.financeapp.data.model.Goal

data class GoalUiState(
    val savedGoals: List<Goal> = emptyList(),
    val selectedGoalId: String = "",

    val goalTitle: String = "",
    val goalTitleError: Boolean = false,

    val targetAmount: String = "",
    val targetAmountError: Boolean = false,

    val currentSavedAmount: String = "",
    val currentSavedAmountError: Boolean = false,

    val deadline: String = "",
    val deadlineError: Boolean = false,

    val selectedGoalStatus: String = "Active",

    val shouldScrollToGoalTitle: Boolean = false,
    val shouldScrollToTargetAmount: Boolean = false,
    val shouldScrollToCurrentSavedAmount: Boolean = false,
    val shouldScrollToDeadline: Boolean = false,

    val isSavedSuccessfully: Boolean = false,
    val successMessage: String = ""
)