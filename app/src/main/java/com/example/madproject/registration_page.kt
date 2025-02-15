package com.example.madproject

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.text.SpannableString
import android.text.Spanned
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.util.Base64
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.FirebaseApp
import com.google.firebase.database.FirebaseDatabase
import java.io.ByteArrayOutputStream

class registration_page : AppCompatActivity() {

    private lateinit var emailField: EditText
    private lateinit var usernameField: EditText
    private lateinit var passwordField: EditText

    private lateinit var profileImageView: ImageView
    private lateinit var uploadButton: Button
    private lateinit var signUpBtn: Button

    // Request code for picking an image from the gallery
    private val PICK_IMAGE_REQUEST = 1001

    // We’ll store the Base64-encoded string here after user picks an image
    private var selectedImageBase64: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_registration_page)

        // Initialize Firebase (configuration is read from google-services.json)
        FirebaseApp.initializeApp(this)

        // Get references to the EditText fields
        emailField = findViewById(R.id.editTextTextPassword)   // for Email
        usernameField = findViewById(R.id.editTextTextPassword2) // for Username
        passwordField = findViewById(R.id.editTextTextPassword3) // for Password

        // ImageView & Button for uploading a profile picture
        profileImageView = findViewById(R.id.imageView)
        uploadButton = findViewById(R.id.button4)

        // Button for sign-up
        signUpBtn = findViewById(R.id.sign_up_btn)

        // Set up an OnClickListener for "Upload" button to let user pick an image
        uploadButton.setOnClickListener {
            val intent = Intent(Intent.ACTION_PICK)
            intent.type = "image/*"
            startActivityForResult(intent, PICK_IMAGE_REQUEST)
        }

        // Use your Firebase Database URL
        val database = FirebaseDatabase.getInstance("https://madproj-d74a3-default-rtdb.asia-southeast1.firebasedatabase.app/")
        val usersRef = database.getReference("users")

        signUpBtn.setOnClickListener {
            Toast.makeText(this, "Signing up...", Toast.LENGTH_SHORT).show()

            val email = emailField.text.toString().trim()
            val username = usernameField.text.toString().trim()
            val password = passwordField.text.toString().trim()

            // Basic input validation
            if (email.isEmpty() || username.isEmpty() || password.isEmpty()) {
                Toast.makeText(this, "Please fill in all fields", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            // Create a new user node (push generates a unique key)
            val newUserRef = usersRef.push()

            // Prepare user data to save; role is set to "customer"
            val userMap = mutableMapOf<String, Any>(
                "email" to email,
                "username" to username,
                "password" to password,   // WARNING: Plain-text for demo!
                "role" to "customer"
            )

            // If the user selected an image, add it to the map
            selectedImageBase64?.let { base64Image ->
                userMap["profileImage"] = base64Image
            }

            newUserRef.setValue(userMap)
                .addOnSuccessListener {
                    Toast.makeText(this, "Registration successful", Toast.LENGTH_SHORT).show()

                    // After registration, "log in" the user by saving their info in SharedPreferences
                    val sharedPref = getSharedPreferences("MyAppPrefs", MODE_PRIVATE)
                    sharedPref.edit().apply {
                        putString("USER_KEY", newUserRef.key)  // Save the generated key
                        putString("USER_NAME", username)
                        putString("USER_ROLE", "customer")
                        // Optionally, you can also save email and profileImage if needed:
                        putString("USER_EMAIL", email)
                        selectedImageBase64?.let { putString("USER_PROFILE_IMAGE", it) }
                        apply()
                    }

                    // Navigate to Dashboard (login flow is complete)
                    val dashboardIntent = Intent(this, dashboard_page::class.java)
                    // You can pass extras if needed, though dashboard can read from SharedPreferences.
                    dashboardIntent.putExtra("username", username)
                    dashboardIntent.putExtra("userKey", newUserRef.key)
                    startActivity(dashboardIntent)
                    finish()
                }
                .addOnFailureListener { e ->
                    Toast.makeText(this, "Failed to save user data: ${e.message}", Toast.LENGTH_LONG).show()
                }
        }

        // Set up the "Already have an account? Login" clickable text
        val signInLink = findViewById<TextView>(R.id.sign_in_link)
        val linkText = "Already have an account? Login"
        val spannableString = SpannableString(linkText)
        val startIndex = linkText.indexOf("Login")
        val endIndex = startIndex + "Login".length

        val clickableSpan = object : ClickableSpan() {
            override fun onClick(widget: View) {
                val intent = Intent(this@registration_page, login_page::class.java)
                startActivity(intent)
            }
        }

        spannableString.setSpan(UnderlineSpan(), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        val linkColor = ContextCompat.getColor(this, R.color.dark_blue)
        spannableString.setSpan(ForegroundColorSpan(linkColor), startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        spannableString.setSpan(clickableSpan, startIndex, endIndex, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        signInLink.text = spannableString
        signInLink.movementMethod = LinkMovementMethod.getInstance()
    }

    // Called when user picks an image from the gallery
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == PICK_IMAGE_REQUEST && resultCode == RESULT_OK && data != null) {
            val imageUri: Uri? = data.data
            if (imageUri != null) {
                // Show the selected image in the ImageView (preview)
                profileImageView.setImageURI(imageUri)

                // Convert the selected image to a Bitmap, then to Base64 string
                val bitmap = uriToBitmap(imageUri)
                if (bitmap != null) {
                    selectedImageBase64 = bitmapToBase64(bitmap)
                }
            }
        }
    }

    // Helper: Convert URI to Bitmap
    private fun uriToBitmap(uri: Uri): Bitmap? {
        return try {
            val inputStream = contentResolver.openInputStream(uri)
            BitmapFactory.decodeStream(inputStream)
        } catch (e: Exception) {
            e.printStackTrace()
            null
        }
    }

    // Helper: Convert Bitmap to Base64 string
    private fun bitmapToBase64(bitmap: Bitmap): String {
        val outputStream = ByteArrayOutputStream()
        // Compress the image (adjust quality and format as needed)
        bitmap.compress(Bitmap.CompressFormat.JPEG, 70, outputStream)
        val byteArray = outputStream.toByteArray()
        return Base64.encodeToString(byteArray, Base64.DEFAULT)
    }
}
