package com.example.madproject

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase

class host_page_last : AppCompatActivity() {

    // This will store the second-step place type (entire place / room / shared bed).
    private var selectedPlaceType: String = ""

    // This will store the category from the previous screen (Apartment, Home, Beaches, etc.).
    private var mainCategory: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_host_page_last)
        FirebaseApp.initializeApp(this)

        // Retrieve extras from the intent
        // "placeType" might contain something like "Home", "Apartment", "Beaches", etc.
        mainCategory = intent.getStringExtra("placeType") ?: ""
        val userKey = intent.getStringExtra("userKey") ?: ""

        // TextView to display the selection
        val textViewPlaceType = findViewById<TextView>(R.id.textView56)
        textViewPlaceType.text = "Category: $mainCategory\n(Select Entire Place / Room / Shared)"

        // Card references for the second-step place type
        val cardEntirePlace = findViewById<CardView>(R.id.cardEntirePlace)
        val cardRoom = findViewById<CardView>(R.id.cardRoom)
        val cardSharedBed = findViewById<CardView>(R.id.cardSharedBed)

        // Set up the click listeners to highlight selected card
        cardEntirePlace.setOnClickListener {
            selectedPlaceType = "An entire place"
            updateCardSelection(cardEntirePlace, cardRoom, cardSharedBed)
            textViewPlaceType.text = "Category: $mainCategory\nSelected Type: $selectedPlaceType"
        }

        cardRoom.setOnClickListener {
            selectedPlaceType = "A room"
            updateCardSelection(cardRoom, cardEntirePlace, cardSharedBed)
            textViewPlaceType.text = "Category: $mainCategory\nSelected Type: $selectedPlaceType"
        }

        cardSharedBed.setOnClickListener {
            selectedPlaceType = "A shared bed"
            updateCardSelection(cardSharedBed, cardEntirePlace, cardRoom)
            textViewPlaceType.text = "Category: $mainCategory\nSelected Type: $selectedPlaceType"
        }

        // EditText references for location and description
        val editTextLocation = findViewById<EditText>(R.id.editTextLocation)
        val editTextDescription = findViewById<EditText>(R.id.editTextDescription)

        // The "Save Place" button
        val savePlaceButton = findViewById<Button>(R.id.savePlaceButton)
        savePlaceButton.setOnClickListener {
            val location = editTextLocation.text.toString().trim()
            val description = editTextDescription.text.toString().trim()

            // Check for empty fields
            if (mainCategory.isEmpty() || selectedPlaceType.isEmpty()
                || location.isEmpty() || description.isEmpty()) {
                Toast.makeText(
                    this,
                    "Please select category and place type, and enter location & description.",
                    Toast.LENGTH_SHORT
                ).show()
                return@setOnClickListener
            }

            // Firebase Database reference
            val database = FirebaseDatabase.getInstance(
                "https://madproj-d74a3-default-rtdb.asia-southeast1.firebasedatabase.app/"
            )
            val placesRef = database.getReference("places")
            val newPlaceRef = placesRef.push() // unique key

            // Data to be saved
            val placeData = mapOf(
                "userKey" to userKey,
                "category" to mainCategory,        // from the first screen
                "placeType" to selectedPlaceType,  // from this screen
                "location" to location,
                "description" to description,
                "timestamp" to System.currentTimeMillis()
            )

            // Save the new place
            newPlaceRef.setValue(placeData)
                .addOnSuccessListener {
                    Toast.makeText(this, "Place created successfully", Toast.LENGTH_SHORT).show()
                    // Navigate to ManageHostingActivity (or anywhere you want)
                    val intent = Intent(this, ManageHostingActivity::class.java)
                    intent.putExtra("userKey", userKey)
                    startActivity(intent)
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(
                        this,
                        "Failed to create place: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
        }
    }

    /**
     * Highlights the selected CardView and resets the others to default color.
     */
    private fun updateCardSelection(selected: CardView, vararg others: CardView) {
        // Highlight the selected card
        selected.setCardBackgroundColor(
            ContextCompat.getColor(this, R.color.highlighted_color)
        )
        // Un-highlight the others
        for (other in others) {
            other.setCardBackgroundColor(
                ContextCompat.getColor(this, R.color.offwhite)
            )
        }
    }

}
