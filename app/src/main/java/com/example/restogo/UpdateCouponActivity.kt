package com.example.restogo

import android.app.Activity
import android.app.DatePickerDialog
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import com.android.volley.AuthFailureError
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.restogo.model.Coupon
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class UpdateCouponActivity : Activity(), View.OnClickListener {
    private lateinit var btnBack: ImageView
    private lateinit var edtCouponCode: EditText
    private lateinit var edtDiscount: EditText
    private lateinit var dateStarted: EditText
    private lateinit var dateEnded: EditText
    private lateinit var btnSubmit: Button
    private lateinit var dateFormatter: SimpleDateFormat
    private lateinit var requestQueue: RequestQueue
    private lateinit var coupon: Coupon
    private val API_URL = Env.apiUrl

    companion object {
        const val EXTRA_COUPON: String = "extra_coupon"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_coupon)
        initComponents()

        dateFormatter = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'", Locale.US)

        dateStarted.setOnClickListener {
            showDatePickerDialog(dateStarted, dateFormatter)
        }

        dateEnded.setOnClickListener {
            showDatePickerDialog(dateEnded, dateFormatter)
        }

        coupon = if (Build.VERSION.SDK_INT >= 33) {
            intent?.getParcelableExtra(UpdateCouponActivity.EXTRA_COUPON, Coupon::class.java)
        } else {
            @Suppress("DEPRECATION")
            intent?.getParcelableExtra(UpdateCouponActivity.EXTRA_COUPON)
        }!!

        edtCouponCode.setText(coupon.couponCode)
        edtDiscount.setText(coupon.discount.toString())
        dateStarted.setText(dateFormatter.format(coupon.dateStarted))
        dateEnded.setText(dateFormatter.format(coupon.dateEnded))

        btnSubmit.setOnClickListener(this)
        btnBack.setOnClickListener(this)
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_update_coupon_activity_simpan -> updateCoupon()
            R.id.img_update_coupon_activity_back -> finish()
        }
    }

    private fun initComponents() {
        btnBack = findViewById(R.id.img_update_coupon_activity_back)
        edtCouponCode = findViewById(R.id.edt_update_coupon_activity_coupon_code)
        edtDiscount = findViewById(R.id.edt_update_coupon_activity_discount)
        dateStarted = findViewById(R.id.edt_update_coupon_activity_date_started)
        dateEnded = findViewById(R.id.edt_update_coupon_activity_date_ended)
        btnSubmit = findViewById(R.id.btn_update_coupon_activity_simpan)
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

    private fun updateCoupon() {
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

        // Gunakan ID kupon untuk menghindari pengecekan duplikasi saat update
        val url = "$API_URL/coupons/${coupon._id}"

        val requestBody = JSONObject().apply {
            put("couponCode", couponCode)
            put("discount", discount)
            put("dateStarted", startDate)
            put("dateEnded", endDate)
        }

        val request = object : JsonObjectRequest(
            Method.PUT,
            url,
            requestBody,
            { response ->
                Log.d("API_RESPONSE", response.toString())
                if (response.has("message") && response.getString("message") == "Berhasil mengedit kupon!") {
                    Toast.makeText(this, "Berhasil mengupdate kupon!", Toast.LENGTH_SHORT)
                        .show()
                    finish()
                } else {
                    Toast.makeText(this, "Gagal mengupdate kupon!", Toast.LENGTH_SHORT)
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
