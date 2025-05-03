package com.example.progressiomobileapp

import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.progressiomobileapp.data.AppDatabase
import com.example.progressiomobileapp.data.Task
import com.github.mikephil.charting.charts.PieChart
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.components.Legend
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.PieEntry
import com.github.mikephil.charting.data.BarEntry
import com.github.mikephil.charting.data.PieData
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.PieDataSet
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter
import com.github.mikephil.charting.formatter.ValueFormatter

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class AnalyticsActivity : BaseActivity() {

    private lateinit var pieChartStatus: PieChart
    private lateinit var pieChartCompletion: PieChart
    private lateinit var barChartOverdueOnTime: BarChart
    private lateinit var barChartEmployeeTasks: BarChart

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_analytics)
        setupBottomNavigation(R.id.nav_analytics)

        pieChartStatus = findViewById(R.id.pieChartStatus)
        pieChartCompletion = findViewById(R.id.pieChartCompletion)
        barChartOverdueOnTime = findViewById(R.id.barChartOverdueOnTime)
        barChartEmployeeTasks = findViewById(R.id.barChartEmployeeTasks)

        // Fetch tasks for the logged-in user
        val userId = 1 // This should be retrieved from shared preferences or database (e.g., logged-in user)
        fetchAnalyticsData(userId)
    }

    // Use dummy data to simulate fetching tasks
    private fun fetchAnalyticsData(userId: Int) {
        // Dummy data for testing
        val tasks = listOf(
            Task(
                taskId = 1,
                title = "Task 1",
                description = "Description 1",
                status = "Completed",
                dueDate = "2025-05-01",
                assignedTo = 1,
                createdBy = 1,
                creationDate = "2025-04-01",
                historyId = 101
            ),
            Task(
                taskId = 2,
                title = "Task 2",
                description = "Description 2",
                status = "In Progress",
                dueDate = "2025-06-01",
                assignedTo = 1,
                createdBy = 1,
                creationDate = "2025-04-01",
                historyId = 102
            ),
            Task(
                taskId = 3,
                title = "Task 3",
                description = "Description 3",
                status = "Overdue",
                dueDate = "2025-04-10",
                assignedTo = 1,
                createdBy = 1,
                creationDate = "2025-04-01",
                historyId = 103
            ),
            Task(
                taskId = 4,
                title = "Task 4",
                description = "Description 4",
                status = "Completed",
                dueDate = "2025-05-01",
                assignedTo = 1,
                createdBy = 1,
                creationDate = "2025-04-01",
                historyId = 104
            ),
            Task(
                taskId = 5,
                title = "Task 5",
                description = "Description 5",
                status = "Overdue",
                dueDate = "2025-04-15",
                assignedTo = 1,
                createdBy = 1,
                creationDate = "2025-04-01",
                historyId = 105
            )
        )

        // Process the tasks data to populate the charts
        val (completed, inProgress, overdue) = tasks.partitionTasksByStatus()
        val (onTime, overdueTasks) = tasks.partitionTasksByTime()

        // Dummy data for employee task distribution
        val employeeTasks = mapOf(
            "Nat" to 3,
            "Ling" to 1,
            "Michelle" to 1
        )

        // Log the processed data to check values
        Log.d("AnalyticsActivity", "Completed: $completed, In Progress: $inProgress, Overdue: $overdue")
        Log.d("AnalyticsActivity", "On Time: $onTime, Overdue Tasks: $overdueTasks")

        // Update charts on the main thread
        runOnUiThread {
            updateTaskStatusPieChart(completed, inProgress, overdue)
            updateTaskCompletionPieChart(completed, inProgress)
            updateOverdueOnTimeBarChart(onTime, overdueTasks)
            updateEmployeeTaskDistributionBarChart(employeeTasks)
        }
    }

