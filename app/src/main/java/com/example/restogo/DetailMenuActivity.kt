package com.example.restogo

import android.annotation.SuppressLint
import android.app.Activity
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
import com.example.restogo.model.ExtraMenu
import com.example.restogo.model.Menu
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

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
    private var quantity: Int = 0
    private var subTotal: Int = 0

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

    @SuppressLint("SetTextI18n")
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

        tvName.setText(menu.name)
        tvPrice.setText("Rp.${menu.price}")
        tvQuantity.setText(quantity.toString())
        tvSubTotal.setText("Rp.${subTotal}")

        rvExtraMenus.layoutManager = LinearLayoutManager(this)
        adapter = RadioButtonAdapter(extraMenus) { selectedOption ->
            Toast.makeText(this, "Selected: $selectedOption", Toast.LENGTH_SHORT).show()
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
        subTotal = (menu.price * quantity)
        tvQuantity.setText(quantity.toString())
        tvSubTotal.setText("Rp.${subTotal}")
    }

    @SuppressLint("SetTextI18n")
    private fun updateQuantityMinus() {
        if (quantity == 0) {
            quantity = 0
        } else{
            quantity -= 1
        }
        subTotal = (menu.price * quantity)
        tvQuantity.setText(quantity.toString())
        tvSubTotal.setText("Rp.${subTotal}")
    }
}
