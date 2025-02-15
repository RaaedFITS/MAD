package com.example.madproject

import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*

class CartActivity : AppCompatActivity() {

    private lateinit var recyclerView: RecyclerView
    private lateinit var cartAdapter: CartAdapter
    private var orderList = mutableListOf<Order>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        supportActionBar?.title = ""  // This explicitly sets the ActionBar title to empty
        toolbar.setNavigationOnClickListener { onBackPressed() }


        // Initialize Firebase if not already done
        FirebaseApp.initializeApp(this)

        recyclerView = findViewById(R.id.recyclerViewCart)
        recyclerView.layoutManager = LinearLayoutManager(this)
        cartAdapter = CartAdapter(orderList)
        recyclerView.adapter = cartAdapter

        fetchOrdersForUser()
    }

    private fun fetchOrdersForUser() {
        // Retrieve the current user's key from SharedPreferences
        val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
        val userKey = sharedPref.getString("USER_KEY", "") ?: ""

        if (userKey.isEmpty()) {
            Toast.makeText(this, "User not logged in", Toast.LENGTH_SHORT).show()
            return
        }

        val database = FirebaseDatabase.getInstance("https://madproj-d74a3-default-rtdb.asia-southeast1.firebasedatabase.app/")
        val ordersRef = database.getReference("orders")
        // Query orders where "userKey" equals the current user's key
        val query = ordersRef.orderByChild("userKey").equalTo(userKey)

        query.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val orders = mutableListOf<Order>()
                for (child in snapshot.children) {
                    val order = child.getValue(Order::class.java)
                    if (order != null) {
                        orders.add(order.copy(orderId = child.key ?: ""))
                    }
                }
                orderList = orders
                cartAdapter.updateData(orderList)
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("CartActivity", "Error fetching orders: ${error.message}")
                Toast.makeText(this@CartActivity, "Failed to load orders", Toast.LENGTH_SHORT).show()
            }
        })
    }
}
