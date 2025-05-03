package com.example.progressiomobileapp

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.progressiomobileapp.adapter.TaskAdapter
import com.example.progressiomobileapp.data.*
import com.example.progressiomobileapp.viewmodel.TaskViewModel
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

class TaskAdminActivity : BaseActivity() {

    private lateinit var taskViewModel: TaskViewModel
    private lateinit var taskAdapter: TaskAdapter
    private lateinit var taskRecyclerView: RecyclerView
    private lateinit var btnAddTask: ImageButton

    private val dummyUsers = listOf(
        User(2, "John Doe", "john@example.com", "password123", "user", 1),
        User(3, "Jane Smith", "jane@example.com", "password123", "user", 1),
        User(4, "Emily Johnson", "emily@example.com", "password123", "user", 1)
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_task_admin)
        setupBottomNavigation(R.id.nav_tasks)

        taskRecyclerView = findViewById(R.id.recyclerViewTasks)
        btnAddTask = findViewById(R.id.btnAddTask)

        taskViewModel = ViewModelProvider(this)[TaskViewModel::class.java]

        taskAdapter = TaskAdapter(
            emptyList(),
            onEditClick = { showTaskDialog(it) },
            onDeleteClick = { showDeleteConfirmation(it) }
        )

        taskRecyclerView.layoutManager = LinearLayoutManager(this)
        taskRecyclerView.adapter = taskAdapter

        lifecycleScope.launch {
            taskViewModel.tasks.collect { tasks ->
                taskAdapter.updateTasks(tasks)
            }
        }

        btnAddTask.setOnClickListener {
            showTaskDialog(null)
        }

