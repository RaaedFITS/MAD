package com.example.madproject

import android.content.Intent
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.view.View
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.text.set

class login_page : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login_page)

        val register_acc = findViewById<TextView>(R.id.tv_create_account)

        val text = "Need an AirBuddy account? Create an account"
        val spannableString = SpannableString(text)

        val startIndex = text.indexOf("Create an account")
        val endIndex = startIndex + "Create an account".length


        val clickableSpan = object : ClickableSpan() {

            override fun onClick(widget: View) {
                val intent = Intent(this@login_page, registration_page::class.java)
                startActivity(intent)
            }
        }
        spannableString.setSpan(UnderlineSpan(), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE) // underline the "Create an account"

        val color = ContextCompat.getColor(this, R.color.dark_blue) // text will color
        spannableString.setSpan(ForegroundColorSpan(color), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        spannableString.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

        register_acc.text = spannableString
        register_acc.movementMethod = android.text.method.LinkMovementMethod.getInstance()


    }
}