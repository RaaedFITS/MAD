package com.example.madproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.content.Intent
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.view.View
import android.widget.Button
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.core.text.set


class registration_page : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration_page)

        // Login button function goes to dashboard
        val signup_btn = findViewById<Button>(R.id.sign_up_btn)
        signup_btn.setOnClickListener {
            val dashboardIntent = Intent(this, dashboard_page::class.java)
            startActivity(dashboardIntent)
        }

        val signin_link = findViewById<TextView>(R.id.sign_in_link)

        val text = "Already have an account? Login"
        val spannableString = SpannableString(text)

        val startIndex = text.indexOf("Login")
        val endIndex = startIndex + "Login".length

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent = Intent(this@registration_page, login_page::class.java)
                startActivity(intent)
              }
          }
        spannableString.setSpan(UnderlineSpan(), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE) // underline the "Create an account"

        val color = ContextCompat.getColor(this, R.color.dark_blue) // text will color
        spannableString.setSpan(ForegroundColorSpan(color), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        spannableString.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        signin_link.text = spannableString
        signin_link.movementMethod = android.text.method.LinkMovementMethod.getInstance()

    }
}