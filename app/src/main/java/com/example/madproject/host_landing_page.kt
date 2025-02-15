package com.example.madproject

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar

class host_landing_page : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_host_landing_page)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""  // This explicitly sets the ActionBar title to empty
        toolbar.setNavigationOnClickListener { onBackPressed() }


        // 1) Retrieve the userKey from SharedPreferences (instead of Intent)
        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val userKey = sharedPref.getString("USER_KEY", "") ?: ""

        if (userKey.isEmpty()) {
            Toast.makeText(this, "User key missing. Please log in again.", Toast.LENGTH_SHORT).show()
        }

        // For demonstration, we still pass userKey to the next screen,
        // but it's optional because we could also retrieve it there again from SharedPreferences
        val proceedButton = findViewById<Button>(R.id.proceedButton)
        proceedButton.setOnClickListener {
            val intent = Intent(this, host_select_page::class.java)
            intent.putExtra("userKey", userKey)
            startActivity(intent)
        }
    }
}
