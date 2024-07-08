package com.example.restogo

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button

class SuccessOrderActivity : Activity(), View.OnClickListener {
    private lateinit var btnKembali: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_success_order)
        initComponents()

        btnKembali.setOnClickListener(this)
    }

    private fun initComponents() {
        btnKembali = findViewById(R.id.btn_success_activity)
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.btn_success_activity) {
            finish()
        }
    }
}