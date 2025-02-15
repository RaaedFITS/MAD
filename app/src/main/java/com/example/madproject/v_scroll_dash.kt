package com.example.madproject

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.google.firebase.database.FirebaseDatabase

class v_scroll_dash : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_vscroll_dash)

        // Retrieve extras from the Intent
        val placeId = intent.getStringExtra("placeId") ?: ""
        val category = intent.getStringExtra("category") ?: ""
        val locationName = intent.getStringExtra("locationName") ?: ""
        val description = intent.getStringExtra("description") ?: ""
        val price = intent.getDoubleExtra("price", 0.0)
        val imageRes = intent.getIntExtra("imageRes", 0)

        // Find the views in the layout
        val ivPlace = findViewById<ImageView>(R.id.imageView13)
        val tvTitle = findViewById<TextView>(R.id.textView10)
        val tvRating = findViewById<TextView>(R.id.textView1)
        val tvDescription = findViewById<TextView>(R.id.textView9)
        val tvPrice = findViewById<TextView>(R.id.tvPrice)
        val btnAddToPlan = findViewById<Button>(R.id.button)
        val backButton = findViewById<Button>(R.id.backButton)

        // Set the data to the views
        if (imageRes != 0) {
            ivPlace.setImageResource(imageRes)
        }
        tvTitle.text = locationName
        tvRating.text = "Ratings: 4.8"  // Replace with actual rating if available
        tvDescription.text = description
        tvPrice.text = "Price: $$price"

        // Back button click returns to previous screen
        backButton.setOnClickListener {
            finish()
        }

        // When "Add to plan" is clicked, record a purchase/order in the database
        btnAddToPlan.setOnClickListener {
            // Retrieve the current user's key from SharedPreferences
            val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
            val userKeyCurrent = sharedPref.getString("USER_KEY", "") ?: ""
            if (userKeyCurrent.isEmpty()) {
                Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Create an order map with the purchase details
            val orderData = mapOf(
                "userKey" to userKeyCurrent,
                "placeId" to placeId,
                "price" to price,
                "timestamp" to System.currentTimeMillis(),
                "status" to "purchased"
            )

            // Write the order record to Firebase under a new node "orders"
            val database = FirebaseDatabase.getInstance("https://madproj-d74a3-default-rtdb.asia-southeast1.firebasedatabase.app/")
            val ordersRef = database.getReference("orders")
            ordersRef.push().setValue(orderData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Purchase recorded successfully", Toast.LENGTH_SHORT).show()
                    // Optionally, navigate to another page or update UI
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to record purchase: ${e.message}", Toast.LENGTH_SHORT).show()
                }
        }
    }
}
