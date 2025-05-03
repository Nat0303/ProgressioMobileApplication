package com.example.progressiomobileapp

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil.setContentView

class EmailVerificationActivity : AppCompatActivity() {

    private lateinit var verificationCodeInput: EditText
    private lateinit var btnVerify: Button
    private lateinit var tvVerificationMessage: TextView
    private var correctCode: String = ""  // Variable to store the correct verification code

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_email_verification)

        // Get email, role, and the verification code passed from SignUpActivity
        val email = intent.getStringExtra("email") ?: ""
        val role = intent.getStringExtra("role") ?: "User"
        correctCode = intent.getStringExtra("verificationCode") ?: ""  // Receive the correct verification code

        // Initialize Views
        verificationCodeInput = findViewById(R.id.etVerificationCode)
        btnVerify = findViewById(R.id.btnVerify)
        tvVerificationMessage = findViewById(R.id.tvVerificationMessage)
        val emailInput: EditText = findViewById(R.id.etEmailInput) // Reference to the email EditText

        // Set the email to the EditText
        emailInput.setText(email)  // Display the email

        // Verification button click listener
        btnVerify.setOnClickListener {
            val enteredCode = verificationCodeInput.text.toString()

            // Check if the verification code is empty
            if (enteredCode.isEmpty()) {
                Toast.makeText(this, "Please enter the verification code.", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Validate the entered code with the actual correct verification code
            if (verifyCode(enteredCode)) {
                // Check the user's role and navigate accordingly
                if (role == "Admin") {
                    // Navigate to HomepageAdminActivity
                    val intent = Intent(this, HomepageAdminActivity::class.java)
                    startActivity(intent)
                } else {
                    // Navigate to HomepageUserActivity
                    val intent = Intent(this, HomepageUserActivity::class.java)
                    startActivity(intent)
                }
                finish()  // Close the EmailVerificationActivity
            } else {
                Toast.makeText(this, "Invalid verification code. Please try again.", Toast.LENGTH_SHORT).show()
            }
        }
    }

    // Function to compare the entered code with the correct code received from SignUpActivity
    private fun verifyCode(enteredCode: String): Boolean {
        return enteredCode == correctCode  // Compare the entered code with the correct verification code
    }
}