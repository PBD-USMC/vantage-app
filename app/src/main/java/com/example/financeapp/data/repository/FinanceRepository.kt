package com.example.financeapp.data.repository

import com.example.financeapp.data.model.Expense
import com.example.financeapp.data.model.Goal
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

    suspend fun saveGoalToFirestore(goal: Goal): Boolean {
        return try {
            val userId = getCurrentUserId() ?: return false

            val goalDocument = firestore
                .collection("users")
                .document(userId)
                .collection("goals")
                .document()

            val goalWithId = goal.copy(
                goalId = goalDocument.id
            )

            goalDocument
                .set(goalWithId)
                .await()

            true
        } catch (exception: Exception) {
            false
        }
    }

    suspend fun updateGoalInFirestore(goal: Goal): Boolean {
        return try {
            val userId = getCurrentUserId() ?: return false

            if (goal.goalId.isBlank()) {
                return false
            }

            firestore
                .collection("users")
                .document(userId)
                .collection("goals")
                .document(goal.goalId)
                .set(goal)
                .await()

            true
        } catch (exception: Exception) {
            false
        }
    }

    suspend fun deleteGoalFromFirestore(goalId: String): Boolean {
        return try {
            val userId = getCurrentUserId() ?: return false

            if (goalId.isBlank()) {
                return false
            }

            firestore
                .collection("users")
                .document(userId)
                .collection("goals")
                .document(goalId)
                .delete()
                .await()

            true
        } catch (exception: Exception) {
            false
        }
    }

    suspend fun pauseOtherActiveGoals(activeGoalId: String): Boolean {
        return try {
            val userId = getCurrentUserId() ?: return false

            if (activeGoalId.isBlank()) {
                return false
            }

            val activeGoalsSnapshot = firestore
                .collection("users")
                .document(userId)
                .collection("goals")
                .whereEqualTo("status", "Active")
                .get()
                .await()

            val batch = firestore.batch()

            activeGoalsSnapshot.documents.forEach { document ->
                if (document.id != activeGoalId) {
                    batch.update(document.reference, "status", "Paused")
                }
            }

            batch.commit().await()

            true
        } catch (exception: Exception) {
            false
        }
    }

    suspend fun getGoalsFromFirestore(): List<Goal> {
        return try {
            val userId = getCurrentUserId() ?: return emptyList()

            firestore
                .collection("users")
                .document(userId)
                .collection("goals")
                .get()
                .await()
                .toObjects(Goal::class.java)
        } catch (exception: Exception) {
            emptyList()
        }
    }
}