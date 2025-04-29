package com.example.progressiomobileapp

import android.graphics.Typeface
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import android.graphics.Color
import android.os.Build
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.progressiomobileapp.data.Task
import com.example.progressiomobileapp.databinding.ActivityCreateBinding
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class CreateTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCreateBinding
    private val taskList = mutableListOf<Task>() // List to hold tasks

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCreateBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Dummy data for tasks
        val dummyData = listOf(
            Task(
                taskId = 1,
                title = "Task 1",
                description = "Description of Task 1",
                status = "To-Do",
                dueDate = "2025-05-01",
                assignedTo = 1,  // Example user ID
                createdBy = 1,   // Example admin ID
                creationDate = "2025-04-01",
                historyId = 201  // Example history ID
            ),
            Task(
                taskId = 2,
                title = "Task 2",
                description = "Description of Task 2",
                status = "In Progress",
                dueDate = "2025-06-01",
                assignedTo = 2,
                createdBy = 1,
                creationDate = "2025-04-02",
                historyId = 202
            ),
            Task(
                taskId = 3,
                title = "Task 3",
                description = "Description of Task 3",
                status = "Completed",
                dueDate = "2025-07-01",
                assignedTo = 3,
                createdBy = 2,
                creationDate = "2025-04-03",
                historyId = 203
            )
        )

        // Add the dummy data to the task list
        taskList.addAll(dummyData)

        // Display tasks in the RecyclerView and set the LayoutManager
        binding.recyclerView.layoutManager = LinearLayoutManager(this) // Set the LayoutManager here
        displayTasks()

        // Add Task Button click
        binding.btnAddTask.setOnClickListener {
            val currentDateTime = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")) // Get current date and time

            val newTask = Task(
                taskId = taskList.size + 1, // Automatically increment taskId
                title = "New Task",
                description = "This is a new task",
                status = "To-Do",
                dueDate = "2025-05-01",
                assignedTo = 1,  // Example user ID
                createdBy = 1,   // Example admin ID
                creationDate = currentDateTime,
                historyId = taskList.size + 1 // Example history ID
            )

            // Add the new task to the list
            if (taskList.add(newTask)) {
                Toast.makeText(this, getString(R.string.task_created), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, getString(R.string.task_creation_failed), Toast.LENGTH_SHORT).show()
            }

            // Update the displayed tasks
            displayTasks()
        }

        // Update Task Button click (this is a placeholder for now)
        binding.btnUpdateTask.setOnClickListener {
            // Update task logic here (this could be used to modify an existing task)
            // For now, we'll just show a success message
            if (taskList.isNotEmpty()) {
                // Updating the first task as an example
                val updatedTask = taskList[0]
                updatedTask.title = "Updated Task Title"
                updatedTask.description = "Updated task description"
                updatedTask.status = "Updated Status"

                Toast.makeText(this, getString(R.string.task_updated), Toast.LENGTH_SHORT).show()
                displayTasks() // Refresh the RecyclerView
            } else {
                Toast.makeText(this, getString(R.string.task_update_failed), Toast.LENGTH_SHORT).show()
            }
        }

        // Delete Task Button click
        binding.btnDeleteTask.setOnClickListener {
            // For this demo, we’ll just delete the first task in the list
            if (taskList.isNotEmpty()) {
                taskList.removeAt(0)  // Remove the first task (for demo purposes)
                Toast.makeText(this, getString(R.string.task_deleted), Toast.LENGTH_SHORT).show()
            } else {
                Toast.makeText(this, getString(R.string.task_deletion_failed), Toast.LENGTH_SHORT).show()
            }

            displayTasks()  // Refresh the RecyclerView
        }
    }

    // Display tasks in the RecyclerView or a simple layout
    private fun displayTasks() {
        // Clear any previous views before adding new ones
        binding.recyclerView.removeAllViews()

        // Loop through the taskList and create views for each task
        taskList.forEach { task ->
            // Create a LinearLayout for each task
            val taskLayout = LinearLayout(this).apply {
                orientation = LinearLayout.VERTICAL
                setPadding(16, 16, 16, 16)

                val titleTextView = TextView(this@CreateTaskActivity).apply {
                    text = task.title
                    textSize = 18f
                    setTypeface(null, Typeface.BOLD)
                }

                val descriptionTextView = TextView(this@CreateTaskActivity).apply {
                    text = task.description
                    textSize = 14f
                    setPadding(0, 4, 0, 4)
                }

                val statusTextView = TextView(this@CreateTaskActivity).apply {
                    text = "Status: ${task.status}"
                    textSize = 14f
                    setTextColor(Color.GRAY)
                    setPadding(0, 4, 0, 4)
                }

                // Add an Edit Button for each task
                val editButton = Button(this@CreateTaskActivity).apply {
                    text = "Edit Task"
                    setOnClickListener {
                        // When Edit button is clicked, show an edit dialog
                        showEditTaskDialog(task)
                    }
                }

                // Add a delete button for each task
                val deleteButton = Button(this@CreateTaskActivity).apply {
                    text = "Delete Task"
                    setOnClickListener {
                        // When delete button is clicked, delete the task
                        taskList.remove(task)
                        displayTasks()  // Refresh the displayed tasks
                    }
                }

                // Add the TextViews and Button to the LinearLayout
                addView(titleTextView)
                addView(descriptionTextView)
                addView(statusTextView)
                addView(deleteButton)
            }

            // Add the task layout to the RecyclerView (or in this case, just the main layout)
            binding.recyclerView.addView(taskLayout)
        }
    }

    // Show an edit task dialog to allow user to edit task details
    private fun showEditTaskDialog(task: Task) {
        // Create an EditText for each property we want to edit
        val titleEditText = EditText(this).apply {
            setText(task.title)
        }
        val descriptionEditText = EditText(this).apply {
            setText(task.description)
        }
        val statusEditText = EditText(this).apply {
            setText(task.status)
        }

        // Create an AlertDialog to display the editing form
        val builder = AlertDialog.Builder(this)
            .setTitle("Edit Task")
            .setView(titleEditText)  // Set the EditText for title
            .setView(descriptionEditText)  // Set the EditText for description
            .setView(statusEditText)  // Set the EditText for status
            .setPositiveButton("Save") { dialog, _ ->
                // Update the task with new values
                task.title = titleEditText.text.toString()
                task.description = descriptionEditText.text.toString()
                task.status = statusEditText.text.toString()

                // Refresh the displayed tasks
                displayTasks()
                Toast.makeText(this, getString(R.string.task_updated), Toast.LENGTH_SHORT).show()
                dialog.dismiss()
            }
            .setNegativeButton("Cancel") { dialog, _ ->
                dialog.dismiss()
            }

        builder.create().show()
    }
}
