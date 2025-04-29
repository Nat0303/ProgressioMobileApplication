package com.example.progressiomobileapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.progressiomobileapp.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Set up a button click to navigate to AnalyticsActivity
        binding.buttonViewAnalytics.setOnClickListener {
            // Start AnalyticsActivity when the button is clicked
            val intent = Intent(this, AnalyticsActivity::class.java)
            startActivity(intent)
        }

        binding.buttonViewTask.setOnClickListener {
            // Start AnalyticsActivity when the button is clicked
            val intent = Intent(this, CreateTaskActivity::class.java)
            startActivity(intent)
        }
    }
}
