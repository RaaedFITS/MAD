package com.example.madproject

import android.content.Intent
import android.os.Bundle
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
    private lateinit var userKey: String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_manage_hosting)
        FirebaseApp.initializeApp(this)

        // Set up the toolbar and collapsing toolbar
        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { onBackPressed() }
        val collapsingToolbar = findViewById<CollapsingToolbarLayout>(R.id.collapsingToolbar)
        collapsingToolbar.title = "Manage Hosting"

        // Retrieve the current user's key from the intent
        userKey = intent.getStringExtra("userKey") ?: ""
        if (userKey.isEmpty()) {
            Toast.makeText(this, "User key is missing", Toast.LENGTH_SHORT).show()
        }

        // Set up the Add New Listing button to navigate to host_landing_page
        val addListingButton = findViewById<Button>(R.id.addListingButton)
        addListingButton.setOnClickListener {
            val intent = Intent(this, host_landing_page::class.java)
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

    private fun loadUserPlaces() {
        val database = FirebaseDatabase.getInstance("https://madproj-d74a3-default-rtdb.asia-southeast1.firebasedatabase.app/")
        val placesRef = database.getReference("places")
        // Query for places where the "userKey" equals the current user's key
        val query = placesRef.orderByChild("userKey").equalTo(userKey)
        query.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val places = mutableListOf<Place>()
                for (child in snapshot.children) {
                    val place = child.getValue(Place::class.java)
                    if (place != null) {
                        places.add(place)
                    }
                }
                placeAdapter.updateData(places)
            }

            override fun onCancelled(error: DatabaseError) {
                Toast.makeText(this@ManageHostingActivity, "Failed to load places: ${error.message}", Toast.LENGTH_SHORT).show()
            }
        })
    }

    // Refresh the list when the activity resumes
    override fun onResume() {
        super.onResume()
        loadUserPlaces()
    }
}
