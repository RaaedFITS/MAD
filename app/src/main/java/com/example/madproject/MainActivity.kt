package com.example.madproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.content.Intent
import com.google.firebase.FirebaseApp


class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        // Initialize Firebase explicitly if needed
        FirebaseApp.initializeApp(this)
        setContentView(R.layout.activity_main)

        val start_btn = findViewById<Button>(R.id.getstarted_btn)
        start_btn.setOnClickListener {
            val intent = Intent(this, login_page::class.java)
            startActivity(intent)
        }
    }
}
