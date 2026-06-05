package com.example.financeapp.data.repository

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import com.example.financeapp.data.local.AppDatabase
import com.example.financeapp.data.local.UserProfileEntity
import com.example.financeapp.data.model.AuthResult
import com.example.financeapp.data.model.User
import com.google.firebase.FirebaseNetworkException
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseAuthInvalidCredentialsException
import com.google.firebase.auth.FirebaseAuthUserCollisionException
import com.google.firebase.auth.FirebaseAuthWeakPasswordException
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class AuthRepository(
    private val context: Context? = null
) {

    private val firebaseAuth = FirebaseAuth.getInstance()
    private val firestore = FirebaseFirestore.getInstance()

    private val userProfileDao = context?.let {
        AppDatabase
            .getDatabase(it)
            .userProfileDao()
    }

    private val networkErrorMessage =
        "No internet connection. Please check your network and try again."

    suspend fun loginUser(
        email: String,
        password: String
    ): AuthResult {
        if (!isNetworkAvailable()) {
            return AuthResult(
                isSuccessful = false,
                message = networkErrorMessage
            )
        }

        return try {
            firebaseAuth
                .signInWithEmailAndPassword(email, password)
                .await()

            AuthResult(
                isSuccessful = true,
                message = ""
            )
        } catch (exception: Exception) {
            AuthResult(
                isSuccessful = false,
                message = getLoginErrorMessage(exception)
            )
        }
    }

    suspend fun registerUser(
        name: String,
        email: String,
        password: String
    ): AuthResult {
        if (!isNetworkAvailable()) {
            return AuthResult(
                isSuccessful = false,
                message = networkErrorMessage
            )
        }

        return try {
            val authResult = firebaseAuth
                .createUserWithEmailAndPassword(email, password)
                .await()

            val userId = authResult.user?.uid
                ?: return AuthResult(
                    isSuccessful = false,
                    message = "Registration failed. Please try again."
                )

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

            saveUserProfileToRoom(user)

            AuthResult(
                isSuccessful = true,
                message = ""
            )
        } catch (exception: Exception) {
            AuthResult(
                isSuccessful = false,
                message = getRegisterErrorMessage(exception)
            )
        }
    }

    suspend fun sendPasswordResetEmail(
        email: String
    ): AuthResult {
        if (!isNetworkAvailable()) {
            return AuthResult(
                isSuccessful = false,
                message = networkErrorMessage
            )
        }

        return try {
            firebaseAuth
                .sendPasswordResetEmail(email)
                .await()

            AuthResult(
                isSuccessful = true,
                message = "Password reset email sent. Please check your inbox."
            )
        } catch (exception: Exception) {
            AuthResult(
                isSuccessful = false,
                message = getPasswordResetErrorMessage(exception)
            )
        }
    }

    suspend fun getCurrentUserProfile(): User? {
        val userId = getCurrentUserId() ?: return null

        return try {
            val documentSnapshot = firestore
                .collection("users")
                .document(userId)
                .get()
                .await()

            val user = documentSnapshot.toObject(User::class.java)

            if (user != null) {
                saveUserProfileToRoom(user)
                user
            } else {
                getUserProfileFromRoom(userId)
            }
        } catch (exception: Exception) {
            getUserProfileFromRoom(userId)
        }
    }

    suspend fun updateUserName(
        name: String
    ): Boolean {
        if (!isNetworkAvailable()) {
            return false
        }

        return try {
            val userId = getCurrentUserId() ?: return false

            firestore
                .collection("users")
                .document(userId)
                .update("name", name)
                .await()

            val currentEmail = getCurrentUserEmail() ?: ""

            val updatedUser = User(
                userId = userId,
                name = name,
                email = currentEmail
            )

            saveUserProfileToRoom(updatedUser)

            true
        } catch (exception: Exception) {
            false
        }
    }

    private fun isNetworkAvailable(): Boolean {
        val appContext = context ?: return true

        val connectivityManager = appContext.getSystemService(
            Context.CONNECTIVITY_SERVICE
        ) as ConnectivityManager

        val activeNetwork = connectivityManager.activeNetwork
            ?: return false

        val networkCapabilities = connectivityManager.getNetworkCapabilities(activeNetwork)
            ?: return false

        return networkCapabilities.hasCapability(
            NetworkCapabilities.NET_CAPABILITY_INTERNET
        )
    }

    private fun getLoginErrorMessage(
        exception: Exception
    ): String {
        return when (exception) {
            is FirebaseNetworkException -> {
                networkErrorMessage
            }

            is FirebaseAuthInvalidCredentialsException -> {
                "Invalid email or password."
            }

            else -> {
                "Login failed. Please try again."
            }
        }
    }

    private fun getRegisterErrorMessage(
        exception: Exception
    ): String {
        return when (exception) {
            is FirebaseNetworkException -> {
                networkErrorMessage
            }

            is FirebaseAuthUserCollisionException -> {
                "This email is already registered."
            }

            is FirebaseAuthWeakPasswordException -> {
                "Password is too weak. Please use at least 6 characters."
            }

            is FirebaseAuthInvalidCredentialsException -> {
                "Please enter a valid email address."
            }

            else -> {
                "Registration failed. Please try again."
            }
        }
    }

    private fun getPasswordResetErrorMessage(
        exception: Exception
    ): String {
        return when (exception) {
            is FirebaseNetworkException -> {
                networkErrorMessage
            }

            is FirebaseAuthInvalidCredentialsException -> {
                "Please enter a valid registered email address."
            }

            else -> {
                "Failed to send reset email. Please try again."
            }
        }
    }

    private suspend fun saveUserProfileToRoom(
        user: User
    ) {
        userProfileDao?.saveUserProfile(
            UserProfileEntity(
                userId = user.userId,
                name = user.name,
                email = user.email
            )
        )
    }

    private suspend fun getUserProfileFromRoom(
        userId: String
    ): User? {
        val cachedProfile = userProfileDao
            ?.getUserProfile(userId)
            ?: return null

        return User(
            userId = cachedProfile.userId,
            name = cachedProfile.name,
            email = cachedProfile.email
        )
    }

    fun getCurrentUserId(): String? {
        return firebaseAuth.currentUser?.uid
    }

    fun getCurrentUserEmail(): String? {
        return firebaseAuth.currentUser?.email
    }

    fun logoutUser() {
        firebaseAuth.signOut()
    }
}