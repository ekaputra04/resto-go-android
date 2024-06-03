package com.example.restogo

import android.annotation.SuppressLint
import android.app.Activity
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.RequestQueue
import com.android.volley.toolbox.Volley
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MenuCategoryActivity : Activity() {
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MenuCategoryAdapter
    private lateinit var requestQueue: RequestQueue
    private val categories = mutableListOf<MenuCategory>()
    private val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(Env.apiUrl)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_categories)
        recyclerView = findViewById(R.id.rv_edit_menu_categories)
        recyclerView.layoutManager = LinearLayoutManager(this)
        requestQueue = Volley.newRequestQueue(this)

        adapter = MenuCategoryAdapter(categories) { category ->
            showUpdateDeleteDialog(category)
        }
        recyclerView.adapter = adapter

        fetchCategories()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun fetchCategories() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.getMenuCategories()
                withContext(Dispatchers.Main) {
                    categories.clear()
                    categories.addAll(response.data)
                    adapter.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(this@MenuCategoryActivity, "Failed to fetch categories", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    private fun showUpdateDeleteDialog(category: MenuCategory) {
        val options = arrayOf("Update category", "Delete category")
        MaterialAlertDialogBuilder(this)
            .setTitle("Choose an option")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> {
                        // Handle update category
                        // Start UpdateCategoryActivity or show a dialog to update the category
                    }
                    1 -> {
                        // Handle delete category
                        // Start DeleteCategoryActivity or show a confirmation dialog to delete the category
                    }
                }
            }
            .show()
    }
}