//    private fun fetchAnalyticsData(userId: Int) {
//        lifecycleScope.launch(Dispatchers.IO) {
//            val taskDao = AppDatabase.getDatabase(applicationContext).taskDao()
//            val tasks = taskDao.getTasksAssignedToUser(userId)
//
//            // Process the tasks data to populate the charts
//            val (completed, inProgress, overdue) = tasks.partitionTasksByStatus()
//            val (onTime, overdueTasks) = tasks.partitionTasksByTime()
//
//            runOnUiThread {
//                // Update pie chart for task status
//                updateTaskStatusPieChart(completed, inProgress, overdue)
//
//                // Update bar chart for overdue vs on time tasks
//                updateOverdueOnTimeBarChart(onTime, overdueTasks)
//            }
//        }
//    }

    // Partition tasks by their status
    private fun List<Task>.partitionTasksByStatus(): Triple<Int, Int, Int> {
        val completed = count { it.status == "Completed" }
        val inProgress = count { it.status == "In Progress" }
        val overdue = count { it.status == "Overdue" }
        return Triple(completed, inProgress, overdue)
    }

    // Partition tasks based on whether they are on time or overdue
    private fun List<Task>.partitionTasksByTime(): Pair<Int, Int> {
        val onTime = count { it.status == "Completed" || it.status == "In Progress" }
        val overdueTasks = count { it.status == "Overdue" }
        return Pair(onTime, overdueTasks)
    }

    // Update the pie chart for task status
    private fun updateTaskStatusPieChart(completed: Int, inProgress: Int, overdue: Int) {
        val entries = listOf(
            PieEntry(completed.toFloat(), "Completed"),
            PieEntry(inProgress.toFloat(), "In Progress"),
            PieEntry(overdue.toFloat(), "Overdue")
        )

        val dataSet = PieDataSet(entries, "Task Status").apply {
            colors = listOf(
                resources.getColor(R.color.teal_700, theme),
                resources.getColor(R.color.purple_700, theme),
                resources.getColor(R.color.red, theme)
            )
            valueTextColor = resources.getColor(R.color.black, theme)
            valueTextSize = resources.getDimension(R.dimen.pie_legend_text_size)
            valueTextColor = resources.getColor(R.color.black, theme)
        }

        val pieData = PieData(dataSet)
        pieData.setValueFormatter(object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return String.format("%.0f%%", value)  // Format the value to a whole number percentage
            }
        })

        pieChartStatus.data = pieData
        pieChartStatus.description.isEnabled = false // Hide default description
        pieChartStatus.setUsePercentValues(true) // Enable percent values
        pieChartStatus.invalidate() // Refresh the chart

        // Customize legend
        pieChartStatus.legend.apply {
            isEnabled = true
            verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
            horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
            orientation = Legend.LegendOrientation.HORIZONTAL
            formSize = resources.getDimension(R.dimen.pie_legend_form_size)
            formToTextSpace = resources.getDimension(R.dimen.pie_legend_form_text_spacing)
            xEntrySpace = resources.getDimension(R.dimen.pie_legend_item_horizontal_spacing)
            yEntrySpace = resources.getDimension(R.dimen.pie_legend_item_vertical_spacing)
            textSize = resources.getDimension(R.dimen.pie_legend_text_size)
        }

        pieChartStatus.setEntryLabelColor(resources.getColor(R.color.black, theme)) // Label color
        pieChartStatus.setEntryLabelTextSize(12f) // Text size for labels
        pieChartStatus.invalidate() // Refresh the chart
    }

    // Update the pie chart for task completion (completed vs incomplete)
    private fun updateTaskCompletionPieChart(completed: Int, inProgress: Int) {
        val entries = listOf(
            PieEntry(completed.toFloat(), "Completed"),
            PieEntry(inProgress.toFloat(), "Incomplete")
        )

        val dataSet = PieDataSet(entries, "Task Complete").apply {
            colors = listOf(
                resources.getColor(R.color.green, theme), // Completed
                resources.getColor(R.color.orange, theme) // Incomplete
            )

            // Set label colors and text size
            valueTextColor = resources.getColor(R.color.black, theme) // Label color
            valueTextColor = resources.getColor(R.color.black, theme) // Label color
            valueTextSize = resources.getDimension(R.dimen.pie_legend_text_size) // Adjusted text size
        }

        val pieData = PieData(dataSet)
        pieData.setValueFormatter(object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return String.format("%.0f%%", value)  // Format the value to a whole number percentage
            }
        })

        pieChartCompletion.data = pieData
        pieChartCompletion.description.isEnabled = false // Hide default description
        pieChartCompletion.setUsePercentValues(true) // Show percentages
        pieChartCompletion.invalidate()

        // Customize and enable the legend for PieChart
        pieChartCompletion.legend.apply {
            isEnabled = true // Enable legend
            verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
            horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
            orientation = Legend.LegendOrientation.HORIZONTAL // Set orientation to horizontal
            formSize = resources.getDimension(R.dimen.pie_legend_form_size) // Set the size of the color box
            formToTextSpace = resources.getDimension(R.dimen.pie_legend_form_text_spacing) // Space between color box and text
            xEntrySpace = resources.getDimension(R.dimen.pie_legend_item_horizontal_spacing) // Horizontal space between items
            yEntrySpace = resources.getDimension(R.dimen.pie_legend_item_vertical_spacing) // Vertical space between items
            textSize = resources.getDimension(R.dimen.pie_legend_text_size) // Set text size for the legend
        }

        // Setting a shadow effect for the labels
        pieChartCompletion.setEntryLabelColor(resources.getColor(R.color.black, theme)) // Label color
        pieChartCompletion.setEntryLabelTextSize(12f) // Text size for labels
        pieChartCompletion.invalidate() // Refresh the chart
    }

    // Update the bar chart for overdue vs on-time tasks
    private fun updateOverdueOnTimeBarChart(onTime: Int, overdue: Int) {
        val entries = listOf(
            BarEntry(0f, onTime.toFloat()),  // First bar
            BarEntry(1f, overdue.toFloat()) // Second bar
        )

        val dataSet = BarDataSet(entries, "Overdue vs On Time").apply {
            colors = listOf(
                resources.getColor(R.color.green, theme), // On Time
                resources.getColor(R.color.red, theme) // Overdue
            )
            valueTextColor = resources.getColor(R.color.black, theme)
            valueTextSize = resources.getDimension(R.dimen.bar_legend_text_size)
        }

        val barData = BarData(dataSet)
        barChartOverdueOnTime.data = barData
        barChartOverdueOnTime.description.isEnabled = false
        barChartOverdueOnTime.invalidate()

        // Customize X and Y axis labels
        val xAxis = barChartOverdueOnTime.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(listOf("On Time", "Overdue"))
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f

        val yAxis = barChartOverdueOnTime.axisLeft
        yAxis.setGranularity(1f)
        yAxis.textColor = resources.getColor(R.color.black, theme)

        // For the left Y-axis (axisLeft)
        val yAxisLeft = barChartOverdueOnTime.axisLeft
        yAxisLeft.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return value.toInt().toString() // Remove decimals and show integer value
            }
        }

        // For the right Y-axis (axisRight)
        val yAxisRight = barChartOverdueOnTime.axisRight
        yAxisRight.setGranularity(1f)
        yAxisRight.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return value.toInt().toString() // Remove decimals and show integer value
            }
        }

        // For BarChart values (labels on top of bars)
        val chartValueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return value.toInt().toString() // Format the bar values as integers
            }
        }

        // Apply to the BarChart itself
        barChartOverdueOnTime.barData.setValueFormatter(chartValueFormatter)

        // Customize legend
        barChartOverdueOnTime.legend.apply {
            isEnabled = true
            verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
            horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
            orientation = Legend.LegendOrientation.VERTICAL
            formSize = resources.getDimension(R.dimen.bar_legend_form_size)
            formToTextSpace = resources.getDimension(R.dimen.bar_legend_form_text_spacing)
            xEntrySpace = resources.getDimension(R.dimen.bar_legend_item_horizontal_spacing)
            yEntrySpace = resources.getDimension(R.dimen.bar_legend_item_vertical_spacing)
            textSize = resources.getDimension(R.dimen.bar_legend_text_size)
        }
    }

    // Update the bar chart for task distribution among employees
    private fun updateEmployeeTaskDistributionBarChart(employeeTasks: Map<String, Int>) {
        val entries: List<BarEntry> = employeeTasks.entries.mapIndexed { index, entry ->
            BarEntry(index.toFloat(), entry.value.toFloat()) // index for x-axis, task count for y-axis
        }

        val dataSet = BarDataSet(entries, "Task Distribution Among Employee").apply {
            colors = listOf(
                resources.getColor(R.color.blue, theme),
                resources.getColor(R.color.purple_700, theme),
                resources.getColor(R.color.green, theme)
            )
            valueTextColor = resources.getColor(R.color.black, theme) // Label color
            valueTextSize = resources.getDimension(R.dimen.bar_legend_text_size) // Set text size for the labels
        }

        val barData = BarData(dataSet)
        barChartEmployeeTasks.data = barData
        barChartEmployeeTasks.description.isEnabled = false // Hide description

        // Set x-axis labels (employee names) on the bar chart
        val xAxis = barChartEmployeeTasks.xAxis
        xAxis.valueFormatter = IndexAxisValueFormatter(employeeTasks.keys.toList())

        // Customize the X and Y axis labels
        xAxis.position = XAxis.XAxisPosition.BOTTOM
        xAxis.granularity = 1f // Enforce one value per label

        val yAxis = barChartEmployeeTasks.axisLeft
        yAxis.setGranularity(1f) // Enforce one value per label
        yAxis.textColor = resources.getColor(R.color.black, theme)

        // For the left Y-axis (axisLeft)
        val yAxisLeft = barChartEmployeeTasks.axisLeft
        yAxisLeft.setGranularity(1f)
        yAxisLeft.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return value.toInt().toString() // Remove decimals and show integer value
            }
        }

        // For the right Y-axis (axisRight)
        val yAxisRight = barChartEmployeeTasks.axisRight
        yAxisRight.setGranularity(1f)
        yAxisRight.valueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return value.toInt().toString() // Remove decimals and show integer value
            }
        }

        // For BarChart values (labels on top of bars)
        val chartValueFormatter = object : ValueFormatter() {
            override fun getFormattedValue(value: Float): String {
                return value.toInt().toString() // Format the bar values as integers
            }
        }

// Apply to the BarChart itself
        barChartEmployeeTasks.barData.setValueFormatter(chartValueFormatter)


        // Customize the legend for the BarChart
        barChartEmployeeTasks.legend.apply {
            isEnabled = true // Enable legend
            verticalAlignment = Legend.LegendVerticalAlignment.BOTTOM
            horizontalAlignment = Legend.LegendHorizontalAlignment.CENTER
            orientation = Legend.LegendOrientation.HORIZONTAL // Set the orientation to horizontal
            formSize = resources.getDimension(R.dimen.bar_legend_form_size) // Set the size of the legend color box
            formToTextSpace = resources.getDimension(R.dimen.bar_legend_form_text_spacing) // Space between color box and text
            xEntrySpace = resources.getDimension(R.dimen.bar_legend_item_horizontal_spacing) // Horizontal space between items
            yEntrySpace = resources.getDimension(R.dimen.bar_legend_item_vertical_spacing) // Vertical space between items
            textSize = resources.getDimension(R.dimen.bar_legend_text_size) // Set text size for the legend
        }

        barChartEmployeeTasks.invalidate() // Refresh the chart
    }
}