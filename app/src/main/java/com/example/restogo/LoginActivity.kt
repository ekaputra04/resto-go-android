package com.example.restogo

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.restogo.model.User

class LoginActivity : Activity(), View.OnClickListener {
    private lateinit var edtTelepon: EditText
    private lateinit var btnKirim: Button
    private lateinit var btnBack: ImageView
    private lateinit var tvDaftar: TextView
    private lateinit var requestQueue: RequestQueue
    private val API_URL = Env.apiUrl

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        initComponents()
        btnKirim.setOnClickListener(this)
        btnBack.setOnClickListener(this)
        tvDaftar.setOnClickListener(this)

        // Inisialisasi RequestQueue
        requestQueue = Volley.newRequestQueue(this)
    }

    private fun initComponents() {
        edtTelepon = findViewById(R.id.edt_login_telepon)
        btnKirim = findViewById(R.id.btn_login_kirim)
        btnBack = findViewById(R.id.img_login_back)
        tvDaftar = findViewById(R.id.tv_login_daftar)
    }

    private fun checkUserExists(telepon: String, callback: (Boolean) -> Unit) {
        val url = "$API_URL/users/telephone/$telepon"
        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                Log.d("API_RESPONSE", response.toString())
                if (response.has("data")) {
                    val userJson = response.getJSONObject("data")
                    val user = User(
                        userJson.getString("_id"),
                        userJson.getString("name"),
                        userJson.getString("telephone"),
                        userJson.getBoolean("isAdmin")
                    )
                    saveUserToPreferences(user)
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

    private fun saveUserToPreferences(user: User) {
        val sharedPref = getSharedPreferences("USER_PREF", Context.MODE_PRIVATE) ?: return
        with(sharedPref.edit()) {
            putString("_id", user._id)
            putString("name", user.name)
            putString("telephone", user.telephone)
            putBoolean("isAdmin", user.isAdmin)
            apply()
        }
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.btn_login_kirim) {
            val telepon = edtTelepon.text.toString().trim()

            if (telepon.isEmpty()) {
                edtTelepon.error = "Masukkan no telepon!"
                return
            }

            checkUserExists(telepon) { exists ->
                if (exists) {
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "No telepon belum terdaftar!", Toast.LENGTH_SHORT).show()
                    edtTelepon.error = "No telepon belum terdaftar!"
                }
            }
        }

        if (v?.id == R.id.img_login_back) {
            val intent = Intent(this, ChoiceRoleActivity::class.java)
            startActivity(intent)
            finish()
        }

        if (v?.id == R.id.tv_login_daftar) {
            val intent = Intent(this, RegisterActivity::class.java)
            startActivity(intent)
            finish()
        }
    }
}
