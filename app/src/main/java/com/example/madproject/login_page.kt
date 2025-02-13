package com.example.madproject

import android.content.Intent
import android.os.Bundle
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
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*

class login_page : AppCompatActivity() {

    private lateinit var database: FirebaseDatabase
    private lateinit var usersRef: DatabaseReference

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_page)

        // Initialize Firebase Database using your URL
        FirebaseApp.initializeApp(this)
        database = FirebaseDatabase.getInstance("https://madproj-d74a3-default-rtdb.asia-southeast1.firebasedatabase.app/")
        usersRef = database.getReference("users")

        val emailField = findViewById<EditText>(R.id.username_edit_text)
        val passwordField = findViewById<EditText>(R.id.psw_edit_text)
        val loginBtn = findViewById<Button>(R.id.login_btn)
        val createAccountLink = findViewById<TextView>(R.id.create_account_link)

        loginBtn.setOnClickListener {
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString().trim()

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Query the "users" node for a record with the matching email
            val query = usersRef.orderByChild("email").equalTo(email)
            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        var loginSuccessful = false
                        var username: String? = null
                        var userKey: String? = null
                        // Iterate over matching records (ideally, there should be one)
                        for (childSnapshot in snapshot.children) {
                            val storedPassword = childSnapshot.child("password").getValue(String::class.java)
                            if (storedPassword == password) {
                                // Retrieve the username and the unique key for this user
                                username = childSnapshot.child("username").getValue(String::class.java)
                                userKey = childSnapshot.key
                                loginSuccessful = true
                                break
                            }
                        }
                        if (loginSuccessful) {
                            Toast.makeText(this@login_page, "Login successful", Toast.LENGTH_SHORT).show()
                            val intent = Intent(this@login_page, dashboard_page::class.java)
                            // Pass both username and userKey to the dashboard
                            intent.putExtra("username", username ?: "User")
                            intent.putExtra("userKey", userKey)
                            startActivity(intent)
                            finish()
                        } else {
                            Toast.makeText(this@login_page, "Incorrect password", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Toast.makeText(this@login_page, "No user found with that email", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@login_page, "Database error: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }

        // Set up the clickable "Create an account" text
        val text = "Need an AirBuddy account? Create an account"
        val spannableString = SpannableString(text)
        val startIndex = text.indexOf("Create an account")
        val endIndex = startIndex + "Create an account".length
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val registrationIntent = Intent(this@login_page, registration_page::class.java)
                startActivity(registrationIntent)
            }
        }
        spannableString.setSpan(UnderlineSpan(), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        val linkColor = ContextCompat.getColor(this, R.color.dark_blue)
        spannableString.setSpan(ForegroundColorSpan(linkColor), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        createAccountLink.text = spannableString
        createAccountLink.movementMethod = LinkMovementMethod.getInstance()
    }
}
