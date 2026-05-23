package com.example.financeapp.data.repository

import com.example.financeapp.data.model.Expense
import com.example.financeapp.data.model.Income
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FinanceRepository {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private fun getCurrentUserId(): String? {
        return firebaseAuth.currentUser?.uid
    }

    suspend fun addIncomeToFirestore(income: Income): Boolean {
        return try {
            val userId = getCurrentUserId() ?: return false

            val incomeDocument = firestore
                .collection("users")
                .document(userId)
                .collection("incomes")
                .document()

            val incomeWithId = income.copy(
                incomeId = incomeDocument.id
            )

            incomeDocument
                .set(incomeWithId)
                .await()

            true
        } catch (exception: Exception) {
            false
        }
    }

    suspend fun getIncomesFromFirestore(): List<Income> {
        return try {
            val userId = getCurrentUserId() ?: return emptyList()

            firestore
                .collection("users")
                .document(userId)
                .collection("incomes")
                .get()
                .await()
                .toObjects(Income::class.java)
        } catch (exception: Exception) {
            emptyList()
        }
    }

    suspend fun deleteIncomeFromFirestore(incomeId: String): Boolean {
        return try {
            val userId = getCurrentUserId() ?: return false

            if (incomeId.isBlank()) {
                return false
            }

            firestore
                .collection("users")
                .document(userId)
                .collection("incomes")
                .document(incomeId)
                .delete()
                .await()

            true
        } catch (exception: Exception) {
            false
        }
    }

    suspend fun getExpensesFromFirestore(): List<Expense> {
        return try {
            val userId = getCurrentUserId() ?: return emptyList()

            firestore
                .collection("users")
                .document(userId)
                .collection("expenses")
                .get()
                .await()
                .toObjects(Expense::class.java)
        } catch (exception: Exception) {
            emptyList()
        }
    }

    suspend fun addExpenseToFirestore(expense: Expense): Boolean {
        return try {
            val userId = getCurrentUserId() ?: return false

            val expenseDocument = firestore
                .collection("users")
                .document(userId)
                .collection("expenses")
                .document()

            val expenseWithId = expense.copy(
                expenseId = expenseDocument.id
            )

            expenseDocument
                .set(expenseWithId)
                .await()

            true
        } catch (exception: Exception) {
            false
        }
    }

    suspend fun deleteExpenseFromFirestore(expenseId: String): Boolean {
        return try {
            val userId = getCurrentUserId() ?: return false

            if (expenseId.isBlank()) {
                return false
            }

            firestore
                .collection("users")
                .document(userId)
                .collection("expenses")
                .document(expenseId)
                .delete()
                .await()

            true
        } catch (exception: Exception) {
            false
        }
    }
}