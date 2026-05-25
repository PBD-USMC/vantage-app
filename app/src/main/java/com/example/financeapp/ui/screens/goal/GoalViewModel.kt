package com.example.financeapp.ui.screens.goal

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeapp.data.model.Goal
import com.example.financeapp.data.repository.FinanceRepository
import com.example.financeapp.ui.utils.calculateGoalProgressPercentage
import com.example.financeapp.ui.utils.calculateMonthlyRequiredSaving
import com.example.financeapp.ui.utils.formatTimestampToDate
import com.example.financeapp.ui.utils.isGoalCompleted
import com.example.financeapp.ui.utils.parseDateToTimestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.format.DateTimeParseException

class GoalViewModel : ViewModel() {

    private val financeRepository = FinanceRepository()

    private val _uiState = MutableStateFlow(GoalUiState())
    val uiState: StateFlow<GoalUiState> = _uiState.asStateFlow()

    val goalStatuses = listOf(
        "Active",
        "Pause",
        "Completed"
    )

    init {
        loadGoals()
    }

    fun loadGoals() {
        viewModelScope.launch {
            val goals = financeRepository.getGoalsFromFirestore()
                .sortedByDescending { goal -> goal.createdAt.seconds }

            _uiState.value = _uiState.value.copy(
                savedGoals = goals
            )
        }
    }

    fun loadGoalForForm(goalId: String) {
        if (goalId.isBlank()) {
            onAddNewGoalClick()
            return
        }

        viewModelScope.launch {
            val goals = financeRepository.getGoalsFromFirestore()
                .sortedByDescending { goal -> goal.createdAt.seconds }

            val selectedGoal = goals.find { goal ->
                goal.goalId == goalId
            }

            _uiState.value = _uiState.value.copy(
                savedGoals = goals
            )

            if (selectedGoal != null) {
                onGoalSelected(selectedGoal)
            }
        }
    }

    fun onGoalSelected(goal: Goal) {
        _uiState.value = _uiState.value.copy(
            selectedGoalId = goal.goalId,

            goalTitle = goal.title,
            goalTitleError = false,

            targetAmount = goal.targetAmount.toStringWithoutTrailingZero(),
            targetAmountError = false,

            currentSavedAmount = goal.currentAmount.toStringWithoutTrailingZero(),
            currentSavedAmountError = false,

            deadline = formatTimestampToDate(goal.deadline),
            deadlineError = false,

            selectedGoalStatus = normalizeGoalStatus(goal.status),

            shouldScrollToGoalTitle = false,
            shouldScrollToTargetAmount = false,
            shouldScrollToCurrentSavedAmount = false,
            shouldScrollToDeadline = false,

            isSavedSuccessfully = false,
            successMessage = ""
        )
    }

    fun onGoalTitleChange(goalTitle: String) {
        _uiState.value = _uiState.value.copy(
            goalTitle = goalTitle,
            goalTitleError = false,
            shouldScrollToGoalTitle = false,
            isSavedSuccessfully = false,
            successMessage = ""
        )
    }

    fun onTargetAmountChange(targetAmount: String) {
        if (isValidDecimalInput(targetAmount)) {
            _uiState.value = _uiState.value.copy(
                targetAmount = targetAmount,
                targetAmountError = false,
                currentSavedAmountError = false,
                shouldScrollToTargetAmount = false,
                isSavedSuccessfully = false,
                successMessage = ""
            )
        }
    }

    fun onCurrentSavedAmountChange(currentSavedAmount: String) {
        if (isValidDecimalInput(currentSavedAmount)) {
            _uiState.value = _uiState.value.copy(
                currentSavedAmount = currentSavedAmount,
                currentSavedAmountError = false,
                shouldScrollToCurrentSavedAmount = false,
                isSavedSuccessfully = false,
                successMessage = ""
            )
        }
    }

    fun onDeadlineChange(deadline: String) {
        _uiState.value = _uiState.value.copy(
            deadline = deadline,
            deadlineError = false,
            shouldScrollToDeadline = false,
            isSavedSuccessfully = false,
            successMessage = ""
        )
    }

