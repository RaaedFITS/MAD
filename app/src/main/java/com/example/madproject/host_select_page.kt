package com.example.madproject

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class host_select_page : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_host_select_page)

        // Retrieve the current user's key (if needed)
        val userKey = intent.getStringExtra("userKey") ?: ""

        // Existing categories
        val cardHome = findViewById<CardView>(R.id.cardHome)
        cardHome.setOnClickListener {
            navigateToHostPageLast("Home", userKey)
        }

        val cardApartment = findViewById<CardView>(R.id.cardApartment)
        cardApartment.setOnClickListener {
            navigateToHostPageLast("Apartment", userKey)
        }

        val cardVehicle = findViewById<CardView>(R.id.cardVehicle)
        cardVehicle.setOnClickListener {
            navigateToHostPageLast("Vehicle", userKey)
        }

        val cardVan = findViewById<CardView>(R.id.cardVan)
        cardVan.setOnClickListener {
            navigateToHostPageLast("Campervan / RV", userKey)
        }

        // New categories
        val cardBeach = findViewById<CardView>(R.id.cardBeach)
        cardBeach.setOnClickListener {
            navigateToHostPageLast("Beaches", userKey)
        }

        val cardTemple = findViewById<CardView>(R.id.cardTemple)
        cardTemple.setOnClickListener {
            navigateToHostPageLast("Temples", userKey)
        }

        val cardRuins = findViewById<CardView>(R.id.cardRuins)
        cardRuins.setOnClickListener {
            navigateToHostPageLast("Ruins", userKey)
        }

        val cardForest = findViewById<CardView>(R.id.cardForest)
        cardForest.setOnClickListener {
            navigateToHostPageLast("Forest", userKey)
        }

        val cardMountain = findViewById<CardView>(R.id.cardMountain)
        cardMountain.setOnClickListener {
            navigateToHostPageLast("Mountain", userKey)
        }

        val cardVehicles = findViewById<CardView>(R.id.cardVehicles)
        cardVehicles.setOnClickListener {
            navigateToHostPageLast("Vehicles", userKey)
        }

        val cardGuestHouse = findViewById<CardView>(R.id.cardGuestHouse)
        cardGuestHouse.setOnClickListener {
            navigateToHostPageLast("Guest House", userKey)
        }
    }

    private fun navigateToHostPageLast(placeType: String, userKey: String) {
        val intent = Intent(this, host_page_last::class.java)
        intent.putExtra("placeType", placeType)
        intent.putExtra("userKey", userKey)
        startActivity(intent)
    }
}
