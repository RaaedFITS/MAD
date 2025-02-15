package com.example.madproject

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.appbar.CollapsingToolbarLayout
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*

class ManageHostingActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var placeAdapter: PlaceAdapter
    private val placeList = mutableListOf<Place>()

    // We'll store the user's unique key (passed from the login or host flow)
    private lateinit var userKey: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_hosting)

        // Initialize Firebase (if not already done in Application class)
        FirebaseApp.initializeApp(this)

        // Set up the toolbar and collapsing toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressed() }

        val collapsingToolbar = findViewById<CollapsingToolbarLayout>(R.id.collapsingToolbar)
        collapsingToolbar.title = "Manage Hosting"

        // Retrieve the current user's key from the Intent
        userKey = intent.getStringExtra("userKey") ?: ""
        // Log it for debugging
        Log.d("MANAGE_HOSTING", "Received userKey: $userKey")

        // If userKey is empty, let the user know
        if (userKey.isEmpty()) {
            Toast.makeText(this, "User key is missing", Toast.LENGTH_SHORT).show()
        }

        // Set up the "Add New Listing" button to navigate to host_landing_page
        val addListingButton = findViewById<Button>(R.id.addListingButton)
        addListingButton.setOnClickListener {
            val intent = Intent(this, host_landing_page::class.java)
            // Pass the same userKey forward
            intent.putExtra("userKey", userKey)
            startActivity(intent)
        }

        // Initialize the RecyclerView and adapter
        recyclerView = findViewById(R.id.recyclerViewPlaces)
        recyclerView.layoutManager = LinearLayoutManager(this)
        placeAdapter = PlaceAdapter(placeList)
        recyclerView.adapter = placeAdapter

        // Load the user's places from Firebase
        loadUserPlaces()
    }

    /**
     * Queries Firebase for places where "userKey" matches the current user's key.
     * This ensures only that user's listings are shown.
     */
    private fun loadUserPlaces() {
        // Get reference to your Firebase Realtime Database
        val database = FirebaseDatabase.getInstance(
            "https://madproj-d74a3-default-rtdb.asia-southeast1.firebasedatabase.app/"
        )
        val placesRef = database.getReference("places")

        // Order by "userKey" and match exactly this user's key
        val query = placesRef.orderByChild("userKey").equalTo(userKey)

        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                // Log the raw snapshot to debug
                Log.d("MANAGE_HOSTING", "Snapshot exists: ${snapshot.exists()}")
                Log.d("MANAGE_HOSTING", "Snapshot value: ${snapshot.value}")

                if (snapshot.exists()) {
                    val places = mutableListOf<Place>()
                    for (child in snapshot.children) {
                        val place = child.getValue(Place::class.java)
                        if (place != null) {
                            places.add(place)
                        }
                    }
                    placeAdapter.updateData(places)
                } else {
                    // Means no places found for this userKey
                    Toast.makeText(
                        this@ManageHostingActivity,
                        "No places found for this user.",
                        Toast.LENGTH_SHORT
                    ).show()
                    placeAdapter.updateData(emptyList())
                }
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(
                    this@ManageHostingActivity,
                    "Failed to load places: ${error.message}",
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    /**
     * Reload places whenever we return to this screen, so that new data
     * is always reflected.
     */
    override fun onResume() {
        super.onResume()
        loadUserPlaces()
    }
}
