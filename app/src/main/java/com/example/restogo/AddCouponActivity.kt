package com.example.restogo

import android.app.Activity
import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.android.volley.Request
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import org.json.JSONArray
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class AddCouponActivity : Activity(), View.OnClickListener {
    private lateinit var btnBack: ImageView
    private lateinit var edtCouponCode: EditText
    private lateinit var edtDiscount: EditText
    private lateinit var dateStarted: EditText
    private lateinit var dateEnded: EditText
    private lateinit var btnSubmit: Button
    private lateinit var dateFormatter: SimpleDateFormat
    private lateinit var requestQueue: com.android.volley.RequestQueue
    private val API_URL = Env.apiUrl

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_coupon)
        initComponents()

        dateFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)

        dateStarted.setOnClickListener {
            showDatePickerDialog(dateStarted, dateFormatter)
        }

        dateEnded.setOnClickListener {
            showDatePickerDialog(dateEnded, dateFormatter)
        }

        btnSubmit.setOnClickListener(this)
        btnBack.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_add_coupon_activity_simpan -> createCoupon()
            R.id.img_add_coupon_activity_back -> finish()
        }
    }

    private fun initComponents() {
        btnBack = findViewById(R.id.img_add_coupon_activity_back)
        edtCouponCode = findViewById(R.id.edt_add_coupon_activity_coupon_code)
        edtDiscount = findViewById(R.id.edt_add_coupon_activity_discount)
        dateStarted = findViewById(R.id.edt_add_coupon_activity_date_started)
        dateEnded = findViewById(R.id.edt_add_coupon_activity_date_ended)
        btnSubmit = findViewById(R.id.btn_add_coupon_activity_simpan)
        requestQueue = Volley.newRequestQueue(this)
    }

    private fun showDatePickerDialog(editText: EditText, dateFormatter: SimpleDateFormat) {
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        val datePickerDialog =
            DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
                calendar.set(selectedYear, selectedMonth, selectedDay)
                editText.setText(dateFormatter.format(calendar.time))
            }, year, month, day)

        datePickerDialog.show()
    }

    private fun checkCouponExists(couponCode: String, callback: (Boolean) -> Unit) {
        val url = "$API_URL/coupons/name/$couponCode"
        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                Log.d("API_RESPONSE", response.toString())
                callback(response.has("data"))
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

    private fun createCoupon() {
        val couponCode = edtCouponCode.text.toString().trim()
        val discount = edtDiscount.text.toString().trim()
        val startDate = dateStarted.text.toString().trim()
        val endDate = dateEnded.text.toString().trim()

        if (couponCode.isEmpty()) {
            edtCouponCode.error = "Masukkan kode kupon!"
            return
        }

        if (discount.isEmpty()) {
            edtDiscount.error = "Masukkan diskon!"
            return
        }

        if (startDate.isEmpty()) {
            dateStarted.error = "Masukkan tanggal mulai!"
            return
        }

        if (endDate.isEmpty()) {
            dateEnded.error = "Masukkan tanggal berakhir!"
            return
        }

        checkCouponExists(couponCode) { exists ->
            if (exists) {
                Toast.makeText(this, "Kode kupon sudah terdaftar!", Toast.LENGTH_SHORT)
                    .show()
                edtCouponCode.error = "Kode kupon sudah terdaftar!"
            } else {
                val requestBody = JSONObject().apply {
                    put("data", JSONArray().apply {
                        put(JSONObject().apply {
                            put("couponCode", couponCode)
                            put("discount", discount)
                            put("dateStarted", startDate)
                            put("dateEnded", endDate)
                        })
                    })
                }

                val registerRequest = JsonObjectRequest(
                    Request.Method.POST, "$API_URL/coupons", requestBody,
                    { response ->
                        Toast.makeText(
                            this,
                            "Berhasil menambah kupon!",
                            Toast.LENGTH_SHORT
                        ).show()
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
                            "Gagal menambah kupon!",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                )
                requestQueue.add(registerRequest)
            }
        }
    }
}
