package com.example.financeapp.ui.screens.goal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeapp.data.model.Goal
import com.example.financeapp.data.repository.FinanceRepository
import com.example.financeapp.ui.utils.calculateGoalProgressPercentage
import com.example.financeapp.ui.utils.calculateMonthlyRequiredSaving
import com.example.financeapp.ui.utils.isGoalCompleted
import com.example.financeapp.ui.utils.parseDateToTimestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GoalViewModel : ViewModel() {

    private val financeRepository = FinanceRepository()

    private val _uiState = MutableStateFlow(GoalUiState())
    val uiState: StateFlow<GoalUiState> = _uiState.asStateFlow()

    init {
        loadGoals()
    }

    fun onGoalTitleChange(goalTitle: String) {
        _uiState.value = _uiState.value.copy(
            goalTitle = goalTitle,
            goalTitleError = false
        )
    }

    fun onTargetAmountChange(targetAmount: String) {
        _uiState.value = _uiState.value.copy(
            targetAmount = targetAmount,
            targetAmountError = false
        )
    }

    fun onCurrentSavedAmountChange(currentSavedAmount: String) {
        _uiState.value = _uiState.value.copy(
            currentSavedAmount = currentSavedAmount,
            currentSavedAmountError = false
        )
    }

    fun onDeadlineChange(deadline: String) {
        _uiState.value = _uiState.value.copy(
            deadline = deadline,
            deadlineError = false
        )
    }

    fun onGoalStatusChange(status: String) {
        _uiState.value = _uiState.value.copy(
            selectedGoalStatus = status
        )
    }

    fun loadGoals() {
        viewModelScope.launch {
            val goals = financeRepository.getGoalsFromFirestore()

            _uiState.value = _uiState.value.copy(
                savedGoals = goals
            )
        }
    }

    fun saveGoal() {
        val currentState = _uiState.value

        val title = currentState.goalTitle.trim()
        val targetAmount = currentState.targetAmount.toDoubleOrNull()
        val currentSavedAmount = currentState.currentSavedAmount.toDoubleOrNull()
        val deadline = currentState.deadline.trim()

        val hasTitleError = title.isBlank()
        val hasTargetAmountError = targetAmount == null || targetAmount <= 0.0
        val hasCurrentSavedAmountError = currentSavedAmount == null || currentSavedAmount < 0.0
        val hasDeadlineError = deadline.isBlank()

        if (
            hasTitleError ||
            hasTargetAmountError ||
            hasCurrentSavedAmountError ||
            hasDeadlineError
        ) {
            _uiState.value = currentState.copy(
                goalTitleError = hasTitleError,
                targetAmountError = hasTargetAmountError,
                currentSavedAmountError = hasCurrentSavedAmountError,
                deadlineError = hasDeadlineError,
                shouldScrollToGoalTitle = hasTitleError,
                shouldScrollToTargetAmount = !hasTitleError && hasTargetAmountError,
                shouldScrollToCurrentSavedAmount = !hasTitleError &&
                        !hasTargetAmountError &&
                        hasCurrentSavedAmountError,
                shouldScrollToDeadline = !hasTitleError &&
                        !hasTargetAmountError &&
                        !hasCurrentSavedAmountError &&
                        hasDeadlineError
            )
            return
        }

        val finalTargetAmount = targetAmount ?: 0.0
        val finalCurrentSavedAmount = currentSavedAmount ?: 0.0

        val monthlyRequiredSaving = calculateMonthlyRequiredSaving(
            targetAmount = finalTargetAmount,
            currentAmount = finalCurrentSavedAmount,
            deadlineText = deadline
        )

        val status = if (
            isGoalCompleted(
                targetAmount = finalTargetAmount,
                currentAmount = finalCurrentSavedAmount
            )
        ) {
            "Completed"
        } else {
            currentState.selectedGoalStatus
        }

        val goal = Goal(
            title = title,
            targetAmount = finalTargetAmount,
            currentAmount = finalCurrentSavedAmount,
            deadline = parseDateToTimestamp(deadline),
            monthlyRequiredSaving = monthlyRequiredSaving,
            status = status
        )

        viewModelScope.launch {
            val isSaved = financeRepository.saveGoalToFirestore(goal)

            if (isSaved) {
                val goals = financeRepository.getGoalsFromFirestore()

                _uiState.value = GoalUiState(
                    savedGoals = goals,
                    isSavedSuccessfully = true,
                    successMessage = "Goal saved successfully"
                )
            }
        }
    }

    fun updateGoal(goal: Goal) {
        viewModelScope.launch {
            val monthlyRequiredSaving = calculateMonthlyRequiredSaving(
                targetAmount = goal.targetAmount,
                currentAmount = goal.currentAmount,
                deadlineText = com.example.financeapp.ui.utils.formatTimestampToDate(goal.deadline)
            )

            val status = if (
                isGoalCompleted(
                    targetAmount = goal.targetAmount,
                    currentAmount = goal.currentAmount
                )
            ) {
                "Completed"
            } else {
                goal.status
            }

            val updatedGoal = goal.copy(
                monthlyRequiredSaving = monthlyRequiredSaving,
                status = status
            )

            val isUpdated = financeRepository.updateGoalInFirestore(updatedGoal)

            if (isUpdated) {
                loadGoals()
            }
        }
    }

    fun deleteGoal(goalId: String) {
        viewModelScope.launch {
            val isDeleted = financeRepository.deleteGoalFromFirestore(goalId)

            if (isDeleted) {
                loadGoals()
            }
        }
    }

    fun pauseOtherActiveGoals(activeGoalId: String) {
        viewModelScope.launch {
            financeRepository.pauseOtherActiveGoals(activeGoalId)
            loadGoals()
        }
    }

    fun resetSuccessMessage() {
        _uiState.value = _uiState.value.copy(
            isSavedSuccessfully = false,
            successMessage = ""
        )
    }

    fun resetScrollState() {
        _uiState.value = _uiState.value.copy(
            shouldScrollToGoalTitle = false,
            shouldScrollToTargetAmount = false,
            shouldScrollToCurrentSavedAmount = false,
            shouldScrollToDeadline = false
        )
    }

    fun calculateProgress(goal: Goal): Int {
        return calculateGoalProgressPercentage(
            targetAmount = goal.targetAmount,
            currentAmount = goal.currentAmount
        )
    }
}