    fun onGoalStatusChange(status: String) {
        _uiState.value = _uiState.value.copy(
            selectedGoalStatus = status,
            isSavedSuccessfully = false,
            successMessage = ""
        )
    }

    fun saveGoal() {
        onSaveGoalClick()
    }

    fun onSaveGoalClick() {
        val currentState = _uiState.value

        val title = currentState.goalTitle.trim()
        val targetAmount = currentState.targetAmount.toDoubleOrNull()

        val currentSavedAmount = if (currentState.currentSavedAmount.isBlank()) {
            0.0
        } else {
            currentState.currentSavedAmount.toDoubleOrNull()
        }

        val deadline = currentState.deadline.trim()

        val hasTitleError = title.isBlank()

        val hasTargetAmountError =
            currentState.targetAmount.isBlank() ||
                    targetAmount == null ||
                    targetAmount <= 0.0

        val hasCurrentSavedAmountError =
            currentSavedAmount == null ||
                    currentSavedAmount < 0.0 ||
                    (targetAmount != null && currentSavedAmount > targetAmount)

        val hasDeadlineError =
            deadline.isBlank() || !isValidFutureOrTodayDate(deadline)

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
                shouldScrollToCurrentSavedAmount =
                    !hasTitleError && !hasTargetAmountError && hasCurrentSavedAmountError,
                shouldScrollToDeadline =
                    !hasTitleError &&
                            !hasTargetAmountError &&
                            !hasCurrentSavedAmountError &&
                            hasDeadlineError,

                isSavedSuccessfully = false,
                successMessage = ""
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

        val finalStatus = if (
            isGoalCompleted(
                targetAmount = finalTargetAmount,
                currentAmount = finalCurrentSavedAmount
            )
        ) {
            "Completed"
        } else {
            normalizeGoalStatus(currentState.selectedGoalStatus)
        }

        val goal = Goal(
            goalId = currentState.selectedGoalId,
            title = title,
            targetAmount = finalTargetAmount,
            currentAmount = finalCurrentSavedAmount,
            deadline = parseDateToTimestamp(deadline),
            monthlyRequiredSaving = monthlyRequiredSaving,
            status = finalStatus
        )

        viewModelScope.launch {
            val isUpdatingExistingGoal = currentState.selectedGoalId.isNotBlank()

            val saveResult = if (isUpdatingExistingGoal) {
                financeRepository.updateGoalInFirestore(goal)
            } else {
                financeRepository.saveGoalToFirestore(goal)
            }

            if (saveResult) {
                var updatedGoals = financeRepository.getGoalsFromFirestore()
                    .sortedByDescending { savedGoal -> savedGoal.createdAt.seconds }

                val savedGoalId = if (isUpdatingExistingGoal) {
                    currentState.selectedGoalId
                } else {
                    updatedGoals.firstOrNull()?.goalId ?: ""
                }

                if (finalStatus == "Active" && savedGoalId.isNotBlank()) {
                    financeRepository.pauseOtherActiveGoals(savedGoalId)

                    updatedGoals = financeRepository.getGoalsFromFirestore()
                        .sortedByDescending { savedGoal -> savedGoal.createdAt.seconds }
                }

                _uiState.value = currentState.copy(
                    savedGoals = updatedGoals,

                    selectedGoalId = if (isUpdatingExistingGoal) {
                        currentState.selectedGoalId
                    } else {
                        savedGoalId
                    },

                    goalTitleError = false,
                    targetAmountError = false,
                    currentSavedAmountError = false,
                    deadlineError = false,

                    shouldScrollToGoalTitle = false,
                    shouldScrollToTargetAmount = false,
                    shouldScrollToCurrentSavedAmount = false,
                    shouldScrollToDeadline = false,

                    isSavedSuccessfully = true,
                    successMessage = if (isUpdatingExistingGoal) {
                        "Goal updated successfully"
                    } else {
                        "Goal saved successfully"
                    }
                )
            } else {
                _uiState.value = currentState.copy(
                    isSavedSuccessfully = false,
                    successMessage = "Goal save failed"
                )
            }
        }
    }

