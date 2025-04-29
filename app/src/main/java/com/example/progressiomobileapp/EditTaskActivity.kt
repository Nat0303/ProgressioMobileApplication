package com.example.progressiomobileapp

import android.R.attr.progress
import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.progressiomobileapp.data.Task
import com.example.progressiomobileapp.databinding.ActivityEditTaskBinding

class EditTaskActivity : AppCompatActivity() {

    private lateinit var binding: ActivityEditTaskBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityEditTaskBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Retrieve the task passed via Intent
        val task = intent.getSerializableExtra("TASK") as Task

        // Set initial task values
        binding.edtTitle.setText(task.title)
        binding.edtDescription.setText(task.description)
        binding.spinnerAssignedUser.setSelection(task.assignedTo) // Assuming you have a list of users to select from
        binding.progressBar.progress = task.progress // Display progress

        // Setup checklist dynamically
        task.modules.forEach { module ->
            val checkBox = CheckBox(this).apply {
                text = module
                isChecked = task.completedModules.contains(module)
            }
            binding.checklistLayout.addView(checkBox)
        }

        // Due Date Button
        binding.btnDueDate.setOnClickListener {
            // Show a DatePickerDialog to set a new due date
        }

        // Save button to save the changes
        binding.btnSave.setOnClickListener {
            val updatedTask = task.apply {
                title = binding.edtTitle.text.toString()
                description = binding.edtDescription.text.toString()
                dueDate = binding.btnDueDate.text.toString() // Get updated due date
                assignedTo = binding.spinnerAssignedUser.selectedItemPosition // Update assigned user
                progress = calculateProgress() // Update progress
            }

            // Update the task in your list or database

            // Notify user of success
            Toast.makeText(this, "Task Updated!", Toast.LENGTH_SHORT).show()

            // Finish and return updated task
            val resultIntent = Intent()
            resultIntent.putExtra("UPDATED_TASK", updatedTask)
            setResult(RESULT_OK, resultIntent)
            finish()
        }

        // Cancel button to discard changes
        binding.btnCancel.setOnClickListener {
            finish()
        }
    }

    // Calculate progress based on checked modules
    private fun calculateProgress(): Int {
        val totalModules = binding.checklistLayout.childCount
        val checkedModules = (0 until totalModules).count {
            (binding.checklistLayout.getChildAt(it) as CheckBox).isChecked
        }
        return (checkedModules * 100) / totalModules
    }
}