        insertInitialUserAndAdmin()
    }

    private fun showDeleteConfirmation(task: Task) {
        AlertDialog.Builder(this)
            .setTitle("Delete Task")
            .setMessage("Are you sure you want to delete this task?")
            .setPositiveButton("Yes") { _, _ ->
                taskViewModel.deleteTask(task)
                showMessage(true, "delete")
            }
            .setNegativeButton("No", null)
            .show()
    }

    private fun showMessage(success: Boolean, type: String) {
        val msg = when {
            success && type == "create" -> "Task created successfully"
            !success && type == "create" -> "Task creation failed"
            success && type == "update" -> "Task updated successfully"
            !success && type == "update" -> "Task update failed"
            success && type == "delete" -> "Task deleted successfully"
            else -> "Task deletion failed"
        }
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show()
    }

    suspend fun validateUserExists(userId: Int): Boolean {
        val user = taskViewModel.getUserById(userId)
        return user != null
    }

    private fun showTaskDialog(existingTask: Task?) {
        val builder = AlertDialog.Builder(this)
        builder.setTitle(if (existingTask == null) "Add Task" else "Edit Task")

        val view = layoutInflater.inflate(R.layout.dialog_task_form, null)
        val etTitle = view.findViewById<EditText>(R.id.etTaskTitle)
        val etDescription = view.findViewById<EditText>(R.id.etTaskDescription)
        val etDueDate = view.findViewById<EditText>(R.id.etTaskDueDate)
        val checkboxChecklist = view.findViewById<CheckBox>(R.id.checkboxChecklist)
        val checklistContainer = view.findViewById<LinearLayout>(R.id.checklistContainer)
        val btnAssign = view.findViewById<ImageView>(R.id.btnAssign)
        val tvAssign = view.findViewById<TextView>(R.id.tvAssign)
        val etChecklistItem = view.findViewById<EditText>(R.id.etChecklistItem)
        val btnAddChecklistItem = view.findViewById<Button>(R.id.btnAddChecklistItem)
        val checklistItemContainer = view.findViewById<LinearLayout>(R.id.checklistItemContainer)

        var selectedUserId: Int? = null
        val checklistItems = mutableListOf<CheckBox>()

        if (existingTask != null) {
            etTitle.setText(existingTask.title)
            etDescription.setText(existingTask.description)
            etDueDate.setText(existingTask.dueDate)

            selectedUserId = existingTask.assignedTo
            tvAssign.text = selectedUserId?.toString() ?: "No user selected" // Display the user ID (assigned_to)

            lifecycleScope.launch {
                taskViewModel.getChecklistItemsByTaskId(existingTask.taskId).collect { items ->
                    items.forEach { item ->
                        val checkBox = CheckBox(this@TaskAdminActivity).apply {
                            text = item.itemText
                            isChecked = item.isChecked == 1
                        }
                        checklistItemContainer.addView(checkBox)
                        checklistItems.add(checkBox)
                    }
                }
            }
        }

        etDueDate.setOnClickListener {
            val calendar = Calendar.getInstance()
            DatePickerDialog(this, { _, year, month, day ->
                val selected = Calendar.getInstance().apply { set(year, month, day) }
                etDueDate.setText(SimpleDateFormat("yyyy-MM-dd", Locale.US).format(selected.time))
            }, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show()
        }

        checkboxChecklist.setOnCheckedChangeListener { _, isChecked ->
            checklistContainer.visibility = if (isChecked) View.VISIBLE else View.GONE
        }

        btnAddChecklistItem.setOnClickListener {
            val itemText = etChecklistItem.text.toString()
            if (itemText.isNotEmpty()) {
                val checkBox = CheckBox(this).apply { text = itemText }
                checklistItemContainer.addView(checkBox)
                checklistItems.add(checkBox)
                etChecklistItem.text.clear()
            }
        }

        btnAssign.setOnClickListener {
            val userIds = dummyUsers.map { it.userId.toString() }.toTypedArray() // List of user IDs
            val selectedIndex = dummyUsers.indexOfFirst { it.userId == selectedUserId }

            AlertDialog.Builder(this)
                .setTitle("Assign Task")
                .setSingleChoiceItems(userIds, selectedIndex) { dialog, which ->
                    selectedUserId = dummyUsers[which].userId // Assign selected user's ID to the task
                }
                .setPositiveButton("Done") { _, _ ->
                    tvAssign.text = selectedUserId?.toString() ?: "No user selected" // Display the user ID (assigned_to)
                }
                .setNegativeButton("Cancel", null)
                .show()
        }

        builder.setView(view)
        builder.setPositiveButton(if (existingTask == null) "Create" else "Update") { _, _ ->
            val task = Task(
                taskId = existingTask?.taskId ?: 0, // Use existing task ID if editing
                title = etTitle.text.toString(),
                description = etDescription.text.toString(),
                status = existingTask?.status ?: "To-Do",
                dueDate = etDueDate.text.toString(),
                assignedTo = selectedUserId ?: existingTask?.assignedTo ?: 1, // Set assignedTo as user ID
                createdBy = 1,
                creationDate = existingTask?.creationDate ?: SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date()),
                completionDate = existingTask?.completionDate,
                pendingReviewTime = existingTask?.pendingReviewTime,
                historyId = existingTask?.historyId
            )

            lifecycleScope.launch {
                try {
                    // Ensure the assigned user exists
                    if (selectedUserId != null) {
                        val isUserValid = validateUserExists(task.assignedTo)
                        if (isUserValid) {
                            val taskId = if (existingTask == null) {
                                taskViewModel.addTaskAndReturnId(task)
                            } else {
                                taskViewModel.updateTask(task)
                                task.taskId
                            }

                            taskViewModel.deleteChecklistItemsByTaskId(taskId.toInt()) // Ensure previous checklist is cleared

                            checklistItems.forEachIndexed { index, checkBox ->
                                val item = ChecklistItem(
                                    taskId = taskId.toInt(),
                                    itemText = checkBox.text.toString(),
                                    isChecked = if (checkBox.isChecked) 1 else 0,
                                    itemOrder = index
                                )
                                taskViewModel.addChecklistItem(item)
                            }

                            showMessage(true, if (existingTask == null) "create" else "update")
                        } else {
                            // If user is invalid, assign to a default valid user (e.g., user_id = 1)
                            task.assignedTo = 1 // Default user
                            taskViewModel.addTask(task)
                            showMessage(false, "create")
                            Toast.makeText(this@TaskAdminActivity, "Invalid user ID. Assigned to default user.", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        showMessage(false, "create")
                        Toast.makeText(this@TaskAdminActivity, "Please select a valid user", Toast.LENGTH_SHORT).show()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                    showMessage(false, if (existingTask == null) "create" else "update")
                }
            }
        }

        builder.setNegativeButton("Cancel", null)
        builder.show()
    }

    private fun insertInitialUserAndAdmin() {
        lifecycleScope.launch {
            try {
                val db = AppDatabase.getDatabase(applicationContext)
                val existingUser = db.userDao().getUserById(1)
                val existingAdmin = db.adminDao().getAdminByUserId(1)

                if (existingUser == null) {
                    db.userDao().insert(
                        User(1, "Michelle", "michellekychin@gmail.com", "Cky@040314", "admin", 1)
                    )
                }

                if (existingAdmin == null) {
                    db.adminDao().insert(Admin(1, 1, null))
                }
            } catch (e: Exception) {
                e.printStackTrace()
                Toast.makeText(this@TaskAdminActivity, "Failed to insert user/admin", Toast.LENGTH_LONG).show()
            }
        }
    }
}