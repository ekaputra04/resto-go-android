package com.example.restogo

import android.app.Activity
import android.os.Bundle
import android.widget.EditText
import com.android.volley.RequestQueue

class AddMenuCategoriesActivity : Activity() {
    private lateinit var edtNama: EditText
    private lateinit var requestQueue: RequestQueue
    private val API_URL = Env.apiUrl
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_menu_categories)
    }
}