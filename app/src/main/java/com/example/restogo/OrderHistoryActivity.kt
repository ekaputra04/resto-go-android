package com.example.restogo

import android.app.Activity
import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.restogo.model.ApiService
import com.example.restogo.model.OrderMenuData
import com.example.restogo.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class OrderHistoryActivity : Activity(), View.OnClickListener {
    private lateinit var btnBack: ImageView
    private lateinit var tvStatusPesanan: TextView
    private lateinit var tvOrderBerlangsung: TextView
    private lateinit var recyclerView: RecyclerView
    private lateinit var detailOrders: List<OrderMenuData>
    private lateinit var adapter: OrderHistoryAdapter
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
        setContentView(R.layout.activity_order_history)
        initComponents()

        user = getUserFromPreferences(this)!!

        if (user != null) {
            fetchUserOrders(user._id)
        }

        btnBack.setOnClickListener(this)
        tvOrderBerlangsung.setOnClickListener(this)
    }

    private fun initComponents() {
        btnBack = findViewById(R.id.img_order_history_activity_back)
        tvStatusPesanan = findViewById(R.id.tv_order_history_status)
        tvOrderBerlangsung = findViewById(R.id.tv_order_history_berlangsung)
        recyclerView = findViewById(R.id.rv_order_history_activity)
        recyclerView.layoutManager = LinearLayoutManager(this)
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.img_order_history_activity_back) {
            finish()
        }

        if (v?.id == R.id.tv_order_history_berlangsung) {
            val intent = Intent(this, MyOrderActivity::class.java)
            startActivity(intent)
            finish()
        }
    }

    private fun fetchUserOrders(userId: String) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.getMenusFromUserOrderHistory(userId)

                if (response != null) {
                    withContext(Dispatchers.Main) {
                        detailOrders = response.data
                        tvStatusPesanan.visibility = View.GONE
                        adapter = OrderHistoryAdapter(
                            this@OrderHistoryActivity,
                            detailOrders
                        ) { detailMenu ->
                            // Implementasikan aksi ketika item diklik
                        }
                        recyclerView.adapter = adapter
                    }
                }
            } catch (e: Exception) {
                Log.e("OrderHistoryActivity", "Error fetching user orders", e)
            }
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