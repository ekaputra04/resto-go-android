package com.example.restogo

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.Volley
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : Activity(), View.OnClickListener {
    private lateinit var tvNameUser: TextView
    private lateinit var tvRoleUser: TextView
    private lateinit var tvSilahkanPilihMenu: TextView
    private lateinit var imgProfile: ImageView
    private lateinit var imgCart: ImageView
    private lateinit var recycleViewMenuCategories: RecyclerView
    private lateinit var adapter: HomeMenuCategoriesAdapter
    private lateinit var requestQueue: com.android.volley.RequestQueue
    private val API_URL = Env.apiUrl
    private val categories = mutableListOf<MenuCategory>()

    private val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    @SuppressLint("SetTextI18n")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initComponents()
        imgProfile.setOnClickListener(this)
        imgCart.setOnClickListener(this)

        val user = getUserFromPreferences(this)
        tvNameUser.text = user?.name ?: "User"
        tvSilahkanPilihMenu.text = "Hai ${user?.name ?: "User"}, Silahkan Pilih Menu"

        tvRoleUser.text = if (user?.isAdmin == true) "Admin" else "Pelanggan"

        // menu categories recycleview
        recycleViewMenuCategories.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        requestQueue = Volley.newRequestQueue(this)

        adapter = HomeMenuCategoriesAdapter(categories) { category ->
            Toast.makeText(this, "Berhasil klik: ${category.name}", Toast.LENGTH_SHORT).show()
        }
        recycleViewMenuCategories.adapter = adapter

        fetchCategories()
    }

    private fun initComponents() {
        tvNameUser = findViewById(R.id.tv_home_name)
        tvRoleUser = findViewById(R.id.tv_home_role)
        tvSilahkanPilihMenu = findViewById(R.id.tv_home_silahkan_pilih_menu)
        imgProfile = findViewById(R.id.img_home_profile)
        imgCart = findViewById(R.id.img_home_cart)
        recycleViewMenuCategories = findViewById(R.id.rv_home_menu_categories)
    }

    private fun getUserFromPreferences(context: Context): User? {
        val sharedPref = context.getSharedPreferences("USER_PREF", Context.MODE_PRIVATE)
        val _id = sharedPref.getString("_id", null)
        val name = sharedPref.getString("name", null)
        val telephone = sharedPref.getString("telephone", null)
        val isAdmin = sharedPref.getBoolean("isAdmin", false)
        return if (_id != null && name != null && telephone != null) {
            User(_id, name, telephone, isAdmin)
        } else {
            null
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.img_home_profile -> {
                val intent = Intent(this, ProfileActivity::class.java)
                startActivity(intent)
            }
            R.id.img_home_cart -> {
                val intent = Intent(this, CartActivity::class.java)
                startActivity(intent)
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun fetchCategories() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.getMenuCategories()
                withContext(Dispatchers.Main) {
                    categories.clear()
                    categories.addAll(response.data)
                    adapter.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@MainActivity,
                        "Failed to fetch categories",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }
}
