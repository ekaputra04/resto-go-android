package com.example.restogo

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.toolbox.Volley
import com.example.restogo.model.DetailMenu
import com.example.restogo.model.OrderObject

class CartActivity : Activity(), View.OnClickListener {
    private lateinit var btnBack: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var edtCoupon: EditText
    private lateinit var btnApply: Button
    private lateinit var btnKirim: Button
    private lateinit var tvTotalPrice: TextView
    private lateinit var adapter: CartAdapter
    private lateinit var requestQueue: com.android.volley.RequestQueue

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
        val totalPrice = OrderObject.details.sumByDouble { it.subTotalMenu.toDouble() }.toFloat()
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
                val couponCode = edtCoupon.text.toString()
                if (couponCode.isNotEmpty()) {
                    applyCoupon(couponCode)
                } else {
                    Toast.makeText(this, "Please enter a coupon code", Toast.LENGTH_SHORT).show()
                }
            }
            R.id.btn_cart_kirim -> {
                // Handle order submission
                submitOrder()
            }
        }
    }

    private fun applyCoupon(couponCode: String) {
        // Implement the logic to apply the coupon code
        // This might include checking the validity of the coupon and updating the total price
        Toast.makeText(this, "Coupon applied: $couponCode", Toast.LENGTH_SHORT).show()
        // Example: update the total price after applying the coupon
        updateTotalPrice()
    }

    private fun submitOrder() {
        // Implement the logic to submit the order
        Toast.makeText(this, "Order submitted", Toast.LENGTH_SHORT).show()
        // Example: redirect to another activity after order submission
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}
