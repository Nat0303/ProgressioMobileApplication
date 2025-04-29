package com.example.progressiomobileapp.data.dao

import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.example.progressiomobileapp.data.Task


@Dao
interface TaskDao {
    // Insert a new task or replace if already exists
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(task: Task): Long

    // Get a specific task by taskId
    @Query("SELECT * FROM Tasks WHERE task_id = :taskId")
    suspend fun getTaskById(taskId: Int): Task?

    // Get tasks assigned to a specific user
    @Query("SELECT * FROM Tasks WHERE assigned_to = :assignedTo")
    fun getTasksAssignedToUser(assignedTo: Int): Flow<List<Task>>

    // Get tasks created by a specific admin
    @Query("SELECT * FROM Tasks WHERE created_by = :createdBy")
    fun getTasksCreatedByAdmin(createdBy: Int): Flow<List<Task>>

    // Update an existing task
    @Update
    suspend fun update(task: Task)

    // Delete a task
    @Delete
    suspend fun delete(task: Task)

    // Get all tasks
    @Query("SELECT * FROM Tasks")
    fun getAllTasks(): Flow<List<Task>>

    // Get tasks within a specific date range
    @Query("SELECT * FROM Tasks WHERE due_date BETWEEN :startDate AND :endDate")
    fun getTasksBetweenDates(startDate: String, endDate: String): Flow<List<Task>>

    // Get tasks for a specific date
    @Query("SELECT * FROM Tasks WHERE strftime('%Y-%m-%d', due_date) = :date")
    fun getTasksForDate(date: String): Flow<List<Task>>

    // Get tasks for a specific status
    @Query("SELECT * FROM Tasks WHERE status = :status")
    fun getTasksByStatus(status: String): Flow<List<Task>>
}

