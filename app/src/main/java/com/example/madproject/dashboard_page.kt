package com.example.madproject

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class dashboard_page : AppCompatActivity() {

    private lateinit var rvCategories: RecyclerView
    private lateinit var rvPlaces: RecyclerView

    // Static list of categories for the categories RecyclerView
    private val categoryList = listOf(
        CategoryItem("Beaches", R.drawable.beachicon),
        CategoryItem("Temples", R.drawable.temple),
        CategoryItem("Ruins", R.drawable.ruine),
        CategoryItem("Forest", R.drawable.forest),
        CategoryItem("Mountain", R.drawable.mountain),
        CategoryItem("Vehicles", R.drawable.vehicle),
        CategoryItem("Guest house", R.drawable.home)
    )

    // We'll store fetched places here
    private var travelLocations = listOf<TravelLocation>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard_page)

        // Retrieve user info from Intent
        val username = intent.getStringExtra("username") ?: "User"
        val userKey = intent.getStringExtra("userKey") ?: ""
        val userNameTextView = findViewById<TextView>(R.id.textView20)
        userNameTextView.text = username

        // Profile card + username text -> customer_profile
        val profileCard = findViewById<CardView>(R.id.profile_card)
        profileCard.setOnClickListener {
            val intent = Intent(this, customer_profile::class.java)
            intent.putExtra("userKey", userKey)
            startActivity(intent)
        }
        userNameTextView.setOnClickListener {
            val intent = Intent(this, customer_profile::class.java)
            intent.putExtra("userKey", userKey)
            startActivity(intent)
        }

        // 1) Setup Categories RecyclerView (horizontal)
        rvCategories = findViewById(R.id.rvCategories)
        rvCategories.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        val categoryAdapter = CategoryAdapter(categoryList) { categoryClicked ->
            filterByCategory(categoryClicked.name)
        }
        rvCategories.adapter = categoryAdapter

        // 2) Setup Places RecyclerView (horizontal)
        rvPlaces = findViewById(R.id.rvPlaces)
        rvPlaces.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)

        // Start with an empty adapter; we’ll populate it after fetching from Firebase
        rvPlaces.adapter = TravelLocationAdapter(travelLocations)

        // 3) Setup "See all" button to remove any filter
        val btnSeeAll = findViewById<Button>(R.id.see_all_btn)
        btnSeeAll.setOnClickListener {
            // Reset the adapter to show the full list
            rvPlaces.adapter = TravelLocationAdapter(travelLocations)
        }

        // 4) Setup searching (filters as user types)
        val searchView = findViewById<androidx.appcompat.widget.SearchView>(R.id.searchView)
        searchView.setOnQueryTextListener(object : androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                filterPlaces(newText ?: "")
                return true
            }
        })

        // Finally, fetch places from Firebase
        fetchPlacesFromFirebase()
    }

    /**
     * Fetch the list of places from Firebase Realtime Database,
     * then update our RecyclerView adapter when done.
     */
    private fun fetchPlacesFromFirebase() {
        val database = FirebaseDatabase.getInstance(
            "https://madproj-d74a3-default-rtdb.asia-southeast1.firebasedatabase.app/"
        )
        val placesRef = database.getReference("places")

        placesRef.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val newList = mutableListOf<TravelLocation>()

                for (child in snapshot.children) {
                    // Read DB fields
                    val placeType = child.child("placeType").getValue(String::class.java) ?: ""
                    val locationName = child.child("location").getValue(String::class.java) ?: ""
                    val description = child.child("description").getValue(String::class.java) ?: ""

                    // Instead of category-based logic, just pick a random image
                    val imageRes = randomImages.random()

                    // Build a TravelLocation with rating = 0.0 or whatever default you like
                    val travelLocation = TravelLocation(
                        id = child.key ?: "",
                        category = "",          // or placeType, if you want
                        locationName = locationName,
                        rating = 0.0,
                        description = description,
                        imageRes = imageRes
                    )
                    newList.add(travelLocation)
                }

                // Update travelLocations & the RecyclerView
                travelLocations = newList
                rvPlaces.adapter = TravelLocationAdapter(travelLocations)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error if needed
            }
        })
    }
    private val randomImages = listOf(
        R.drawable.mirissa1,
        R.drawable.ambuluwawa,
        R.drawable.sigiriya1,
        R.drawable.sinharaja,
        R.drawable.knuckles,
        R.drawable.vehicle,
        R.drawable.home,
        R.drawable.apartment,
        R.drawable.ninearch
        // ... add any others
    )


    private fun getStaticImageForCategory(category: String): Int {
        return when (category) {
            "Beaches" -> R.drawable.ninearch
            "Temples" -> R.drawable.knuckles
            "Ruins" -> R.drawable.sigiriya1
            "Forest" -> R.drawable.sinharaja
            "Mountain" -> R.drawable.knuckles
            "Vehicles" -> R.drawable.vehicle
            "Guest house" -> R.drawable.home
            "Apartment" -> R.drawable.apartment
            "Home" -> R.drawable.home
            else -> R.drawable.ic_launcher_foreground
        }
    }

    // Filter places by category name
    private fun filterByCategory(category: String) {
        val filtered = travelLocations.filter {
            it.category.equals(category, ignoreCase = true)
        }
        rvPlaces.adapter = TravelLocationAdapter(filtered)
    }

    // Filter places by search text (locationName or description)
    private fun filterPlaces(query: String) {
        val filtered = travelLocations.filter {
            it.locationName.contains(query, ignoreCase = true) ||
                    it.description.contains(query, ignoreCase = true)
        }
        rvPlaces.adapter = TravelLocationAdapter(filtered)
    }
}
