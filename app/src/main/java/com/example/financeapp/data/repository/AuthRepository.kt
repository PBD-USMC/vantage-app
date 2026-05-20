package com.example.financeapp.data.repository

import com.example.financeapp.data.model.User
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    suspend fun loginUser(
        email: String,
        password: String
    ): Boolean {
        return try {
            firebaseAuth.signInWithEmailAndPassword(email, password).await()
            true
        } catch (exception: Exception) {
            false
        }
    }

    suspend fun registerUser(
        name: String,
        email: String,
        password: String
    ): Boolean {
        return try {
            val authResult = firebaseAuth
                .createUserWithEmailAndPassword(email, password)
                .await()

            val userId = authResult.user?.uid ?: return false

            val user = User(
                userId = userId,
                name = name,
                email = email
            )

            firestore
                .collection("users")
                .document(userId)
                .set(user)
                .await()

            true
        } catch (exception: Exception) {
            false
        }
    }

    fun getCurrentUserId(): String? {
        return firebaseAuth.currentUser?.uid
    }

    fun logoutUser() {
        firebaseAuth.signOut()
    }
}