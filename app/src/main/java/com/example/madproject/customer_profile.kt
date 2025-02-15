package com.example.madproject

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.FirebaseApp
import com.google.firebase.database.*

class customer_profile : AppCompatActivity() {

    // Declare UI fields as properties
    private lateinit var editTextUsername: EditText
    private lateinit var editTextEmail: EditText
    private lateinit var editTextPassword: EditText
    private lateinit var editTextBankAccount: EditText
    private lateinit var editTextPayoutMethod: EditText
    private lateinit var editTextTextSize: EditText
    private lateinit var editTextContrast: EditText
    private lateinit var editTextHostDescription: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)



        FirebaseApp.initializeApp(this)
        setContentView(R.layout.activity_customer_profile)

        // Top user name TextView (in header)
        val userNameTextView = findViewById<TextView>(R.id.textViewUserName)

        // ------------------ Toggle Sections ------------------
        // Toggle Personal Information Section
        val headerPersonalInfo = findViewById<LinearLayout>(R.id.headerPersonalInfo)
        val contentPersonalInfo = findViewById<LinearLayout>(R.id.contentPersonalInfo)
        val arrowPersonalInfo = findViewById<ImageView>(R.id.arrowPersonalInfo)
        headerPersonalInfo.setOnClickListener {
            if (contentPersonalInfo.visibility == View.GONE) {
                contentPersonalInfo.visibility = View.VISIBLE
                arrowPersonalInfo.setImageResource(R.drawable.expand_less)
            } else {
                contentPersonalInfo.visibility = View.GONE
                arrowPersonalInfo.setImageResource(R.drawable.expand_more)
            }
        }

        // Toggle Login & Security Section
        val headerLoginSecurity = findViewById<LinearLayout>(R.id.headerLoginSecurity)
        val contentLoginSecurity = findViewById<LinearLayout>(R.id.contentLoginSecurity)
        val arrowLoginSecurity = findViewById<ImageView>(R.id.arrowLoginSecurity)
        headerLoginSecurity.setOnClickListener {
            if (contentLoginSecurity.visibility == View.GONE) {
                contentLoginSecurity.visibility = View.VISIBLE
                arrowLoginSecurity.setImageResource(R.drawable.expand_less)
            } else {
                contentLoginSecurity.visibility = View.GONE
                arrowLoginSecurity.setImageResource(R.drawable.expand_more)
            }
        }

        // Toggle Payment & Payouts Section
        val headerPaymentPayouts = findViewById<LinearLayout>(R.id.headerPaymentPayouts)
        val contentPaymentPayouts = findViewById<LinearLayout>(R.id.contentPaymentPayouts)
        val arrowPaymentPayouts = findViewById<ImageView>(R.id.arrowPaymentPayouts)
        headerPaymentPayouts.setOnClickListener {
            if (contentPaymentPayouts.visibility == View.GONE) {
                contentPaymentPayouts.visibility = View.VISIBLE
                arrowPaymentPayouts.setImageResource(R.drawable.expand_less)
            } else {
                contentPaymentPayouts.visibility = View.GONE
                arrowPaymentPayouts.setImageResource(R.drawable.expand_more)
            }
        }

        // Toggle Accessibility Section
        val headerAccessibility = findViewById<LinearLayout>(R.id.headerAccessibility)
        val contentAccessibility = findViewById<LinearLayout>(R.id.contentAccessibility)
        val arrowAccessibility = findViewById<ImageView>(R.id.arrowAccessibility)
        headerAccessibility.setOnClickListener {
            if (contentAccessibility.visibility == View.GONE) {
                contentAccessibility.visibility = View.VISIBLE
                arrowAccessibility.setImageResource(R.drawable.expand_less)
            } else {
                contentAccessibility.visibility = View.GONE
                arrowAccessibility.setImageResource(R.drawable.expand_more)
            }
        }

        // ------------------ User Type Section ------------------
        val radioGroupUserType = findViewById<RadioGroup>(R.id.radioGroupUserType)
        val radioCustomer = findViewById<RadioButton>(R.id.radioCustomer)
        val radioHost = findViewById<RadioButton>(R.id.radioHost)
        val hostDetailsLayout = findViewById<LinearLayout>(R.id.hostDetailsLayout)

        // ------------------ Initialize Input Fields ------------------
        editTextUsername = findViewById(R.id.editTextUsername)
        editTextEmail = findViewById(R.id.editTextEmail)
        editTextPassword = findViewById(R.id.editTextPassword)
        editTextBankAccount = findViewById(R.id.editTextBankAccount)
        editTextPayoutMethod = findViewById(R.id.editTextPayoutMethod)
        editTextTextSize = findViewById(R.id.editTextTextSize)
        editTextContrast = findViewById(R.id.editTextContrast)
        editTextHostDescription = findViewById(R.id.editTextHostDescription)

        // Get the userKey passed from login/registration (make sure this extra is sent)
        val userKey = intent.getStringExtra("userKey")

        // Initialize Firebase Database using your URL
        val database = FirebaseDatabase.getInstance("https://madproj-d74a3-default-rtdb.asia-southeast1.firebasedatabase.app/")
        val userRef = userKey?.let { database.getReference("users").child(it) }

        // ------------------ Populate Fields with Existing User Data ------------------
        if (userRef != null) {
            userRef.addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    if (snapshot.exists()) {
                        // Retrieve stored values (defaulting to empty string if missing)
                        val username = snapshot.child("username").getValue(String::class.java) ?: ""
                        val email = snapshot.child("email").getValue(String::class.java) ?: ""
                        val password = snapshot.child("password").getValue(String::class.java) ?: ""
                        val role = snapshot.child("role").getValue(String::class.java) ?: "customer"
                        val bankAccount = snapshot.child("bankAccount").getValue(String::class.java) ?: ""
                        val payoutMethod = snapshot.child("payoutMethod").getValue(String::class.java) ?: ""
                        val textSize = snapshot.child("textSize").getValue(String::class.java) ?: ""
                        val contrast = snapshot.child("contrast").getValue(String::class.java) ?: ""
                        val hostDescription = snapshot.child("hostDescription").getValue(String::class.java) ?: ""

                        // Populate the EditText fields
                        editTextUsername.setText(username)
                        editTextEmail.setText(email)
                        editTextPassword.setText(password)
                        editTextBankAccount.setText(bankAccount)
                        editTextPayoutMethod.setText(payoutMethod)
                        editTextTextSize.setText(textSize)
                        editTextContrast.setText(contrast)
                        editTextHostDescription.setText(hostDescription)

                        // Update the top header user name TextView
                        userNameTextView.text = username

                        // Update the radio group selection based on the stored role
                        if (role.toLowerCase() == "host") {
                            radioHost.isChecked = true
                            hostDetailsLayout.visibility = View.VISIBLE
                        } else {
                            radioCustomer.isChecked = true
                            hostDetailsLayout.visibility = View.GONE
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {
                    Toast.makeText(this@customer_profile, "Failed to load user data: ${error.message}", Toast.LENGTH_SHORT).show()
                }
            })
        }

        // Update role immediately when the radio group changes
        radioGroupUserType.setOnCheckedChangeListener { _, checkedId ->
            val selectedRadio = findViewById<RadioButton>(checkedId)
            val selectedRole = selectedRadio.text.toString().toLowerCase()  // "customer" or "host"
            if (selectedRole == "host") {
                hostDetailsLayout.visibility = View.VISIBLE
            } else {
                hostDetailsLayout.visibility = View.GONE
            }
            // Update the user's role in the database
            userRef?.child("role")?.setValue(selectedRole)
        }

        // ------------------ Save Changes Button ------------------
        val saveBtn = findViewById<Button>(R.id.save_btn)
        saveBtn.setOnClickListener {
            // Collect updated values from the fields
            val updatedUsername = editTextUsername.text.toString().trim()
            val updatedEmail = editTextEmail.text.toString().trim()
            val updatedPassword = editTextPassword.text.toString().trim()
            val updatedBankAccount = editTextBankAccount.text.toString().trim()
            val updatedPayoutMethod = editTextPayoutMethod.text.toString().trim()
            val updatedTextSize = editTextTextSize.text.toString().trim()
            val updatedContrast = editTextContrast.text.toString().trim()
            val updatedHostDescription = editTextHostDescription.text.toString().trim()

            // Create a map of the updates (only add non-empty fields)
            val updates = mutableMapOf<String, Any>()
            if (updatedUsername.isNotEmpty()) updates["username"] = updatedUsername
            if (updatedEmail.isNotEmpty()) updates["email"] = updatedEmail
            if (updatedPassword.isNotEmpty()) updates["password"] = updatedPassword
            if (updatedBankAccount.isNotEmpty()) updates["bankAccount"] = updatedBankAccount
            if (updatedPayoutMethod.isNotEmpty()) updates["payoutMethod"] = updatedPayoutMethod
            if (updatedTextSize.isNotEmpty()) updates["textSize"] = updatedTextSize
            if (updatedContrast.isNotEmpty()) updates["contrast"] = updatedContrast
            // Only update host description if the Host radio button is checked
            if (radioHost.isChecked && updatedHostDescription.isNotEmpty()) {
                updates["hostDescription"] = updatedHostDescription
            }

            // Update the user's record in the database if there are any changes
            if (userRef != null && updates.isNotEmpty()) {
                userRef.updateChildren(updates)
                    .addOnSuccessListener {
                        Toast.makeText(this, "Profile updated successfully", Toast.LENGTH_SHORT).show()
                    }
                    .addOnFailureListener { e ->
                        Toast.makeText(this, "Failed to update profile: ${e.message}", Toast.LENGTH_SHORT).show()
                    }
            } else {
                Toast.makeText(this, "No changes to update", Toast.LENGTH_SHORT).show()
            }
        }

        // ------------------ Host Button Navigation ------------------
        // Find the host-only button (make sure you have a Button with id "hostButton" in your layout)
        val hostButton = findViewById<Button>(R.id.hostButton)
        // Only show the button if the stored role is "host"
        userRef?.child("role")?.addListenerForSingleValueEvent(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val role = snapshot.getValue(String::class.java) ?: "customer"
                if (role.trim().toLowerCase() == "host") {
                    hostButton.visibility = View.VISIBLE
                } else {
                    hostButton.visibility = View.GONE
                }
            }
            override fun onCancelled(error: DatabaseError) {
                // Handle error if needed
            }
        })
        // Set up navigation: when the hostButton is clicked, navigate to the Manage Hosting page
        hostButton.setOnClickListener {
            val intent = Intent(this, ManageHostingActivity::class.java)
            // Pass along the userKey so the Manage Hosting page knows which host is logged in
            intent.putExtra("userKey", userKey)
            startActivity(intent)
        }

        val logoutButton = findViewById<Button>(R.id.logoutButton)
        logoutButton.setOnClickListener {
            // Clear the activity stack and navigate to the login page
            val intent = Intent(this, login_page::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
            startActivity(intent)
            finish()
        }

    }
}
