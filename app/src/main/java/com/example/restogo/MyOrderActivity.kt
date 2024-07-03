package com.example.restogo

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ImageView

class MyOrdersActivity : Activity() {
    private lateinit var btnBack: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_orders)
        initComponents()
    }

    private fun initComponents() {
        btnBack = findViewById(R.id.img_my_order_activity_back)
    }
}