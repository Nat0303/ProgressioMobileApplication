package com.example.progressiomobileapp

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.progressiomobileapp.data.Task
import com.example.progressiomobileapp.databinding.ActivityTaskPageBinding

class TaskPage : AppCompatActivity() {

    private lateinit var binding: ActivityTaskPageBinding
    private val taskList = mutableListOf<Task>() // List of tasks
    private lateinit var taskAdapter: TaskAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityTaskPageBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Dummy task data
        val dummyData = listOf(
            Task(1, "Task 1", "Description 1", "To-Do", "2025-05-01", 1, 1, "2025-04-01", 1),
            Task(2, "Task 2", "Description 2", "In Progress", "2025-06-01", 2, 1, "2025-04-02", 2),
            Task(3, "Task 3", "Description 3", "Completed", "2025-07-01", 3, 2, "2025-04-03", 3)
        )

        taskList.addAll(dummyData)

        // Set up RecyclerView to show task list
        taskAdapter = TaskAdapter(taskList)
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = taskAdapter

        // Update task count and progress
        val totalTasks = taskList.size
        val completedTasks = taskList.count { it.status == "Completed" }
        val progressPercentage = (completedTasks * 100) / totalTasks
        binding.tvTaskCount.text = "$completedTasks/$totalTasks Tasks"
        binding.progressBar.progress = progressPercentage

        // Add Task button click
        binding.fabAddTask.setOnClickListener {
            // Navigate to CreateTaskActivity to add a new task
            val intent = Intent(this, CreateTaskActivity::class.java)
            startActivity(intent)
        }
    }
}
