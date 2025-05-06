package com.example.progressiomobileapp

import android.content.Intent
import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class TaskDetailAdminActivity : AppCompatActivity() {

    private lateinit var taskTitleTextView: TextView
    private lateinit var taskStatusTextView: TextView
    private lateinit var taskDueDateTextView: TextView
    private lateinit var taskDescriptionTextView: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_detail_admin)

        // Initialize TextViews
        taskTitleTextView = findViewById(R.id.tvTaskTitle)
        taskStatusTextView = findViewById(R.id.tvTaskStatus)
        taskDueDateTextView = findViewById(R.id.tvTaskDueDate)
        taskDescriptionTextView = findViewById(R.id.tvTaskDescription)

        // Get the task data passed from the Intent
        val taskTitle = intent.getStringExtra("TASK_TITLE")
        val taskStatus = intent.getStringExtra("TASK_STATUS")
        val taskDueDate = intent.getStringExtra("TASK_DUE_DATE")
        val taskDescription = intent.getStringExtra("TASK_DESCRIPTION")

        // Set the data to the TextViews
        taskTitleTextView.text = taskTitle
        taskStatusTextView.text = taskStatus
        taskDueDateTextView.text = "Completion Date: $taskDueDate" // Dynamically setting the due date
        taskDescriptionTextView.text = taskDescription

    }

    // Handle the back button click to go to the task history list
    fun goToHistoryList(view: android.view.View) {
        val intent = Intent(this, TaskHistoryAdminActivity::class.java)
        startActivity(intent)
    }
}
