package com.example.restogo

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.restogo.model.OrderObject
import com.example.restogo.model.User

class CartActivity : Activity(), View.OnClickListener {
    private lateinit var btnBack: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var edtCoupon: EditText
    private lateinit var btnApply: Button
    private lateinit var btnKirim: Button
    private lateinit var tvTotalPrice: TextView
    private lateinit var adapter: CartAdapter
    private lateinit var requestQueue: com.android.volley.RequestQueue
    private val API_URL = Env.apiUrl
    private var couponDiscount: Float = 0.0f

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cart)
        initComponents()

        recyclerView.layoutManager = LinearLayoutManager(this)

        adapter = CartAdapter(OrderObject.details.toMutableList()) {
            updateTotalPrice()
        }
        recyclerView.adapter = adapter

        btnBack.setOnClickListener(this)
        btnKirim.setOnClickListener(this)
        btnApply.setOnClickListener(this)

        // Calculate and display the total price
        updateTotalPrice()
    }

    private fun initComponents() {
        btnBack = findViewById(R.id.img_cart_back)
        recyclerView = findViewById(R.id.rv_cart)
        edtCoupon = findViewById(R.id.edt_cart_coupon)
        btnApply = findViewById(R.id.btn_cart_apply)
        btnKirim = findViewById(R.id.btn_cart_kirim)
        tvTotalPrice = findViewById(R.id.tv_cart_total_price)
        requestQueue = Volley.newRequestQueue(this)
    }

    @SuppressLint("SetTextI18n")
    private fun updateTotalPrice() {
        var totalPrice = OrderObject.details.sumOf { it.subTotalMenu.toDouble() }.toFloat()
        totalPrice *= ((100 - couponDiscount) / 100)
        tvTotalPrice.text = "Rp.${totalPrice}"
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.img_cart_back -> {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }

            R.id.btn_cart_apply -> {
                // Handle coupon application
                val couponCode = edtCoupon.text.toString().trim()
                if (couponCode.isNotEmpty()) {
                    applyCoupon(couponCode)
                } else {
                    Toast.makeText(this, "Please enter a coupon code", Toast.LENGTH_SHORT).show()
                }

                updateTotalPrice()
            }

            R.id.btn_cart_kirim -> {
                // Handle order submission
                submitOrder()
            }
        }
    }

    private fun applyCoupon(couponCode: String) {
        checkCouponExists(couponCode) { exist ->
            if (exist) {
                Toast.makeText(
                    this,
                    "Kode tersedia, diskon sebesar $couponDiscount%",
                    Toast.LENGTH_SHORT
                ).show()

                updateTotalPrice()
            } else {
                Toast.makeText(this, "Kode tidak tersedia!", Toast.LENGTH_SHORT).show()
                couponDiscount = 0.0f
                updateTotalPrice()
            }
        }
    }

    private fun submitOrder() {
        // Implement the logic to submit the order
        Toast.makeText(this, "Order submitted", Toast.LENGTH_SHORT).show()


        // Example: redirect to another activity after order submission
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }

    private fun checkCouponExists(couponCode: String, callback: (Boolean) -> Unit) {
        val url = "$API_URL/coupons/check/$couponCode"
        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                Log.d("API_RESPONSE", response.toString())
                if (response.has("data")) {
                    couponDiscount = response.getJSONObject("data").getInt("discount").toFloat()
                    callback(true)
                } else {
                    couponDiscount = 0.0f
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
