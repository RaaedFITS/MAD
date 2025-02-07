package com.example.madproject

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ClickableSpan
import android.view.View
import android.widget.Button
import android.widget.TextView

class dashboard_page : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dashboard_page)

        // when the see all button clicks it will move to vertical scroll dashboard
        val btn_see_all = findViewById<Button>(R.id.see_all_btn)
        btn_see_all.setOnClickListener {
            val v_scrollIntent = Intent(this, v_scroll_dash::class.java)
            startActivity(v_scrollIntent)
        }

    }
}