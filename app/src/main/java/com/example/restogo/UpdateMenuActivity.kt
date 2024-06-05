package com.example.restogo

import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class UpdateMenuActivity : Activity() {

    companion object {
        const val EXTRA_MENU: String = "extra_menu"
    }
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_menu)
    }
}