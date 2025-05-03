package com.example.progressiomobileapp.data.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.example.progressiomobileapp.data.User


@Dao
interface UserDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: User): Long

    @Query("SELECT * FROM Users WHERE user_id = :userId")
    suspend fun getUserById(userId: Int): User?

    @Query("SELECT * FROM Users WHERE email = :email")
    suspend fun getUserByEmail(email: String): User?

    @Update
    suspend fun update(user: User)

    @Delete
    suspend fun delete(user: User)

    @Query("SELECT * FROM Users")
    fun getAllUsers(): Flow<List<User>>
}