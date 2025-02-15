package com.example.madproject

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.util.Log
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

        // 1) Initialize Firebase
        FirebaseApp.initializeApp(this)
        database = FirebaseDatabase.getInstance(
            "https://madproj-d74a3-default-rtdb.asia-southeast1.firebasedatabase.app/"
        )
        usersRef = database.getReference("users")

        // 2) Find Views
        val emailField = findViewById<EditText>(R.id.username_edit_text)
        val passwordField = findViewById<EditText>(R.id.psw_edit_text)
        val loginBtn = findViewById<Button>(R.id.login_btn)
        val createAccountLink = findViewById<TextView>(R.id.create_account_link)

        // 3) Handle Login Button Click
        loginBtn.setOnClickListener {
            val email = emailField.text.toString().trim()
            val password = passwordField.text.toString().trim()

            Log.d("LOGIN_DEBUG", "Login button clicked")
            Log.d("LOGIN_DEBUG", "Email: $email, Password: $password")

            if (email.isEmpty() || password.isEmpty()) {
                Log.e("LOGIN_ERROR", "Email or password is empty")
                Toast.makeText(this, "Please enter email and password", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            Log.d("LOGIN_DEBUG", "Querying database for email: $email")
            val query = usersRef.orderByChild("email").equalTo(email)

            query.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    Log.d("LOGIN_DEBUG", "Snapshot exists: ${snapshot.exists()}")
                    Log.d("LOGIN_DEBUG", "Snapshot content: ${snapshot.value}")

                    if (snapshot.exists()) {
                        var loggedIn = false
                        for (childSnapshot in snapshot.children) {
                            val storedEmail = childSnapshot.child("email").getValue(String::class.java)
                            val storedPassword = childSnapshot.child("password").getValue(String::class.java)

                            Log.d("LOGIN_DEBUG", "Stored Email: $storedEmail, Stored Password: $storedPassword")

                            // 4) Check password
                            if (storedPassword == password) {
                                // 4a) User's Firebase Key
                                val userKey = childSnapshot.key ?: ""
                                // 4b) Retrieve other user fields
                                val userName = childSnapshot.child("username").getValue(String::class.java) ?: ""
                                val userRole = childSnapshot.child("role").getValue(String::class.java) ?: "customer"
                                // (If you also want to store the user’s password or email locally)
                                val userEmail = childSnapshot.child("email").getValue(String::class.java) ?: ""
                                // val userPassword = childSnapshot.child("password").getValue(String::class.java) ?: ""

                                Log.d("LOGIN_DEBUG", "Login successful for userKey: $userKey")

                                // 5) Store info in SharedPreferences
                                val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                                sharedPref.edit().apply {
                                    putString("USER_KEY", userKey)
                                    putString("USER_NAME", userName)
                                    putString("USER_ROLE", userRole)
                                    // If you want email/password too, uncomment:
                                    // putString("USER_EMAIL", userEmail)
                                    // putString("USER_PASSWORD", userPassword)
                                    apply()
                                }

                                Toast.makeText(this@login_page, "Login successful", Toast.LENGTH_SHORT).show()

                                // 6) Navigate to Dashboard (or whichever page you want)
                                val intent = Intent(this@login_page, dashboard_page::class.java)
                                startActivity(intent)
                                finish()

                                loggedIn = true
                                break
                            }
                        }

                        if (!loggedIn) {
                            // Means we found the email, but the password didn't match
                            Log.e("LOGIN_ERROR", "Incorrect password")
                            Toast.makeText(this@login_page, "Incorrect password", Toast.LENGTH_SHORT).show()
                        }
                    } else {
                        Log.e("LOGIN_ERROR", "No user found with that email")
                        Toast.makeText(this@login_page, "No user found with that email", Toast.LENGTH_SHORT).show()
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("LOGIN_ERROR", "Database error: ${error.message}")
                    Toast.makeText(
                        this@login_page,
                        "Database error: ${error.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }

        // 7) Setup clickable "Create an account" text
        val text = "Need an AirBuddy account? Create an account"
        val spannableString = SpannableString(text)
        val startIndex = text.indexOf("Create an account")
        val endIndex = startIndex + "Create an account".length
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                Log.d("LOGIN_DEBUG", "Navigating to Registration Page")
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
