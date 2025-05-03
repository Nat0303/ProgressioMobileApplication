package com.example.progressiomobileapp.data.dao


import androidx.room.*
import kotlinx.coroutines.flow.Flow
import com.example.progressiomobileapp.data.ChecklistItem

@Dao
interface ChecklistItemDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(checklistItem: ChecklistItem): Long

    @Query("SELECT * FROM ChecklistItems WHERE task_id = :taskId")
    fun getChecklistItemsForTask(taskId: Int): Flow<List<ChecklistItem>>

    @Update
    suspend fun update(checklistItem: ChecklistItem)

    @Delete
    suspend fun delete(checklistItem: ChecklistItem)

    @Query("SELECT * FROM ChecklistItems")
    fun getAllChecklistItems(): Flow<List<ChecklistItem>>

    @Query("DELETE FROM ChecklistItems WHERE task_id = :taskId")
    suspend fun deleteChecklistItemsByTaskId(taskId: Int)
}