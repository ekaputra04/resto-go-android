package com.example.restogo

import android.app.Activity
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.*
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.restogo.model.ApiService
import com.example.restogo.model.Menu
import com.example.restogo.model.MenuCategory
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class UpdateMenuActivity : Activity(), View.OnClickListener {

    companion object {
        const val EXTRA_MENU: String = "extra_menu"
    }

    private lateinit var edtName: EditText
    private lateinit var edtPrice: EditText
    private lateinit var spinnerMenuCategories: Spinner
    private lateinit var btnSimpan: Button
    private lateinit var btnBack: ImageView
    private lateinit var requestQueue: RequestQueue
    private val API_URL = Env.apiUrl
    private lateinit var menu: Menu
    private lateinit var adapter: ArrayAdapter<String>
    private val categories = mutableListOf<MenuCategory>()
    private lateinit var name: String
    private lateinit var category: String
    private lateinit var price: String

    private val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_menu)
        initComponents()

        menu = if (Build.VERSION.SDK_INT >= 33) {
            intent?.getParcelableExtra(EXTRA_MENU, Menu::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent?.getParcelableExtra(EXTRA_MENU)
        }!!

        edtName.setText(menu.name)
        edtPrice.setText(menu.price.toString())
        category = menu.category

        btnSimpan.setOnClickListener(this)
        btnBack.setOnClickListener(this)
    }

    private fun initComponents() {
        edtName = findViewById(R.id.edt_update_menu_activity_name)
        edtPrice = findViewById(R.id.edt_update_menu_activity_price)
        spinnerMenuCategories = findViewById(R.id.spinner_update_menu_activity_category)
        btnSimpan = findViewById(R.id.btn_update_menu_activity_simpan)
        btnBack = findViewById(R.id.img_update_menu_activity_back)
        requestQueue = Volley.newRequestQueue(this)
        fetchMenuCategories()
    }

    private fun updateMenu(
        menuId: String,
        name: String,
        price: Int,
        category: String,
        url_image: String? = null
    ) {
        val url = "$API_URL/menus/$menuId"
        val params = JSONObject().apply {
            put("name", name)
            put("price", price)
            put("category", category)
            put("url_image", url_image)
        }

        val request = object : JsonObjectRequest(
            Method.PUT,
            url,
            params,
            { response ->
                Log.d("API_RESPONSE", response.toString())
                if (response.has("message") && response.getString("message") == "Berhasil mengedit menu!") {
                    // Update category successfully
                    val updatedMenu = Menu(
                        _id = menuId,
                        name = response.getJSONObject("data").getString("name"),
                        price = response.getJSONObject("data").getInt("price"),
                        category = response.getJSONObject("data").getString("category"),
                        url_image = response.getJSONObject("data").getString("url_image"),
                    )
                    Toast.makeText(
                        this,
                        "Berhasil mengedit menu!",
                        Toast.LENGTH_SHORT
                    ).show()

                    val intent = Intent(this, MenuActivity::class.java).apply {
                        putExtra(EXTRA_MENU, updatedMenu)
                    }
                    setResult(RESULT_OK, intent)
                    finish()
                } else {
                    Toast.makeText(this, "Gagal mengupdate menu!", Toast.LENGTH_SHORT).show()
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

    private fun checkMenuExists(name: String, callback: (Boolean) -> Unit) {
        val url = "$API_URL/menus/name/$name"
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
        if (v?.id == R.id.btn_update_menu_category_simpan) {
            name = edtName.text.toString().trim()
            price = edtPrice.text.toString().trim()

            if (name.isEmpty()) {
                edtName.error = "Nama menu tidak boleh kosong!"
            }

            if (price.isEmpty()) {
                edtPrice.error = "Harga tidak boleh kosong!"
            }
            checkMenuExists(name) { exists ->
                if (exists) {
                    Toast.makeText(this, "Nama menu sudah terdaftar!", Toast.LENGTH_SHORT)
                        .show()
                    edtName.error = "Nama menu sudah terdaftar!"
                } else {
                    updateMenu(menu._id, name, price.toInt(), category)
                }
            }

        }
        if (v?.id == R.id.img_update_menu_activity_back) {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun fetchMenuCategories() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.getMenuCategories()
                withContext(Dispatchers.Main) {
                    categories.clear()
                    categories.addAll(response.data)
                    setupSpinner()
                    setSpinnerSelection()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@UpdateMenuActivity,
                        "Failed to fetch categories",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun setupSpinner() {
        // Buat adapter untuk spinner menggunakan kategori yang didapat dari API
        val categoryNames = categories.map { it.name }
        adapter = ArrayAdapter(
            this@UpdateMenuActivity,
            android.R.layout.simple_spinner_item,
            categoryNames
        )
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerMenuCategories.adapter = adapter

        // Tambahkan listener untuk spinner
        spinnerMenuCategories.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                // Handling ketika item dipilih
                val selectedCategory = categories[position]
                // Lakukan sesuatu dengan kategori yang dipilih
                Log.i("infoApk", selectedCategory.name)
                category = selectedCategory._id
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handling ketika tidak ada item yang dipilih
            }
        }
    }

    private fun setSpinnerSelection() {
        // Cari posisi dari kategori yang sesuai dengan menu.category
        val position = categories.indexOfFirst { it._id == menu.category }
        if (position != -1) {
            spinnerMenuCategories.setSelection(position)
        }
    }
}
