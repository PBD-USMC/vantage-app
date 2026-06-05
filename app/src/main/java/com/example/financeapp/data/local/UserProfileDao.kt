package com.example.financeapp.data.local

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert

@Dao
interface UserProfileDao {

    @Upsert
    suspend fun saveUserProfile(
        userProfile: UserProfileEntity
    )

    @Query("SELECT * FROM user_profile WHERE userId = :userId LIMIT 1")
    suspend fun getUserProfile(
        userId: String
    ): UserProfileEntity?

    @Query("DELETE FROM user_profile WHERE userId = :userId")
    suspend fun deleteUserProfile(
        userId: String
    )
}