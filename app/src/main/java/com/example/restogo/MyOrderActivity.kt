package com.example.restogo

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.restogo.model.ApiService
import com.example.restogo.model.OrderMenuData
import com.example.restogo.model.OrderMenuResponse
import com.example.restogo.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class MyOrderActivity : Activity(), View.OnClickListener {
    private lateinit var btnBack: ImageView
    private lateinit var tvOrderHistory: TextView
    private lateinit var tvStatusPesanan: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var detailOrders: List<OrderMenuData>
    private lateinit var adapter: MyOrderAdapter
    private val API_URL = Env.apiUrl
    private lateinit var user: User

    private val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_orders)
        initComponents()
        user = getUserFromPreferences(this)!!

        if (user != null) {
            fetchUserOrders(user._id)
        }

        btnBack.setOnClickListener(this)
        tvOrderHistory.setOnClickListener(this)
    }

    private fun initComponents() {
        btnBack = findViewById(R.id.img_my_order_activity_back)
        tvStatusPesanan = findViewById(R.id.tv_my_order_status)
        tvOrderHistory = findViewById(R.id.tv_my_order_riwayat)
        recyclerView = findViewById(R.id.rv_my_order_activity)
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    private fun fetchUserOrders(userId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.getMenusFromUserOrder(userId)

                if (response != null) {
                    withContext(Dispatchers.Main) {
                        detailOrders = response.data

                        tvStatusPesanan.visibility = View.GONE

                        adapter = MyOrderAdapter(this@MyOrderActivity, detailOrders) { detailMenu ->
                            // Implementasikan aksi ketika item diklik
                        }
                        recyclerView.adapter = adapter
                    }
                }
            } catch (e: Exception) {
                Log.e("MyOrderActivity", "Error fetching user orders", e)
            }
        }
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.img_my_order_activity_back) {
            finish()
        }
        if (v?.id == R.id.tv_my_order_riwayat) {
            val intent = Intent(this, OrderHistoryActivity::class.java)
            startActivity(intent)
            finish()
        }
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
