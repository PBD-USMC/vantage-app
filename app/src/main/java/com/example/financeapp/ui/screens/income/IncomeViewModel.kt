package com.example.financeapp.ui.screens.income

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.financeapp.data.model.Income
import com.example.financeapp.data.repository.FinanceRepository
import com.google.firebase.Timestamp
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.time.LocalDate
import java.time.ZoneId

class IncomeViewModel : ViewModel() {

    private val financeRepository = FinanceRepository()

    private val _uiState = MutableStateFlow(IncomeUiState())
    val uiState: StateFlow<IncomeUiState> = _uiState.asStateFlow()

    val incomeSources = listOf(
        "Salary",
        "Freelance",
        "AdSense",
        "Crypto",
        "Other"
    )

    val incomeTypes = listOf(
        "Fixed",
        "Variable",
        "Irregular",
        "Freelance",
        "Crypto Gain"
    )

    val currencies = listOf(
        "USD",
        "EUR",
        "GBP",
        "AUD",
        "CAD",
        "INR",
        "JPY",
        "USDT",
        "ETH"
    )

    init {
        loadIncomes()
    }

    fun onAmountReceivedChange(newValue: String) {
        if (isValidDecimalInput(newValue)) {
            _uiState.update {
                it.copy(
                    amountReceived = newValue,
                    amountReceivedError = false,
                    shouldScrollToAmount = false,
                    isSavedSuccessfully = false,
                    errorMessage = ""
                )
            }
        }
    }

    fun onIncomeSourceSelected(source: String) {
        _uiState.update {
            it.copy(
                selectedIncomeSource = source,
                isSavedSuccessfully = false,
                errorMessage = ""
            )
        }
    }

    fun onIncomeTypeSelected(type: String) {
        _uiState.update {
            it.copy(
                selectedIncomeType = type,
                isSavedSuccessfully = false,
                errorMessage = ""
            )
        }
    }

    fun onDateReceivedChange(newValue: String) {
        _uiState.update {
            it.copy(
                dateReceived = newValue,
                isSavedSuccessfully = false,
                errorMessage = ""
            )
        }
    }

    fun onCurrencyChange(newValue: String) {
        _uiState.update {
            it.copy(
                currency = "LKR",
                isSavedSuccessfully = false,
                errorMessage = ""
            )
        }
    }

    fun onOriginalCurrencyChange(newValue: String) {
        _uiState.update {
            it.copy(
                originalCurrency = newValue.uppercase(),
                isSavedSuccessfully = false,
                errorMessage = ""
            )
        }
    }

    fun onOriginalAmountChange(newValue: String) {
        if (isValidDecimalInput(newValue)) {
            _uiState.update {
                it.copy(
                    originalAmount = newValue,
                    originalAmountError = false,
                    shouldScrollToOriginalAmount = false,
                    isSavedSuccessfully = false,
                    errorMessage = ""
                )
            }
        }
    }

    fun onNoteChange(newValue: String) {
        val limit = _uiState.value.noteLimit

        if (newValue.length <= limit) {
            _uiState.update {
                it.copy(
                    note = newValue,
                    isSavedSuccessfully = false,
                    errorMessage = ""
                )
            }
        }
    }

    fun onSaveIncomeClick() {
        val currentState = _uiState.value

        val parsedAmount = currentState.amountReceived.toDoubleOrNull()
        val isAmountInvalid =
            currentState.amountReceived.isBlank() || parsedAmount == null || parsedAmount <= 0

        val parsedOriginalAmount = currentState.originalAmount.toDoubleOrNull()
        val isOriginalAmountInvalid =
            currentState.originalAmount.isNotBlank() &&
                    (parsedOriginalAmount == null || parsedOriginalAmount <= 0)

        if (isAmountInvalid || isOriginalAmountInvalid) {
            _uiState.update {
                it.copy(
                    amountReceivedError = isAmountInvalid,
                    originalAmountError = isOriginalAmountInvalid,

                    shouldScrollToAmount = isAmountInvalid,
                    shouldScrollToOriginalAmount = !isAmountInvalid && isOriginalAmountInvalid,

                    isSavedSuccessfully = false,
                    errorMessage = "Please enter valid income details."
                )
            }
            return
        }

        val income = Income(
            source = currentState.selectedIncomeSource,
            amount = parsedAmount,
            currency = "LKR",
            originalCurrency = currentState.originalCurrency.ifBlank { null },
            originalAmount = parsedOriginalAmount,
            incomeType = currentState.selectedIncomeType,
            date = parseDateToTimestamp(currentState.dateReceived),
            note = currentState.note.trim(),
            createdAt = Timestamp.now()
        )

        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = "",
                    isSavedSuccessfully = false
                )
            }

            val saveResult = financeRepository.addIncomeToFirestore(income)

            if (saveResult) {
                _uiState.update {
                    it.copy(
                        amountReceived = "",
                        amountReceivedError = false,
                        dateReceived = "",
                        currency = "LKR",
                        originalCurrency = "",
                        originalAmount = "",
                        originalAmountError = false,
                        note = "",

                        shouldScrollToAmount = false,
                        shouldScrollToOriginalAmount = false,

                        isLoading = false,
                        isSavedSuccessfully = true,
                        errorMessage = ""
                    )
                }

                loadIncomes()
            } else {
                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isSavedSuccessfully = false,
                        errorMessage = "Failed to save income. Please check your connection and try again."
                    )
                }
            }
        }
    }

    fun loadIncomes() {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    isLoading = true,
                    errorMessage = ""
                )
            }

            val incomes = financeRepository.getIncomesFromFirestore()

            _uiState.update {
                it.copy(
                    incomeList = incomes.sortedByDescending { income -> income.createdAt.seconds },
                    isLoading = false
                )
            }
        }
    }

    fun deleteIncome(incomeId: String) {
        viewModelScope.launch {
            val deleteResult = financeRepository.deleteIncomeFromFirestore(incomeId)

            if (deleteResult) {
                loadIncomes()
            } else {
                _uiState.update {
                    it.copy(
                        errorMessage = "Failed to delete income."
                    )
                }
            }
        }
    }

    fun clearSuccessMessage() {
        _uiState.update {
            it.copy(
                isSavedSuccessfully = false
            )
        }
    }

    fun clearScrollRequests() {
        _uiState.update {
            it.copy(
                shouldScrollToAmount = false,
                shouldScrollToOriginalAmount = false
            )
        }
    }

    private fun isValidDecimalInput(value: String): Boolean {
        return value.all { it.isDigit() || it == '.' } &&
                value.count { it == '.' } <= 1
    }

    private fun parseDateToTimestamp(dateText: String): Timestamp {
        return try {
            if (dateText.isBlank()) {
                Timestamp.now()
            } else {
                val localDate = LocalDate.parse(dateText)
                val instant = localDate
                    .atStartOfDay(ZoneId.systemDefault())
                    .toInstant()

                Timestamp(instant.epochSecond, 0)
            }
        } catch (exception: Exception) {
            Timestamp.now()
        }
    }
}