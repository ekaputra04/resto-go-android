package com.example.restogo

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class UpdateMenuCategoryActivity : Activity(), View.OnClickListener {
    companion object {
        const val EXTRA_MENU_CATEGORY: String = "extra_menu_category"
    }

    private lateinit var edtNama: EditText
    private lateinit var btnSimpan: Button
    private lateinit var btnBack: ImageView
    private lateinit var requestQueue: RequestQueue
    private val API_URL = Env.apiUrl
    private lateinit var menuCategory: MenuCategory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_menu_category)
        initComponents()

        menuCategory = if (Build.VERSION.SDK_INT >= 33) {
            intent?.getParcelableExtra(EXTRA_MENU_CATEGORY, MenuCategory::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent?.getParcelableExtra(EXTRA_MENU_CATEGORY)
        }!!

        edtNama.setText(menuCategory.name)
        btnSimpan.setOnClickListener(this)
        btnBack.setOnClickListener(this)
    }

    private fun updateCategory(categoryId: String, name: String) {
        val url = "$API_URL/menu-categories/$categoryId"
        val params = JSONObject().apply {
            put("name", name)
        }

        val request = object : JsonObjectRequest(
            Method.PUT,
            url,
            params,
            { response ->
                Log.d("API_RESPONSE", response.toString())
                if (response.has("message") && response.getString("message") == "Berhasil mengedit kategori menu!") {
                    Toast.makeText(this, "Berhasil mengupdate kategori menu!", Toast.LENGTH_SHORT)
                        .show()
                    val intent = Intent(this, MenuCategoryActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Gagal mengupdate kategori menu!", Toast.LENGTH_SHORT)
                        .show()
                }
            },
            { error ->
                Log.e("API_ERROR", error.toString())
                Toast.makeText(this, "Terjadi kesalahan!", Toast.LENGTH_SHORT).show()
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

    private fun checkMenuCategoryExists(name: String, callback: (Boolean) -> Unit) {
        val url = "$API_URL/menu-categories/name/$name"
        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                Log.d("API_RESPONSE", response.toString())
                if (response.has("data")) {
                    callback(true)
                } else {
                    callback(false)
                }
            },
            { error ->
                Log.e("API_ERROR", error.toString())
                if (error.networkResponse?.statusCode == 404) {
                    callback(false)
                } else {
                    Toast.makeText(this, "Terjadi kesalahan!", Toast.LENGTH_SHORT).show()
                    callback(false)
                }
            }
        )
        requestQueue.add(request)
    }

    private fun initComponents() {
        edtNama = findViewById(R.id.edt_edit_menu_category_nama)
        btnSimpan = findViewById(R.id.btn_update_menu_category_simpan)
        btnBack = findViewById(R.id.img_update_menu_category_back)
        requestQueue = Volley.newRequestQueue(this)
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.btn_update_menu_category_simpan) {
            val name = edtNama.text.toString().trim()

            if (name.isEmpty()) {
                edtNama.error = "Nama kategori tidak boleh kosong!"
            } else {
                checkMenuCategoryExists(name) { exists ->
                    if (exists) {
                        Toast.makeText(this, "Nama kategori sudah terdaftar!", Toast.LENGTH_SHORT)
                            .show()
                        edtNama.error = "Nama kategori sudah terdaftar!"
                    } else {
                        updateCategory(menuCategory._id, name)
                    }
                }
            }
        }

        if (v?.id == R.id.img_update_menu_category_back) {
            val intent = Intent(this, MenuCategoryActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