    fun updateGoal(goal: Goal) {
        viewModelScope.launch {
            val monthlyRequiredSaving = calculateMonthlyRequiredSaving(
                targetAmount = goal.targetAmount,
                currentAmount = goal.currentAmount,
                deadlineText = formatTimestampToDate(goal.deadline)
            )

            val status = if (
                isGoalCompleted(
                    targetAmount = goal.targetAmount,
                    currentAmount = goal.currentAmount
                )
            ) {
                "Completed"
            } else {
                normalizeGoalStatus(goal.status)
            }

            val updatedGoal = goal.copy(
                monthlyRequiredSaving = monthlyRequiredSaving,
                status = status
            )

            val isUpdated = financeRepository.updateGoalInFirestore(updatedGoal)

            if (isUpdated) {
                if (status == "Active" && updatedGoal.goalId.isNotBlank()) {
                    financeRepository.pauseOtherActiveGoals(updatedGoal.goalId)
                }

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

    fun onDeleteSelectedGoalClick() {
        val currentState = _uiState.value
        val selectedGoalId = currentState.selectedGoalId

        if (selectedGoalId.isBlank()) {
            _uiState.value = currentState.copy(
                isSavedSuccessfully = false,
                successMessage = "Select a goal to delete"
            )
            return
        }

        viewModelScope.launch {
            val deleteResult = financeRepository.deleteGoalFromFirestore(selectedGoalId)

            if (deleteResult) {
                val updatedGoals = financeRepository.getGoalsFromFirestore()
                    .sortedByDescending { goal -> goal.createdAt.seconds }

                _uiState.value = currentState.copy(
                    savedGoals = updatedGoals,
                    isSavedSuccessfully = true,
                    successMessage = "Goal deleted successfully"
                )
            } else {
                _uiState.value = currentState.copy(
                    isSavedSuccessfully = false,
                    successMessage = "Goal delete failed"
                )
            }
        }
    }

    fun pauseOtherActiveGoals(activeGoalId: String) {
        viewModelScope.launch {
            financeRepository.pauseOtherActiveGoals(activeGoalId)
            loadGoals()
        }
    }

    fun onAddNewGoalClick() {
        _uiState.value = _uiState.value.copy(
            selectedGoalId = "",

            goalTitle = "",
            goalTitleError = false,

            targetAmount = "",
            targetAmountError = false,

            currentSavedAmount = "",
            currentSavedAmountError = false,

            deadline = "",
            deadlineError = false,

            selectedGoalStatus = "Active",

            shouldScrollToGoalTitle = false,
            shouldScrollToTargetAmount = false,
            shouldScrollToCurrentSavedAmount = false,
            shouldScrollToDeadline = false,

            isSavedSuccessfully = false,
            successMessage = ""
        )
    }

    fun onResetClick() {
        onAddNewGoalClick()
    }

    fun resetSuccessMessage() {
        clearSuccessMessage()
    }

    fun clearSuccessMessage() {
        _uiState.value = _uiState.value.copy(
            isSavedSuccessfully = false,
            successMessage = ""
        )
    }

    fun resetScrollState() {
        clearScrollRequests()
    }

    fun clearScrollRequests() {
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

    private fun normalizeGoalStatus(status: String): String {
        return when (status) {
            "Paused" -> "Pause"
            "Pause" -> "Pause"
            "Completed" -> "Completed"
            else -> "Active"
        }
    }

    private fun isValidFutureOrTodayDate(date: String): Boolean {
        return try {
            val selectedDate = LocalDate.parse(date)
            !selectedDate.isBefore(LocalDate.now())
        } catch (exception: DateTimeParseException) {
            false
        }
    }

    private fun isValidDecimalInput(value: String): Boolean {
        return value.all { character ->
            character.isDigit() || character == '.'
        } && value.count { character ->
            character == '.'
        } <= 1
    }

    private fun Double.toStringWithoutTrailingZero(): String {
        return if (this % 1.0 == 0.0) {
            this.toLong().toString()
        } else {
            this.toString()
        }
    }
}