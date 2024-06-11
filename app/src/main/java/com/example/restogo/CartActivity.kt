package com.example.restogo

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Context
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
import com.example.restogo.model.Order
import com.example.restogo.model.OrderObject
import com.example.restogo.model.User
import org.json.JSONArray
import org.json.JSONObject
import java.util.Date

class CartActivity : Activity(), View.OnClickListener {
    private lateinit var btnBack: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var edtCoupon: EditText
    private lateinit var btnApply: Button
    private lateinit var btnKirim: Button
    private lateinit var tvTotalPrice: TextView
    private lateinit var adapter: CartAdapter
    private lateinit var requestQueue: com.android.volley.RequestQueue
    private lateinit var couponCode: String
    private var isCouponActive: Boolean = false
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

        OrderObject.totalPrice = totalPrice
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
                couponCode = edtCoupon.text.toString().trim()
                if (couponCode.isNotEmpty()) {
                    applyCoupon(couponCode)
                } else {
                    couponCode = null.toString()
                    Toast.makeText(this, "Masukkan kode kupon!", Toast.LENGTH_SHORT).show()
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
        val user = getUserFromPreferences(this)

        if (user != null) {
            OrderObject.user = user

            val userJson = JSONObject().apply {
                put("_id", user._id)
                put("name", user.name)
                put("telephone", user.telephone)
                put("isAdmin", user.isAdmin)
            }

            val couponJson = JSONObject().apply {
                OrderObject.coupon?.let {
                    put("couponCode", couponCode)
                    put("isActive", true)
                    put("discount", 10)
                }
            }

            val detailsJsonArray = JSONArray().apply {
                OrderObject.details.forEach { detail ->
                    val detailJson = JSONObject().apply {
                        val menuJson = JSONObject().apply {
                            put("_id", detail.menu._id)
                            put("name", detail.menu.name)
                            put("price", detail.menu.price)
                            put("category", detail.menu.category)
                            put("url_image", detail.menu.url_image)
                        }

                        val extraMenuJson = JSONObject().apply {
                            put("_id", detail.extraMenu?._id)
                            put("name", detail.extraMenu?.name)
                            put("price", detail.extraMenu?.price)
                        }

                        put("menu", menuJson)
                        put("quantity", detail.quantity)
                        put("extraMenu", extraMenuJson)
                        put("subTotalMenu", detail.subTotalMenu)
                    }
                    put(detailJson)
                }
            }

            val requestBody = JSONObject().apply {
                put("data", JSONObject().apply {
                    put("user", userJson)
                    put("coupon", couponJson)
                    put("totalPrice", OrderObject.totalPrice)
                    put("date", Date().toString()) // Make sure date is properly formatted
                    put("isInCart", OrderObject.isInCart)
                    put("isDone", OrderObject.isDone)
                    put("details", detailsJsonArray)
                })
            }

            val submitRequest = JsonObjectRequest(
                Request.Method.POST, "$API_URL/orders", requestBody,
                { response ->
                    Toast.makeText(
                        this,
                        "Berhasil menambah order!",
                        Toast.LENGTH_SHORT
                    ).show()
                    val intent = Intent(this, MainActivity::class.java)
                    startActivity(intent)
                    finish()
                },
                { error ->
                    Log.e("API_ERROR", error.toString())
                    if (error.networkResponse != null) {
                        val statusCode = error.networkResponse.statusCode
                        val responseBody =
                            String(error.networkResponse.data, Charsets.UTF_8)
                        Log.e(
                            "API_ERROR",
                            "Status Code: $statusCode\nResponse Body: $responseBody"
                        )
                    }
                    Toast.makeText(
                        this,
                        "Gagal menambah menu!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            )
            requestQueue.add(submitRequest)

            Toast.makeText(this, "Order berhasil dipesan!", Toast.LENGTH_SHORT).show()

//            Reset semua data
            OrderObject.user = null
            OrderObject.coupon = null
            OrderObject.totalPrice = 0.0f
            OrderObject.details = emptyList()

            val intent = Intent(this, SplashScreenActivity::class.java)
            startActivity(intent)
            finish()
        } else {
            Toast.makeText(this, "User not found in preferences", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkCouponExists(couponCode: String, callback: (Boolean) -> Unit) {
        val url = "$API_URL/coupons/check/$couponCode"
        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                Log.d("API_RESPONSE", response.toString())
                if (response.has("data")) {
                    couponDiscount = response.getJSONObject("data").getInt("discount").toFloat()
                    isCouponActive = true

                    OrderObject.coupon?.couponCode = couponCode
                    OrderObject.coupon?.isActive = true
                    OrderObject.coupon?.discount = couponDiscount

                    callback(true)
                } else {
                    couponDiscount = 0.0f
                    isCouponActive = false
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
