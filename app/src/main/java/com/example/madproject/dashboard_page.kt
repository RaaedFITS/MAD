package com.example.madproject

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.bottomnavigation.BottomNavigationView
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

        val bottomNav = findViewById<BottomNavigationView>(R.id.bottomNavigationView)
        bottomNav.setOnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.nav_home -> {
                    // Already on home; maybe refresh or do nothing
                    true
                }
                R.id.nav_cart -> {
                    // Navigate to Cart activity
                    val intent = Intent(this, CartActivity::class.java)
                    startActivity(intent)
                    true
                }
                R.id.nav_account -> {
                    // Navigate to Account activity (e.g., customer_profile)
                    val intent = Intent(this, customer_profile::class.java)
                    // Optionally pass userKey if needed
                    val userKey = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                        .getString("USER_KEY", "") ?: ""
                    intent.putExtra("userKey", userKey)
                    startActivity(intent)
                    true
                }
                else -> false
            }
        }
        // 1) Retrieve user info from SharedPreferences
        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val userName = sharedPref.getString("USER_NAME", "User") ?: "User"
        val userKey = sharedPref.getString("USER_KEY", "") ?: ""

        // 2) Display the user's name in the dashboard
        val userNameTextView = findViewById<TextView>(R.id.textView20)
        userNameTextView.text = userName

        // 3) Set up the profile card & textView to navigate to customer_profile
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

        // 4) Setup Categories RecyclerView (horizontal)
        rvCategories = findViewById(R.id.rvCategories)
        rvCategories.layoutManager = LinearLayoutManager(
            this, LinearLayoutManager.HORIZONTAL, false
        )
        val categoryAdapter = CategoryAdapter(categoryList) { categoryClicked ->
            filterByCategory(categoryClicked.name)
        }
        rvCategories.adapter = categoryAdapter

        // 5) Setup Places RecyclerView (horizontal)
        rvPlaces = findViewById(R.id.rvPlaces)
        rvPlaces.layoutManager = LinearLayoutManager(
            this, LinearLayoutManager.HORIZONTAL, false
        )
        rvPlaces.adapter = TravelLocationAdapter(travelLocations)

        // 6) Setup "See all" button to remove any filter
        val btnSeeAll = findViewById<Button>(R.id.see_all_btn)
        btnSeeAll.setOnClickListener {
            rvPlaces.adapter = TravelLocationAdapter(travelLocations)
        }

        // 7) Setup searching (filters as user types)
        val searchView = findViewById<androidx.appcompat.widget.SearchView>(R.id.searchView)
        searchView.setOnQueryTextListener(object :
            androidx.appcompat.widget.SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean = false
            override fun onQueryTextChange(newText: String?): Boolean {
                filterPlaces(newText ?: "")
                return true
            }
        })

        // 8) Finally, fetch places from Firebase
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
                var imageCounter = 0

                for (child in snapshot.children) {
                    // Read DB fields
                    val category = child.child("category").getValue(String::class.java) ?: ""
                    val locationName = child.child("location").getValue(String::class.java) ?: ""
                    val description = child.child("description").getValue(String::class.java) ?: ""
                    // Read the price as a Double. (If stored as a string, you might need to convert.)
                    val price = child.child("price").getValue(Double::class.java) ?: 0.0

                    // Assign the next image in the sequence
                    val imageRes = randomImages[imageCounter]
                    imageCounter++
                    if (imageCounter >= randomImages.size) {
                        imageCounter = 0
                    }

                    // Build a TravelLocation with the fields read from Firebase
                    val travelLocation = TravelLocation(
                        id = child.key ?: "",
                        category = category,
                        locationName = locationName,
                        rating = 0.0,
                        description = description,
                        imageRes = imageRes,
                        price = price
                    )
                    newList.add(travelLocation)
                }

                travelLocations = newList
                rvPlaces.adapter = TravelLocationAdapter(travelLocations)
            }

            override fun onCancelled(error: DatabaseError) {
                // Handle error if needed
            }
        })
    }


    // We'll rotate through these images for each place in sequence
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
    )

    // Filter places by category name (using the "category" field)
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
                    it.description.contains(query, ignoreCase = true) ||
                    it.category.contains(query, ignoreCase = true)
        }
        rvPlaces.adapter = TravelLocationAdapter(filtered)
    }


}
