package com.example.progressiomobileapp

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.example.progressiomobileapp.data.Task

class TaskAdapter(private val taskList: List<Task>) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val itemView = LayoutInflater.from(parent.context).inflate(R.layout.item_task, parent, false)
        return TaskViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = taskList[position]
        holder.titleTextView.text = task.title
        holder.statusTextView.text = task.status

        // Edit button click
        holder.editButton.setOnClickListener {
            // Navigate to the edit task page
            val context = it.context
            val intent = Intent(context, EditTaskActivity::class.java)
            intent.putExtra("TASK", task) // Pass the task to be edited
            context.startActivity(intent)
        }

        // Delete button click
        holder.deleteButton.setOnClickListener {
            // Handle delete action
            taskList.toMutableList().removeAt(position)
            Toast.makeText(it.context, "Task deleted", Toast.LENGTH_SHORT).show()
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return taskList.size
    }

    inner class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val titleTextView: TextView = itemView.findViewById(R.id.tvTaskTitle)
        val statusTextView: TextView = itemView.findViewById(R.id.tvTaskStatus)
        val editButton: Button = itemView.findViewById(R.id.btnUpdateTask)
        val deleteButton: Button = itemView.findViewById(R.id.btnDeleteTask)
    }
}
