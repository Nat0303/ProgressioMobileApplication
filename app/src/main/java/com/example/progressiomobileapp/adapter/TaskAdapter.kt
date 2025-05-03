package com.example.progressiomobileapp.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.progressiomobileapp.R
import com.example.progressiomobileapp.data.Task

class TaskAdapter(
    private var tasks: List<Task>,
    private val onEditClick: (Task) -> Unit,
    private val onDeleteClick: (Task) -> Unit
) : RecyclerView.Adapter<TaskAdapter.TaskViewHolder>() {

    class TaskViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val title: TextView = itemView.findViewById(R.id.tvTitle)
        val status: TextView = itemView.findViewById(R.id.tvStatus)
        val dueDate: TextView = itemView.findViewById(R.id.tvDueDate)
        val btnEdit: ImageView = itemView.findViewById(R.id.btnEdit)
        val btnDelete: ImageView = itemView.findViewById(R.id.btnDelete)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TaskViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_task_admin, parent, false)
        return TaskViewHolder(view)
    }

    override fun onBindViewHolder(holder: TaskViewHolder, position: Int) {
        val task = tasks[position]
        holder.title.text = task.title
        holder.status.text = task.status
        holder.dueDate.text = "Due: ${task.dueDate ?: "N/A"}"
        holder.btnEdit.setOnClickListener { onEditClick(task) }
        holder.btnDelete.setOnClickListener { onDeleteClick(task) }
    }

    override fun getItemCount() = tasks.size

    fun updateTasks(newTasks: List<Task>) {
        this.tasks = newTasks
        notifyDataSetChanged()
    }
}