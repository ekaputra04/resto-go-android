package com.example.restogo

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.media.tv.TvContentRating
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.restogo.model.ApiService
import com.example.restogo.model.Order
import com.example.restogo.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class AllOrderActivity : Activity(), View.OnClickListener {
    private lateinit var imgBack: ImageView
    private lateinit var tvRiwayat: TextView
    private lateinit var recycleView: RecyclerView
    private lateinit var tvTidakTersedia: TextView
    private lateinit var adapter: AllOrderAdapter
    private lateinit var requestQueue: com.android.volley.RequestQueue
    private val API_URL = Env.apiUrl
    private val orders = mutableListOf<Order>()

    private val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_order)
        initComponents()

        recycleView.layoutManager = LinearLayoutManager(this)

        updateUIRecycleView()

        imgBack.setOnClickListener(this)
    }

    override fun onResume() {
        super.onResume()
        fetchOrders()

    }

    private fun initComponents() {
        imgBack = findViewById(R.id.img_all_order_activity_back)
        tvRiwayat = findViewById(R.id.tv_all_order_riwayat)
        recycleView = findViewById(R.id.rv_all_order_activity)
        tvTidakTersedia = findViewById(R.id.tv_all_order_status)
        requestQueue = Volley.newRequestQueue(this)
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.img_all_order_activity_back) {
            finish()
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun fetchOrders() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.getAllOrdersNotDone()
                withContext(Dispatchers.Main) {
                    orders.clear()
                    orders.addAll(response.data)
                    adapter.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@AllOrderActivity,
                        "Failed to fetch orders",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun updateUIRecycleView() {
        fetchOrders()

        if (orders.isNotEmpty()) {
            tvTidakTersedia.visibility = View.GONE
        }

        adapter = AllOrderAdapter(orders) { order ->
            showUpdateDialog(order)
        }
        recycleView.adapter = adapter

    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showUpdateDialog(order: Order) {
        val options = arrayOf("Selesai", "Belum Selesai")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Edit Order: ")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> {
                        updateOrderStatus(order._id, true)
//                        updateUIRecycleView()
                    }

                    1 -> {
                        updateOrderStatus(order._id, false)
//                        updateUIRecycleView()
                    }
                }
            }
        val dialog = builder.create()
        dialog.show()
    }

    private fun updateOrderStatus(orderId: String?, isDone: Boolean) {
        val url = "$API_URL/orders/update-order/$orderId"
        val params = JSONObject().apply {
            put("isDone", isDone)
        }

        val request = object : JsonObjectRequest(
            Method.PUT,
            url,
            params,
            { response ->
                Log.d("API_RESPONSE", response.toString())
                if (response.has("message") && response.getString("message") == "Berhasil memperbarui status order!") {
                    Toast.makeText(this, "Berhasil memperbarui status order!", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(this, "Gagal memperbarui status order!", Toast.LENGTH_SHORT)
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
}