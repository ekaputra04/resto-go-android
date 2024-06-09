package com.example.restogo

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject

class AddExtraMenusActivity : Activity(), View.OnClickListener {
    private lateinit var btnKembali: ImageView
    private lateinit var edtName: EditText
    private lateinit var edtPrice: EditText
    private lateinit var btnKirim: Button
    private lateinit var requestQueue: RequestQueue
    private lateinit var name: String
    private lateinit var price: String
    private val API_URL = Env.apiUrl

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_extra_menus)
        initComponents()

        btnKirim.setOnClickListener(this)
        btnKembali.setOnClickListener(this)
    }

    private fun initComponents() {
        btnKembali = findViewById(R.id.img_add_extra_menu_activity_back)
        edtName = findViewById(R.id.edt_add_extra_menu_activity_name)
        edtPrice = findViewById(R.id.edt_add_extra_menu_activity_price)
        btnKirim = findViewById(R.id.btn_add_extra_menu_simpan)
        requestQueue = Volley.newRequestQueue(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_add_extra_menu_simpan -> {
                name = edtName.text.toString().trim()
                price = edtPrice.text.toString().trim()

                if (name.isEmpty()) {
                    edtName.error = "Masukkan nama extra menu!"
                    return
                }

                if (price.isEmpty()) {
                    edtPrice.error = "Masukkan harga extra menu!"
                    return
                }

                val priceInt = price.toIntOrNull()
                if (priceInt == null) {
                    edtPrice.error = "Harga harus berupa angka!"
                    return
                }

                checkExtraMenuExists(name) { exists ->
                    if (exists) {
                        Toast.makeText(this, "Nama extra menu sudah terdaftar!", Toast.LENGTH_SHORT)
                            .show()
                        edtName.error = "Nama extra menu sudah terdaftar!"
                    } else {
                        val requestBody = JSONObject().apply {
                            put("data", JSONArray().apply {
                                put(JSONObject().apply {
                                    put("name", name)
                                    put("price", priceInt)
                                })
                            })
                        }

                        val registerRequest = JsonObjectRequest(
                            Request.Method.POST, "$API_URL/extra-menus", requestBody,
                            { response ->
                                Toast.makeText(
                                    this,
                                    "Berhasil menambah extra menu!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                val intent = Intent(this, ExtraMenuActivity::class.java)
                                startActivity(intent)
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
                                    "Gagal menambah extra menu!",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        )
                        requestQueue.add(registerRequest)
                    }
                }
            }

            R.id.img_add_extra_menu_activity_back -> {
                val intent = Intent(this, ExtraMenuActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun checkExtraMenuExists(name: String, callback: (Boolean) -> Unit) {
        val url = "$API_URL/extra-menus/name/$name"
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
}
