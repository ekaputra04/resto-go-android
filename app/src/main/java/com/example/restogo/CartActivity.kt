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
import com.example.restogo.model.OrderObject
import com.example.restogo.model.User
import okhttp3.Call
import okhttp3.Callback
import okhttp3.Credentials
import okhttp3.FormBody
import okhttp3.OkHttpClient
import okhttp3.Response
import org.json.JSONArray
import org.json.JSONObject
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class CartActivity : Activity(), View.OnClickListener {
    private lateinit var btnBack: ImageView
    private lateinit var recyclerView: RecyclerView
    private lateinit var edtCoupon: EditText
    private lateinit var btnApply: Button
    private lateinit var btnKirim: Button
    private lateinit var tvTotalPrice: TextView
    private lateinit var adapter: CartAdapter
    private lateinit var requestQueue: com.android.volley.RequestQueue
    private var couponCode: String? = ""
    private var isCouponActive: Boolean = false
    private var couponDiscount: Float = 0.0f
    private val API_URL: String = Env.apiUrl
    private val client = OkHttpClient()
    private val ACCOUNT_SID: String = Env.ACCOUNT_SID
    private val AUTH_TOKEN: String = Env.AUTH_TOKEN
    private val TWILIO_SANDBOX_WHATSAPP_NUMBER: String = Env.TWILIO_SANDBOX_WHATSAPP_NUMBER
    var stringResponseHeader = "================================\n" +
            "*RESTO GO*\n" +
            "_Cita Rasa Nusantara_\n" +
            "================================\n"
    var stringResponseFooter = "--------------------------------\n" +
            "*TERIMA KASIH*\n" +
            "*Sampai Jumpa Kembali*\n" +
            "================================\n"

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
                finish()
            }

            R.id.btn_cart_apply -> {
                // Handle coupon application
                this.couponCode = edtCoupon.text.toString().trim()
                if (couponCode!!.isNotEmpty()) {
                    applyCoupon(couponCode!!)
                } else {
                    Toast.makeText(this, "Masukkan kode kupon!", Toast.LENGTH_SHORT).show()
                }

                updateTotalPrice()
            }

            R.id.btn_cart_kirim -> {
                if (OrderObject.details.isNotEmpty()) {
                    submitOrder()
                } else {
                    Toast.makeText(this, "Silahkan pesan menu!", Toast.LENGTH_SHORT).show()
                }
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

                OrderObject.coupon?.couponCode = couponCode
                OrderObject.coupon?.discount = couponDiscount
                this.couponCode = couponCode
                this.isCouponActive = true

                updateTotalPrice()
            } else {
                Toast.makeText(this, "Kode tidak tersedia!", Toast.LENGTH_SHORT).show()
                this.couponCode = null
                this.couponDiscount = 0.0f
                this.isCouponActive = false
                updateTotalPrice()
            }
        }
    }

    private fun submitOrder() {
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
                if (couponCode.isNullOrEmpty()) {
                    put("couponCode", null)
                    put("isActive", false)
                    put("discount", 0)
                } else {
                    put("couponCode", couponCode)
                    put("isActive", true)
                    put("discount", couponDiscount)
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
                    put("date", Date().toString())
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
//                    val intent = Intent(this, SuccessOrderActivity::class.java)
//                    startActivity(intent)
//                    finish()
                },
                { error ->
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

            // Prepare the message for WhatsApp
            val stringBuilder = StringBuilder()
            stringBuilder.append(stringResponseHeader)
            stringBuilder.append(
                "Tanggal: ${
                    SimpleDateFormat(
                        "dd-MM-yyyy HH:mm:ss",
                        Locale.getDefault()
                    ).format(Date())
                }\n"
            )
            stringBuilder.append("Pelanggan: ${user.name}\n")
            stringBuilder.append("Telepon: ${user.telephone}\n")
            stringBuilder.append("--------------------------------\n")

            OrderObject.details.forEach { detail ->
                stringBuilder.append("${detail.menu.name}\n")
                stringBuilder.append("Jumlah: ${detail.quantity}\n")
                stringBuilder.append("Harga: Rp.${detail.menu.price}\n")
                detail.extraMenu?.let { extra ->
                    stringBuilder.append("Extra: ${extra.name} - Rp.${extra.price}\n")
                }
                stringBuilder.append("Subtotal: Rp.${detail.subTotalMenu}\n")
                stringBuilder.append("--------------------------------\n")
            }

            stringBuilder.append(
                "Subtotal: Rp.${
                    OrderObject.details.sumOf { it.subTotalMenu.toDouble() }.toFloat()
                }\n"
            )
            if (couponDiscount > 0) {
                stringBuilder.append("Diskon: $couponDiscount%\n")
            }
            stringBuilder.append("Total: Rp.${OrderObject.totalPrice}\n")
            stringBuilder.append(stringResponseFooter)

            val message = stringBuilder.toString()

            sendMessage(
                formatPhoneNumber(user.telephone),
                message
            )

            OrderObject.coupon = null
            OrderObject.totalPrice = 0.0f
            OrderObject.details = emptyList()

            val intent = Intent(this, SuccessOrderActivity::class.java)
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
                if (response.has("data")) {
                    this.couponDiscount =
                        response.getJSONObject("data").getInt("discount").toFloat()
                    this.isCouponActive = true

                    OrderObject.coupon?.couponCode = couponCode
                    OrderObject.coupon?.discount = couponDiscount

                    callback(true)
                } else {
                    this.couponDiscount = 0.0f
                    this.isCouponActive = false
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

    private fun sendMessage(to: String, message: String) {
        val url = "https://api.twilio.com/2010-04-01/Accounts/$ACCOUNT_SID/Messages.json"

        val body = FormBody.Builder()
            .add("To", "whatsapp:$to")
            .add("From", TWILIO_SANDBOX_WHATSAPP_NUMBER)
            .add("Body", message)
            .build()

        val request = okhttp3.Request.Builder()
            .url(url)
            .post(body)
            .addHeader("Authorization", Credentials.basic(ACCOUNT_SID, AUTH_TOKEN))
            .build()

        client.newCall(request).enqueue(object : Callback {
            override fun onFailure(call: Call, e: IOException) {
                e.printStackTrace()
                runOnUiThread {
                    Toast.makeText(
                        applicationContext,
                        "Failed to send message: ${e.message}",
                        Toast.LENGTH_SHORT
                    ).show()

                    Log.i("infoMessage", e.message.toString())
                }
            }

            override fun onResponse(call: Call, response: Response) {
                response.use {
                    if (!it.isSuccessful) {
                        runOnUiThread {
                            Toast.makeText(
                                applicationContext,
                                "Gagal mengirim pesan!",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    } else {
                        runOnUiThread {
                            Toast.makeText(
                                applicationContext,
                                "Message sent successfully",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }
        })
    }

    fun formatPhoneNumber(phoneNumber: String): String {
        return if (phoneNumber.startsWith("0")) {
            "+62" + phoneNumber.substring(1)
        } else {
            phoneNumber
        }
    }
}