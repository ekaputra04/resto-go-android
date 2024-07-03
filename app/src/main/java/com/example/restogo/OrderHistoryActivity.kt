package com.example.restogo

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class OrderHistoryActivity : Activity() {
    private lateinit var btnBack: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_order_history)
        initComponents()
    }

    private fun initComponents() {
        btnBack = findViewById(R.id.img_order_history_activity_back)
    }
}