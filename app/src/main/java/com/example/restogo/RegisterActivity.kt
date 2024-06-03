package com.example.restogo

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject
import org.json.JSONArray

class RegisterActivity : Activity(), View.OnClickListener {
    private lateinit var edtNama: EditText
    private lateinit var edtTelepon: EditText
    private lateinit var btnKirim: Button
    private lateinit var tvLogin: TextView
    private lateinit var requestQueue: RequestQueue
    val API_URL = Env.apiUrl

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        initComponents()
        requestQueue = Volley.newRequestQueue(this)
        btnKirim.setOnClickListener(this)
        tvLogin.setOnClickListener(this)
    }

    private fun initComponents() {
        edtNama = findViewById(R.id.edt_register_nama)
        edtTelepon = findViewById(R.id.edt_register_telepon)
        btnKirim = findViewById(R.id.btn_register_kirim)
        tvLogin = findViewById(R.id.tv_register_login)
    }

    private fun checkUserExists(telepon: String, callback: (Boolean) -> Unit) {
        val url = "$API_URL/users/telephone/$telepon"
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
        if (v?.id == R.id.btn_register_kirim) {
            val nama = edtNama.text.toString().trim()
            val telepon = edtTelepon.text.toString().trim()

            if (nama.isEmpty()) {
                edtNama.error = "Masukkan nama!"
                return
            }

            if (telepon.isEmpty()) {
                edtTelepon.error = "Masukkan no telepon!"
                return
            }

            checkUserExists(telepon) { exists ->
                if (exists) {
                    Toast.makeText(this, "No telepon sudah terdaftar!", Toast.LENGTH_SHORT).show()
                    edtTelepon.error = "No telepon sudah terdaftar!"
                } else {
                    val requestBody = JSONObject().apply {
                        put("data", JSONArray().apply {
                            put(JSONObject().apply {
                                put("name", nama)
                                put("telephone", telepon)
                                put("isAdmin", false)
                            })
                        })
                    }

                    val registerRequest = JsonObjectRequest(
                        Request.Method.POST, "$API_URL/users", requestBody,
                        { response ->
                            Toast.makeText(this, "Registrasi berhasil!", Toast.LENGTH_SHORT).show()
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
                            Toast.makeText(this, "Gagal registrasi!", Toast.LENGTH_SHORT).show()
                        }
                    )
                    requestQueue.add(registerRequest)
                }
            }
        }

        if (v?.id == R.id.tv_register_login) {
            val intent = Intent(this, LoginActivity::class.java)
            startActivity(intent)
        }
    }
}
