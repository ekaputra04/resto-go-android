package com.example.restogo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject

class AddMenuCategoryActivity : Activity(), View.OnClickListener {
    private lateinit var edtNama: EditText
    private lateinit var btnSimpan: Button
    private lateinit var btnKembali: ImageView
    private lateinit var requestQueue: RequestQueue
    private val API_URL = Env.apiUrl

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_menu_categories)
        initComponents()
        btnSimpan.setOnClickListener(this)
        btnKembali.setOnClickListener(this)
    }

    private fun initComponents() {
        edtNama = findViewById(R.id.edt_add_menu_category_nama)
        btnSimpan = findViewById(R.id.btn_add_menu_category_simpan)
        btnKembali = findViewById(R.id.img_add_menu_category_back)
        requestQueue = Volley.newRequestQueue(this)
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

    override fun onClick(v: View?) {
        if (v?.id == R.id.btn_add_menu_category_simpan) {
            val name = edtNama.text.toString().trim()

            if (name.isEmpty()) {
                edtNama.error = "Masukkan nama kategori!"
                return
            }

            checkMenuCategoryExists(name) { exists ->
                if (exists) {
                    Toast.makeText(this, "Nama kategori sudah terdaftar!", Toast.LENGTH_SHORT)
                        .show()
                    edtNama.error = "Nama kategori sudah terdaftar!"
                } else {
                    val requestBody = JSONObject().apply {
                        put("data", JSONArray().apply {
                            put(JSONObject().apply {
                                put("name", name)
                            })
                        })
                    }

                    val registerRequest = JsonObjectRequest(
                        Request.Method.POST, "$API_URL/menu-categories", requestBody,
                        { response ->
                            Toast.makeText(
                                this,
                                "Berhasil menambah kategori menu!",
                                Toast.LENGTH_SHORT
                            ).show()
                            finish()
                        },
                        { error ->
                            Log.e("API_ERROR", error.toString())
                            if (error.networkResponse != null) {
                                val statusCode = error.networkResponse.statusCode
                                val responseBody =
                                    String(error.networkResponse.data, Charsets.UTF_8)
                                Log.e(
                                    "API_ERROR",
                                    "Status Code: $statusCode\nResponse Body: $responseBody"
                                )
                            }
                            Toast.makeText(
                                this,
                                "Gagal menambah kategori menu!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    )
                    requestQueue.add(registerRequest)
                }
            }
        }

        if (v?.id == R.id.img_add_menu_category_back) {
            finish()
        }
    }
}
