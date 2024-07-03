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
import com.example.restogo.model.ApiService
import com.example.restogo.model.Menu
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MenuActivity : Activity(), View.OnClickListener {
    private lateinit var btnTambahMenu: Button
    private lateinit var btnKembali: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: MenuAdapter
    private lateinit var requestQueue: com.android.volley.RequestQueue
    private val API_URL = Env.apiUrl
    private val menus = mutableListOf<Menu>()

    private val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_menu)
        initComponents()
        btnTambahMenu.setOnClickListener(this)
        btnKembali.setOnClickListener(this)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = MenuAdapter(menus) { menu ->
            showUpdateDeleteDialog(menu)
        }
        recyclerView.adapter = adapter

        fetchMenus()
    }

    override fun onResume() {
        super.onResume()
        fetchMenus()
    }

    private fun initComponents() {
        btnTambahMenu = findViewById(R.id.btn_menu_activity_add)
        btnKembali = findViewById(R.id.img_menu_activity_back)
        recyclerView = findViewById(R.id.rv_menu_activity)
        requestQueue = Volley.newRequestQueue(this)
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun fetchMenus() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.getMenus()
                withContext(Dispatchers.Main) {
                    menus.clear()
                    menus.addAll(response.data)
                    adapter.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@MenuActivity,
                        "Failed to fetch menus",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showUpdateDeleteDialog(menu: Menu) {
        val options = arrayOf("Update menu", "Hapus menu")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Menu: ${menu.name}")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> {
                        // Handle update menu
                        val intent = Intent(this, UpdateMenuActivity::class.java).apply {
                            putExtra(UpdateMenuActivity.EXTRA_MENU, menu)
                        }
                        startActivity(intent)
                    }

                    1 -> {
                        // Handle delete menu
                        deleteMenu(menu._id) { menuToRemove ->
                            if (menuToRemove != null) {
                                menus.remove(menuToRemove)
                                adapter.notifyDataSetChanged()
                                Toast.makeText(
                                    this@MenuActivity,
                                    "Berhasil menghapus menu '${menu.name}'",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    this@MenuActivity,
                                    "Gagal menghapus menu '${menu.name}'",
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

    private fun deleteMenu(menuId: String, callback: (Menu?) -> Unit) {
        val url = "$API_URL/menus/$menuId"
        val request = object : JsonObjectRequest(
            Method.DELETE,
            url,
            null,
            { response ->
                Log.d("API_RESPONSE", response.toString())
                if (response.has("message") && response.getString("message") == "Berhasil menghapus menu!") {
                    // Penghapusan menu berhasil
                    val menuToRemove = menus.find { it._id == menuId }
                    callback(menuToRemove)
                } else {
                    // Penghapusan menu gagal
                    callback(null)
                }
            },
            { error ->
                Log.e("API_ERROR", error.toString())
                if (error.networkResponse?.statusCode == 404) {
                    // Menu tidak ditemukan
                    Toast.makeText(
                        this@MenuActivity,
                        "Menu tidak ditemukan",
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
        if (v?.id == R.id.btn_menu_activity_add) {
            val intent = Intent(this, AddMenuActivity::class.java)
            startActivityForResult(intent, 1)

        }

        if (v?.id == R.id.img_menu_activity_back) {
            finish()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 || requestCode == 2) {
            if (resultCode == RESULT_OK) {
                fetchMenus()             }
        }
    }
}
