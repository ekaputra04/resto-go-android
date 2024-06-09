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
import com.example.restogo.model.ExtraMenu
import org.json.JSONObject

class UpdateExtraMenusActivity : Activity(), View.OnClickListener {
    private lateinit var btnBack: ImageView
    private lateinit var btnSimpan: Button
    private lateinit var edtName: EditText
    private lateinit var edtPrice: EditText
    private lateinit var requestQueue: RequestQueue
    private val API_URL = Env.apiUrl
    private lateinit var extraMenu: ExtraMenu

    companion object {
        const val EXTRA_MENU: String = "extra_menu"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_extra_menus)
        initComponents()

        extraMenu = if (Build.VERSION.SDK_INT >= 33) {
            intent?.getParcelableExtra(EXTRA_MENU, ExtraMenu::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent?.getParcelableExtra(EXTRA_MENU)
        }!!

        edtName.setText(extraMenu.name)
        edtPrice.setText(extraMenu.price.toString())
        btnSimpan.setOnClickListener(this)
        btnBack.setOnClickListener(this)
    }

    private fun initComponents() {
        btnBack = findViewById(R.id.img_update_extra_menu_activity_back)
        btnSimpan = findViewById(R.id.btn_update_extra_menu_simpan)
        edtName = findViewById(R.id.edt_update_extra_menu_activity_name)
        edtPrice = findViewById(R.id.edt_update_extra_menu_activity_price)
        requestQueue = Volley.newRequestQueue(this)
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.img_update_extra_menu_activity_back) {
            val intent = Intent(this, ExtraMenuActivity::class.java)
            startActivity(intent)
            finish()
        }

        if (v?.id == R.id.btn_update_extra_menu_simpan) {
            val name = edtName.text.toString().trim()
            val price = edtPrice.text.toString().trim()

            if (name.isEmpty()) {
                edtName.error = "Nama extra menu tidak boleh kosong!"
            }

            if (price.isEmpty()) {
                edtPrice.error = "Harga extra menu tidak boleh kosong!"
            }

            checkExtraMenuExists(name) { exists ->
                if (exists) {
                    Toast.makeText(this, "Nama kategori sudah terdaftar!", Toast.LENGTH_SHORT)
                        .show()
                    edtName.error = "Nama kategori sudah terdaftar!"
                } else {
                    updateExtraMenu(extraMenu._id, name, price.toInt())
                }
            }
        }
    }

    private fun updateExtraMenu(extraMenuId: String, name: String, price: Int) {
        val url = "$API_URL/extra-menus/$extraMenuId"
        val params = JSONObject().apply {
            put("name", name)
            put("price", price)
        }

        val request = object : JsonObjectRequest(
            Method.PUT,
            url,
            params,
            { response ->
                Log.d("API_RESPONSE", response.toString())
                if (response.has("message") && response.getString("message") == "Berhasil mengedit extra menu!") {
                    Toast.makeText(this, "Berhasil mengupdate extra menu!", Toast.LENGTH_SHORT)
                        .show()
                    val intent = Intent(this, ExtraMenuActivity::class.java)
                    startActivity(intent)
                    finish()
                } else {
                    Toast.makeText(this, "Gagal mengupdate extra menu!", Toast.LENGTH_SHORT)
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