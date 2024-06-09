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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class ExtraMenuActivity : Activity(), View.OnClickListener {
    private lateinit var btnTambahExtraMenu: Button
    private lateinit var btnKembali: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: ExtraMenuAdapter
    private lateinit var requestQueue: com.android.volley.RequestQueue
    private val API_URL = Env.apiUrl
    private val extraMenus = mutableListOf<ExtraMenu>()

    private val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_extra_menu)
        initComponents()

        btnTambahExtraMenu.setOnClickListener(this)
        btnKembali.setOnClickListener(this)
        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = ExtraMenuAdapter(extraMenus) { extraMenu ->
            showUpdateDeleteDialog(extraMenu)
        }
        recyclerView.adapter = adapter

        fetchExtraMenus()
    }

    private fun initComponents() {
        btnTambahExtraMenu = findViewById(R.id.btn_extra_menu_activity_add)
        btnKembali = findViewById(R.id.img_extra_menu_activity_back)
        recyclerView = findViewById(R.id.rv_extra_menu_activity)

    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.btn_extra_menu_activity_add) {
            val intent = Intent(this, AddExtraMenusActivity::class.java)
            startActivity(intent)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun fetchExtraMenus() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.getExtraMenus()
                Log.i("infoApk", response.data.toString())
                withContext(Dispatchers.Main) {
                    extraMenus.clear()
                    extraMenus.addAll(response.data)
                    adapter.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@ExtraMenuActivity,
                        "Failed to fetch extra menus",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showUpdateDeleteDialog(extraMenu: ExtraMenu) {
        val options = arrayOf("Update extra menu", "Hapus extra menu")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Menu: ${extraMenu.name}")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> {
                        // Handle update menu
                        val intent = Intent(this, UpdateMenuActivity::class.java).apply {
                            putExtra(UpdateMenuActivity.EXTRA_MENU, extraMenu)
                        }
                        startActivity(intent)
                        finish()
                    }

                    1 -> {
                        // Handle delete menu
                        deleteExtraMenu(extraMenu._id) { extraMenuToRemove ->
                            if (extraMenuToRemove != null) {
                                extraMenus.remove(extraMenuToRemove)
                                adapter.notifyDataSetChanged()
                                Toast.makeText(
                                    this@ExtraMenuActivity,
                                    "Berhasil menghapus extra menu '${extraMenu.name}'!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    this@ExtraMenuActivity,
                                    "Gagal menghapus extra menu '${extraMenu.name}'!",
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

    private fun deleteExtraMenu(extraMenuId: String, callback: (ExtraMenu?) -> Unit) {
        val url = "$API_URL/extra-menus/$extraMenuId"
        val request = object : JsonObjectRequest(
            Method.DELETE,
            url,
            null,
            { response ->
                Log.d("API_RESPONSE", response.toString())
                if (response.has("message") && response.getString("message") == "Berhasil menghapus extra menu!") {
                    // Penghapusan menu berhasil
                    val extraMenuToRemove = extraMenus.find { it._id == extraMenuId }
                    callback(extraMenuToRemove)
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
                        this@ExtraMenuActivity,
                        "Extra menu tidak ditemukan",
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
}
