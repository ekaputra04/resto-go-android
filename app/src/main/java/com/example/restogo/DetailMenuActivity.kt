package com.example.restogo

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.Volley
import com.example.restogo.model.ApiService
import com.example.restogo.model.DetailMenu
import com.example.restogo.model.ExtraMenu
import com.example.restogo.model.Menu
import com.example.restogo.model.OrderObject
import com.example.restogo.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.util.Date

class DetailMenuActivity : Activity(), View.OnClickListener {
    private lateinit var imgPhoto: ImageView
    private lateinit var tvName: TextView
    private lateinit var tvPrice: TextView
    private lateinit var rvExtraMenus: RecyclerView
    private lateinit var tvSubTotal: TextView
    private lateinit var tvQuantity: TextView
    private lateinit var imgPlus: ImageView
    private lateinit var imgMinus: ImageView
    private lateinit var imgBack: ImageView
    private lateinit var btnKirim: Button
    private lateinit var adapter: RadioButtonAdapter
    private lateinit var menu: Menu
    private lateinit var requestQueue: com.android.volley.RequestQueue
    private val API_URL = Env.apiUrl
    private val extraMenus = mutableListOf<ExtraMenu>()
    private var selectedMenuPrice: Int = 0
    private var selectedExtraMenu: ExtraMenu? = null
    private var quantity: Int = 0
    private var subTotalMenu: Float = 0.0f

    private val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    companion object {
        const val EXTRA_MENU: String = "extra_menu"
    }

    @SuppressLint("SetTextI18n", "DiscouragedApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail_menu)
        initComponents()

        menu = if (Build.VERSION.SDK_INT >= 33) {
            intent?.getParcelableExtra(EXTRA_MENU, Menu::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent?.getParcelableExtra(EXTRA_MENU)
        }!!

        // Mengubah nama file menjadi resource ID
        val resourceId =
            resources.getIdentifier(menu.url_image.replace(".jpg", ""), "drawable", packageName)

        // Mengatur gambar pada ImageView
        if (resourceId != 0) { // Pastikan resource ID valid
            imgPhoto.setImageResource(resourceId)
        } else {
            // Anda bisa menetapkan gambar default jika resource ID tidak valid
            imgPhoto.setImageResource(R.drawable.logo)
        }

        tvName.setText(menu.name)
        tvPrice.setText("Rp.${menu.price}")
        tvQuantity.setText(quantity.toString())
        tvSubTotal.setText("Rp.${subTotalMenu}")
        rvExtraMenus.layoutManager = LinearLayoutManager(this)
        adapter = RadioButtonAdapter(extraMenus) { selectedOption ->
            selectedMenuPrice = selectedOption.price
            selectedExtraMenu = selectedOption

            if (quantity == 0) subTotalMenu = 0.0f
            else subTotalMenu = ((menu.price * quantity) + selectedMenuPrice).toFloat()

            tvSubTotal.setText("Rp.${subTotalMenu}")
        }
        rvExtraMenus.adapter = adapter

        fetchExtraMenus()

        imgBack.setOnClickListener(this)
        btnKirim.setOnClickListener(this)
        imgPlus.setOnClickListener(this)
        imgMinus.setOnClickListener(this)
    }

    private fun initComponents() {
        imgPhoto = findViewById(R.id.img_detail_menu_photo)
        tvName = findViewById(R.id.tv_detail_menu_name)
        tvPrice = findViewById(R.id.tv_detail_menu_price)
        rvExtraMenus = findViewById(R.id.rv_radio_button_detail_menu)
        tvSubTotal = findViewById(R.id.tv_detail_menu_subtotal)
        tvQuantity = findViewById(R.id.tv_detail_menu_quantity)
        imgPlus = findViewById(R.id.img_detail_menu_plus)
        imgMinus = findViewById(R.id.img_detail_menu_minus)
        btnKirim = findViewById(R.id.btn_detail_menu_kirim)
        imgBack = findViewById(R.id.img_detail_menu_back)
        requestQueue = Volley.newRequestQueue(this)
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.img_detail_menu_back) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        if (v?.id == R.id.img_detail_menu_plus) {
            updateQuantityPlus()
        }

        if (v?.id == R.id.img_detail_menu_minus) {
            updateQuantityMinus()
        }

        if (v?.id == R.id.btn_detail_menu_kirim) {
            if (quantity != 0) {
//                logika untuk menyimpan semua data order di sini pada variabel global OrderObject
                val user = getUserFromPreferences(this)
                val newDetailMenu = DetailMenu(menu, quantity, selectedExtraMenu, subTotalMenu)

                OrderObject.details = OrderObject.details + newDetailMenu
                OrderObject.totalPrice += subTotalMenu

                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun fetchExtraMenus() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.getExtraMenus()
                Log.i("infoApk", response.data.toString())
                withContext(Dispatchers.Main) {
                    extraMenus.clear()
                    extraMenus.addAll(response.data)
                    adapter.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@DetailMenuActivity,
                        "Failed to fetch extra menus",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    @SuppressLint("SetTextI18n")
    private fun updateQuantityPlus() {
        quantity += 1
        subTotalMenu = ((menu.price * quantity) + selectedMenuPrice).toFloat()
        tvQuantity.setText(quantity.toString())
        tvSubTotal.setText("Rp.${subTotalMenu}")
    }

    @SuppressLint("SetTextI18n")
    private fun updateQuantityMinus() {
        if (quantity == 0) {
            quantity = 0
        } else {
            quantity -= 1
        }

        if (quantity == 0) {
            subTotalMenu = 0.0f
        } else {
            subTotalMenu = ((menu.price * quantity) + selectedMenuPrice).toFloat()
        }

        tvQuantity.setText(quantity.toString())
        tvSubTotal.setText("Rp.${subTotalMenu}")
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
}
