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
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.restogo.model.User
import org.json.JSONObject

class UpdateNameUserActivity : Activity(), View.OnClickListener {

    private lateinit var btnBack: ImageView
    private lateinit var btnKirim: Button
    private lateinit var edtName: EditText
    private lateinit var requestQueue: RequestQueue
    private lateinit var user: User
    private lateinit var nameInput: String
    private val API_URL = Env.apiUrl

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_name_user)
        initComponents()

        btnBack.setOnClickListener(this)
        btnKirim.setOnClickListener(this)

        val userFromPreferences = getUserFromPreferences(this)
        if (userFromPreferences != null) {
            user = userFromPreferences
            edtName.setText(user.name)
        } else {
            Toast.makeText(this, "Failed to load user data", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun initComponents() {
        btnBack = findViewById(R.id.img_update_name_user_back)
        btnKirim = findViewById(R.id.btn_update_name_user)
        edtName = findViewById(R.id.edt_update_name_user)
        requestQueue = Volley.newRequestQueue(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.img_update_name_user_back -> {
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
                finish()
            }
            R.id.btn_update_name_user -> {
                nameInput = edtName.text.toString().trim()
                if (nameInput.isEmpty()) {
                    edtName.error = "Nama pengguna tidak boleh kosong!"
                    Toast.makeText(this, "Nama pengguna tidak boleh kosong!", Toast.LENGTH_SHORT).show()
                } else {
                    updateUser(user._id, nameInput)
                }
            }
        }
    }

    private fun updateUser(userId: String, name: String) {
        val url = "$API_URL/users/name/$userId"
        val params = JSONObject().apply {
            put("name", name)
        }

        val request = object : JsonObjectRequest(
            Method.PUT,
            url,
            params,
            { response ->
                Log.d("API_RESPONSE", response.toString())
                if (response.has("message") && response.getString("message") == "Berhasil mengedit nama user!") {
                    Toast.makeText(this, "Berhasil mengupdate nama pelanggan!", Toast.LENGTH_SHORT).show()

                    val updatedUser = User(userId, name, user.telephone, user.isAdmin)
                    saveUserToPreferences(updatedUser)

                    val intent = Intent(this, ProfileActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Gagal mengupdate nama pelanggan!", Toast.LENGTH_SHORT).show()
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
        val sharedPref = context.getSharedPreferences("USER_PREF", Context.MODE_PRIVATE) ?: return null
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
}
