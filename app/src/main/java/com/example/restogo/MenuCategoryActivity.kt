package com.example.restogo

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MenuCategoryActivity : Activity(), View.OnClickListener {
    private lateinit var btnTambahKategori: Button
    private lateinit var btnKembali: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MenuCategoryAdapter
    private lateinit var requestQueue: com.android.volley.RequestQueue
    private val API_URL = Env.apiUrl
    private val categories = mutableListOf<MenuCategory>()

    private val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu_categories)
        initComponents()
        btnTambahKategori.setOnClickListener(this)
        btnKembali.setOnClickListener(this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        requestQueue = Volley.newRequestQueue(this)

        adapter = MenuCategoryAdapter(categories) { category ->
            showUpdateDeleteDialog(category)
        }
        recyclerView.adapter = adapter

        fetchCategories()
    }

    override fun onResume() {
        super.onResume()
        fetchCategories()
    }

    private fun initComponents() {
        btnTambahKategori = findViewById(R.id.btn_menu_category_add)
        btnKembali = findViewById(R.id.img_menu_categories_back)
        recyclerView = findViewById(R.id.rv_edit_menu_categories)
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
                    Toast.makeText(
                        this@MenuCategoryActivity,
                        "Failed to fetch categories",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showUpdateDeleteDialog(category: MenuCategory) {
        val options = arrayOf("Update kategori", "Hapus Kategori")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Kategori: ${category.name}")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> {
                        // Handle update category
                        val intent = Intent(this, UpdateMenuCategoryActivity::class.java).apply {
                            putExtra(UpdateMenuCategoryActivity.EXTRA_MENU_CATEGORY, category)
                        }
                        startActivity(intent)
                        finish()
                    }
                    1 -> {
                        // Handle delete category
                        deleteCategory(category._id) { categoryToRemove ->
                            if (categoryToRemove != null) {
                                categories.remove(categoryToRemove)
                                adapter.notifyDataSetChanged()
                                Toast.makeText(
                                    this@MenuCategoryActivity,
                                    "Berhasil menghapus kategori '${category.name}'",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    this@MenuCategoryActivity,
                                    "Gagal menghapus kategori '${category.name}'",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }
        val dialog = builder.create()
        dialog.show()
    }

    private fun deleteCategory(categoryId: String, callback: (MenuCategory?) -> Unit) {
        val url = "$API_URL/menu-categories/$categoryId"
        val request = object : JsonObjectRequest(
            Method.DELETE,
            url,
            null,
            { response ->
                Log.d("API_RESPONSE", response.toString())
                if (response.has("message") && response.getString("message") == "Berhasil menghapus kategori menu!") {
                    // Penghapusan kategori berhasil
                    val categoryToRemove = categories.find { it._id == categoryId }
                    callback(categoryToRemove)
                } else {
                    // Penghapusan kategori gagal
                    callback(null)
                }
            },
            { error ->
                Log.e("API_ERROR", error.toString())
                if (error.networkResponse?.statusCode == 404) {
                    // Kategori tidak ditemukan
                    Toast.makeText(
                        this@MenuCategoryActivity,
                        "Kategori tidak ditemukan",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(this, "Terjadi kesalahan!", Toast.LENGTH_SHORT).show()
                }
            }
        ) {
            @Throws(AuthFailureError::class)
            override fun getHeaders(): Map<String, String> {
                val headers = HashMap<String, String>()
                headers["Content-Type"] = "application/json"
                return headers
            }
        }

        requestQueue.add(request)
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.btn_menu_category_add) {
            val intent = Intent(this, AddMenuCategoryActivity::class.java)
            startActivityForResult(intent, 1) // Start AddMenuCategoryActivity

        }

        if (v?.id == R.id.img_menu_categories_back) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 || requestCode == 2) {
            if (resultCode == RESULT_OK) {
                fetchCategories() // Fetch categories when returning from add or update
            }
        }
    }
}
