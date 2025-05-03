package com.example.progressiomobileapp.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.progressiomobileapp.data.Task
import com.example.progressiomobileapp.data.AppDatabase
import com.example.progressiomobileapp.data.ChecklistItem
import com.example.progressiomobileapp.data.User
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

class TaskViewModel(application: Application) : AndroidViewModel(application) {

    private val taskDao = AppDatabase.getDatabase(application).taskDao()
    private val checklistDao = AppDatabase.getDatabase(application).checklistItemDao()

    private val _tasks = MutableStateFlow<List<Task>>(emptyList())
    val tasks: StateFlow<List<Task>> get() = _tasks

    init {
        loadAllTasks()
    }

    private fun loadAllTasks() {
        viewModelScope.launch {
            taskDao.getAllTasks().collect { fetchedTasks ->
                _tasks.value = fetchedTasks
            }
        }
    }

    suspend fun getUserById(userId: Int): User? {
        return AppDatabase.getDatabase(getApplication()).userDao().getUserById(userId) // Make sure you have this DAO method
    }


    fun addTask(task: Task) {
        viewModelScope.launch {
            taskDao.insert(task)
            loadAllTasks()
        }
    }

    suspend fun addTaskAndReturnId(task: Task): Long {
        return taskDao.insert(task) // This assumes the insert returns a task ID
    }

    fun updateTask(task: Task) {
        viewModelScope.launch {
            taskDao.update(task)
            loadAllTasks()
        }
    }

    fun deleteTask(task: Task) {
        viewModelScope.launch {
            checklistDao.deleteChecklistItemsByTaskId(task.taskId)
            taskDao.delete(task)
            loadAllTasks()
        }
    }

    // New function to fetch checklist items for a specific taskId
    fun getChecklistItemsByTaskId(taskId: Int) = checklistDao.getChecklistItemsForTask(taskId)

    fun addChecklistItem(item: ChecklistItem) {
        viewModelScope.launch {
            checklistDao.insert(item)
        }
    }

    fun deleteChecklistItemsByTaskId(taskId: Int) {
        viewModelScope.launch {
            checklistDao.deleteChecklistItemsByTaskId(taskId)
        }
    }

}