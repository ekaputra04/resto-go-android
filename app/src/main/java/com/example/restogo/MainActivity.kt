package com.example.restogo

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.Volley
import com.example.restogo.model.ApiService
import com.example.restogo.model.Menu
import com.example.restogo.model.MenuCategory
import com.example.restogo.model.OrderObject
import com.example.restogo.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MainActivity : Activity(), View.OnClickListener {
    private lateinit var tvNameUser: TextView
    private lateinit var tvRoleUser: TextView
    private lateinit var tvShowAllMenus: TextView
    private lateinit var tvSilahkanPilihMenu: TextView
    private lateinit var tvCountMenuInCart: TextView
    private lateinit var imgProfile: ImageView
    private lateinit var imgCart: ImageView
    private lateinit var imgShowAllMenus: ImageView
    private lateinit var edtSearchMenu: EditText
    private lateinit var btnSearchMenu: Button
    private lateinit var recycleViewMenuCategories: RecyclerView
    private lateinit var recycleViewMenus: RecyclerView
    private lateinit var adapterMenuCategories: HomeMenuCategoriesAdapter
    private lateinit var adapterMenus: HomeMenuAdapter
    private lateinit var requestQueue: com.android.volley.RequestQueue
    private val API_URL = Env.apiUrl
    private val categories = mutableListOf<MenuCategory>()
    private val menus = mutableListOf<Menu>()
    private var menusFromCategories = mutableListOf<Menu>()
    private var menusFromName = mutableListOf<Menu>()

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
        tvShowAllMenus.setOnClickListener(this)
        imgShowAllMenus.setOnClickListener(this)
        btnSearchMenu.setOnClickListener(this)

        updateUIUser()
        updateUIMenuCategories()
        updateUIMenus(menus)
        updateUICart()

        fetchCategories()
        fetchMenus()
    }

    override fun onResume() {
        super.onResume()
        updateUIUser()
        updateUIMenuCategories()
        updateUIMenus(menus)
        updateUICart()

        fetchCategories()
        fetchMenus()
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

            R.id.tv_home_detail_menus -> {
                updateUIMenus(menus)
                adapterMenuCategories.resetSelection()
            }

            R.id.img_home_detail_menus -> {
                updateUIMenus(menus)
                adapterMenuCategories.resetSelection()
            }

            R.id.btn_home_search -> {
                searchMenuFromName()
            }
        }
    }

    private fun initComponents() {
        tvNameUser = findViewById(R.id.tv_home_name)
        tvRoleUser = findViewById(R.id.tv_home_role)
        tvShowAllMenus = findViewById(R.id.tv_home_detail_menus)
        tvSilahkanPilihMenu = findViewById(R.id.tv_home_silahkan_pilih_menu)
        imgProfile = findViewById(R.id.img_home_profile)
        imgCart = findViewById(R.id.img_home_cart)
        imgShowAllMenus = findViewById(R.id.img_home_detail_menus)
        edtSearchMenu = findViewById(R.id.edt_home_search_menu)
        btnSearchMenu = findViewById(R.id.btn_home_search)
        recycleViewMenuCategories = findViewById(R.id.rv_home_menu_categories)
        recycleViewMenus = findViewById(R.id.rv_home_menus)
        tvCountMenuInCart = findViewById(R.id.tv_home_value_cart)
    }

    private fun updateUICart() {
        val countMenusInCart = OrderObject.details.count()
        tvCountMenuInCart.setText(countMenusInCart.toString())
    }

    @SuppressLint("SetTextI18n")
    private fun updateUIUser() {
        val user = getUserFromPreferences(this)
        tvNameUser.text = user?.name ?: "User"
        tvSilahkanPilihMenu.text = "Hai ${user?.name ?: "User"}, Silahkan Pilih Menu"
        tvRoleUser.text = if (user?.isAdmin == true) "Admin" else "Pelanggan"
    }

    private fun updateUIMenuCategories() {
        recycleViewMenuCategories.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        requestQueue = Volley.newRequestQueue(this)

        adapterMenuCategories = HomeMenuCategoriesAdapter(categories) { category ->
            getMenusFromCategories(category._id)
        }

        recycleViewMenuCategories.adapter = adapterMenuCategories
    }

    private fun updateUIMenus(filteredMenus: List<Menu>) {
        // Menggunakan GridLayoutManager dengan 2 kolom
        val layoutManager = GridLayoutManager(this, 2)
        recycleViewMenus.layoutManager = layoutManager

        // Inisialisasi adapterMenus
        adapterMenus = HomeMenuAdapter(filteredMenus) { menu ->
            val intent = Intent(this, DetailMenuActivity::class.java).apply {
                putExtra(DetailMenuActivity.EXTRA_MENU, menu)
            }
            startActivity(intent)
        }

        // Set adapter ke RecyclerView
        recycleViewMenus.adapter = adapterMenus
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

    @SuppressLint("NotifyDataSetChanged")
    private fun fetchCategories() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.getMenuCategories()
                withContext(Dispatchers.Main) {
                    categories.clear()
                    categories.addAll(response.data)
                    adapterMenuCategories.notifyDataSetChanged()
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

    @SuppressLint("NotifyDataSetChanged")
    private fun fetchMenus() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.getMenus()
                withContext(Dispatchers.Main) {
                    menus.clear()
                    menus.addAll(response.data)
                    adapterMenus.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@MainActivity,
                        "Failed to fetch menus",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun getMenusFromCategories(category: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val filteredMenus = menus.filter { it.category == category }
                withContext(Dispatchers.Main) {
                    menusFromCategories.clear()
                    menusFromCategories.addAll(filteredMenus)
                    updateUIMenus(menusFromCategories)
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@MainActivity,
                        "Failed to filter menus",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun searchMenuFromName() {
        val name = edtSearchMenu.text.toString().trim()
        if (name.isEmpty()) {
            Toast.makeText(this, "Silahkan masukkan nama menu!", Toast.LENGTH_SHORT).show()
        } else {
            CoroutineScope(Dispatchers.IO).launch {
                try {
                    val filteredMenus = menus.filter { it.name.contains(name, ignoreCase = true) }
                    withContext(Dispatchers.Main) {
                        menusFromName.clear()
                        menusFromName.addAll(filteredMenus)
                        updateUIMenus(menusFromName)

                        // Reset kategori saat pencarian dilakukan
                        adapterMenuCategories.resetSelection()
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        Toast.makeText(
                            this@MainActivity,
                            "Failed to filter menus",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }
}
