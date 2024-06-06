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
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONObject

class UpdateTelephoneUserActivity : Activity(), View.OnClickListener {

    private lateinit var btnBack: ImageView
    private lateinit var btnKirim: Button
    private lateinit var edtTelephone: EditText
    private lateinit var requestQueue: RequestQueue
    private lateinit var user: User
    private lateinit var telephoneInput: String
    private val API_URL = Env.apiUrl
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_telephone_user)
        initComponents()

        btnBack.setOnClickListener(this)
        btnKirim.setOnClickListener(this)

        val userFromPreferences = getUserFromPreferences(this)
        if (userFromPreferences != null) {
            user = userFromPreferences
            edtTelephone.setText(user.telephone)
        } else {
            Toast.makeText(this, "Failed to load user data", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun initComponents() {
        btnBack = findViewById(R.id.img_update_telephone_user_back)
        btnKirim = findViewById(R.id.btn_update_telephone_user)
        edtTelephone = findViewById(R.id.edt_update_telephone_user)
        requestQueue = Volley.newRequestQueue(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.img_update_telephone_user_back -> {
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
                finish()
            }

            R.id.btn_update_telephone_user -> {
                telephoneInput = edtTelephone.text.toString().trim()
                if (telephoneInput.isEmpty()) {
                    edtTelephone.error = "Nama pengguna tidak boleh kosong!"
                    Toast.makeText(this, "Nama pengguna tidak boleh kosong!", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    checkUserExists(telephoneInput) { exists ->
                        if (exists) {
                            Toast.makeText(
                                this,
                                "Nomor telephone sudah terdaftar!",
                                Toast.LENGTH_SHORT
                            )
                                .show()
                            edtTelephone.error = "Nomor telephone sudah terdaftar!"
                        } else {
                            updateUser(user._id, telephoneInput)
                        }
                    }
                }
            }
        }
    }

    private fun updateUser(userId: String, telephone: String) {
        val url = "$API_URL/users/telephone/$userId"
        val params = JSONObject().apply {
            put("telephone", telephone)
        }

        val request = object : JsonObjectRequest(
            Method.PUT,
            url,
            params,
            { response ->
                Log.d("API_RESPONSE", response.toString())
                if (response.has("message") && response.getString("message") == "Berhasil mengedit telephone user!") {
                    Toast.makeText(
                        this,
                        "Berhasil mengupdate nomor telephone pelanggan!",
                        Toast.LENGTH_SHORT
                    )
                        .show()

                    val updatedUser = User(userId, user.name, telephone, user.isAdmin)
                    saveUserToPreferences(updatedUser)

                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(
                        this,
                        "Gagal mengupdate nomor telephone pelanggan!",
                        Toast.LENGTH_SHORT
                    )
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
                return mapOf("Content-Type" to "application/json")
            }
        }

        requestQueue.add(request)
    }

    private fun getUserFromPreferences(context: Context): User? {
        val sharedPref =
            context.getSharedPreferences("USER_PREF", Context.MODE_PRIVATE) ?: return null
        val _id = sharedPref.getString("_id", null) ?: return null
        val name = sharedPref.getString("name", null) ?: return null
        val telephone = sharedPref.getString("telephone", null) ?: return null
        val isAdmin = sharedPref.getBoolean("isAdmin", false)
        return User(_id, name, telephone, isAdmin)
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
}