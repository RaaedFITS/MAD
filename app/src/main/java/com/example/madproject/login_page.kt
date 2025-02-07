package com.example.madproject

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat

class login_page : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_page)

        // Login button function goes to dashboard
        val loginbtn = findViewById<Button>(R.id.login_btn)
        loginbtn.setOnClickListener {
            val dashboardIntent = Intent(this, dashboard_page::class.java)
            startActivity(dashboardIntent)
        }

        // Create account link function and variables
        val createaccount_link = findViewById<TextView>(R.id.create_account_link)
        val text = "Need an AirBuddy account? Create an account"
        val spannableString = SpannableString(text)
        val startIndex = text.indexOf("Create an account")
        val endIndex = startIndex + "Create an account".length

        // ClickableSpan for "Create an account"
        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val registrationIntent = Intent(this@login_page, registration_page::class.java)
                startActivity(registrationIntent)
            }
        }

        // Add underline, color, and click functionality
        spannableString.setSpan(UnderlineSpan(), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        val color = ContextCompat.getColor(this, R.color.dark_blue) // Apply color to the text
        spannableString.setSpan(ForegroundColorSpan(color), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        // Set the styled text and make it clickable
        createaccount_link.text = spannableString
        createaccount_link.movementMethod = android.text.method.LinkMovementMethod.getInstance()
    }
}