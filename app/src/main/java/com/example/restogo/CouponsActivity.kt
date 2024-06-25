package com.example.restogo

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.restogo.model.ApiService
import com.example.restogo.model.Coupon
import com.google.gson.Gson
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class CouponsActivity : Activity(), View.OnClickListener {
    private lateinit var btnTambahKupon: Button
    private lateinit var btnKembali: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: CouponAdapter
    private lateinit var requestQueue: RequestQueue
    private val API_URL = Env.apiUrl
    private val coupons = mutableListOf<Coupon>()

    private val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_coupons)

        initComponents()
        btnTambahKupon.setOnClickListener(this)
        btnKembali.setOnClickListener(this)
        recyclerView.layoutManager = LinearLayoutManager(this)
        adapter = CouponAdapter(coupons) { coupon ->
            showUpdateDeleteDialog(coupon)
        }
        recyclerView.adapter = adapter

        fetchCoupons()
    }

    private fun initComponents() {
        btnTambahKupon = findViewById(R.id.btn_coupons_activity_add)
        btnKembali = findViewById(R.id.img_coupon_activity_back)
        recyclerView = findViewById(R.id.rv_coupons_activity)
        requestQueue = Volley.newRequestQueue(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_coupons_activity_add -> {
                // Implementasi logika untuk menambah kupon
                val intent = Intent(this, AddMenuActivity::class.java)
                startActivityForResult(intent, 1)
            }

            R.id.img_coupon_activity_back -> {
                finish()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        fetchCoupons()
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun fetchCoupons() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.getCoupons()

                withContext(Dispatchers.Main) {
                    coupons.clear()
                    coupons.addAll(response.data)
                    adapter.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@CouponsActivity,
                        "Gagal mengambil daftar kupon",
                        Toast.LENGTH_SHORT
                    ).show()
                    Log.e("CouponsActivity", "Error fetching coupons: ${e.message}")
                }
            }
        }

    }

    @SuppressLint("SetTextI18n")
    private fun showUpdateDeleteDialog(coupon: Coupon) {
        val options = arrayOf("Update kupon", "Hapus kupon")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Kupon: ${coupon.couponCode}")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> {
                        // Handle update kupon
                        val intent = Intent(this, UpdateMenuActivity::class.java).apply {
                            putExtra(UpdateMenuActivity.EXTRA_MENU, coupon)
                        }
                        startActivity(intent)
                    }

                    1 -> {
                        // Handle delete kupon
                        deleteCoupon(coupon._id) { couponToRemove ->
                            if (couponToRemove != null) {
                                coupons.remove(couponToRemove)
                                adapter.notifyDataSetChanged()
                                Toast.makeText(
                                    this@CouponsActivity,
                                    "Berhasil menghapus kupon '${coupon.couponCode}'",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    this@CouponsActivity,
                                    "Gagal menghapus kupon '${coupon.couponCode}'",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                        }
                    }
                }
            }
        val dialog = builder.create()
        dialog.show()
    }

    private fun deleteCoupon(couponId: String, callback: (Coupon?) -> Unit) {
        val url = "$API_URL/coupons/$couponId"
        val request = object : JsonObjectRequest(
            Request.Method.DELETE,
            url,
            null,
            { response ->
                Log.d("API_RESPONSE", response.toString())
                if (response.has("message") && response.getString("message") == "Berhasil menghapus kupon!") {
                    val couponToRemove = coupons.find { it._id == couponId }
                    callback(couponToRemove)
                } else {
                    callback(null)
                }
            },
            { error ->
                Log.e("API_ERROR", error.toString())
                if (error.networkResponse?.statusCode == 404) {
                    Toast.makeText(
                        this@CouponsActivity,
                        "Kupon tidak ditemukan",
                        Toast.LENGTH_SHORT
                    ).show()
                } else {
                    Toast.makeText(this@CouponsActivity, "Terjadi kesalahan!", Toast.LENGTH_SHORT)
                        .show()
                }
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

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 1 || requestCode == 2) {
            if (resultCode == RESULT_OK) {
                fetchCoupons()
            }
        }
    }
}
