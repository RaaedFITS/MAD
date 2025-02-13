package com.example.madproject

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class host_landing_page : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_host_landing_page)

        // Retrieve the userKey passed from the previous screen (if any)
        val userKey = intent.getStringExtra("userKey") ?: ""

        // Assume there is a button with ID 'proceedButton' in the layout.
        val proceedButton = findViewById<Button>(R.id.proceedButton)
        proceedButton.setOnClickListener {
            val intent = Intent(this, host_select_page::class.java)
            intent.putExtra("userKey", userKey)
            startActivity(intent)
        }
    }
}
