package com.example.madproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase

class registration_page : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration_page)

        // Initialize Firebase (configuration is read from google-services.json)
        FirebaseApp.initializeApp(this)

        // Get references to the EditText fields (ensure these IDs match your layout)
        val emailField = findViewById<EditText>(R.id.editTextTextPassword)       // For Email
        val usernameField = findViewById<EditText>(R.id.editTextTextPassword2)     // For Username
        val passwordField = findViewById<EditText>(R.id.editTextTextPassword3)     // For Password
        val signUpBtn = findViewById<Button>(R.id.sign_up_btn)

        // Use your specific Firebase Database URL
        val database = FirebaseDatabase.getInstance("https://madproj-d74a3-default-rtdb.asia-southeast1.firebasedatabase.app/")
        val usersRef = database.getReference("users")

        signUpBtn.setOnClickListener {
            Toast.makeText(this, "Signing up...", Toast.LENGTH_SHORT).show()

            val email = emailField.text.toString().trim()
            val username = usernameField.text.toString().trim()
            val password = passwordField.text.toString().trim()

            // Basic input validation
            if (email.isEmpty() || username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Create a new user node (push generates a unique key)
            val newUserRef = usersRef.push()

            // Prepare user data to save, including role = "customer"
            val userData = mapOf(
                "email" to email,
                "username" to username,
                "password" to password,  // WARNING: Do not store plain text passwords in production!
                "role" to "customer"
            )

            newUserRef.setValue(userData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()
                    // Launch the dashboard and pass the username and the unique user key
                    val dashboardIntent = Intent(this, dashboard_page::class.java)
                    dashboardIntent.putExtra("username", username)
                    dashboardIntent.putExtra("userKey", newUserRef.key)
                    startActivity(dashboardIntent)
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to save user data: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }

        // Set up the "Already have an account? Login" clickable text
        val signInLink = findViewById<TextView>(R.id.sign_in_link)
        val linkText = "Already have an account? Login"
        val spannableString = SpannableString(linkText)
        val startIndex = linkText.indexOf("Login")
        val endIndex = startIndex + "Login".length

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent = Intent(this@registration_page, login_page::class.java)
                startActivity(intent)
            }
        }

        spannableString.setSpan(UnderlineSpan(), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        val linkColor = ContextCompat.getColor(this, R.color.dark_blue)
        spannableString.setSpan(ForegroundColorSpan(linkColor), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        signInLink.text = spannableString
        signInLink.movementMethod = LinkMovementMethod.getInstance()
    }
}
