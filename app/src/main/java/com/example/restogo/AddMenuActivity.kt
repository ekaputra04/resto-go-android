package com.example.restogo

import android.app.Activity
import android.app.ProgressDialog
import android.content.Intent
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import com.android.volley.Request
import com.android.volley.RequestQueue
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.restogo.model.ApiService
import com.example.restogo.model.MenuCategory
import kotlinx.coroutines.*
import okhttp3.MultipartBody
import retrofit2.Retrofit
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.RequestBody.Companion.asRequestBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File

class AddMenuActivity : Activity(), View.OnClickListener {
    private lateinit var btnKembali: ImageView
    private lateinit var edtName: EditText
    private lateinit var edtPrice: EditText
    //    private lateinit var imgPhoto: ImageView
    private lateinit var btnKirim: Button
    private lateinit var adapter: ArrayAdapter<String>
    private lateinit var requestQueue: RequestQueue
    private lateinit var spinnerMenuCategories: Spinner
    private lateinit var name: String
    private lateinit var price: String
    private lateinit var category: String
    private lateinit var displayUrl: String
    private lateinit var progressDialog: ProgressDialog
    private val API_URL = Env.apiUrl
    private val categories = mutableListOf<MenuCategory>()
    private var selectedImagePath: String? = null

    private val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    private val imgbbApiService: ImgbbApiService by lazy {
        Retrofit.Builder()
            .baseUrl("https://api.imgbb.com/1/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ImgbbApiService::class.java)
    }

    companion object {
        private const val REQUEST_IMAGE_GALLERY = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_menu)
        initComponents()

        // Inisialisasi ProgressDialog
        progressDialog = ProgressDialog(this)
        progressDialog.setMessage("Uploading image...")
        progressDialog.setCancelable(false)

        // Buat panggilan API untuk mendapatkan kategori
        fetchMenuCategories()

        btnKirim.setOnClickListener(this)
//        imgPhoto.setOnClickListener(this)
        btnKembali.setOnClickListener(this)
    }

    private fun initComponents() {
        btnKembali = findViewById(R.id.img_add_menu_activity_back)
        edtName = findViewById(R.id.edt_add_menu_activity_name)
        edtPrice = findViewById(R.id.edt_add_menu_activity_price)
//        imgPhoto = findViewById(R.id.img_add_menu_activity_image)
        btnKirim = findViewById(R.id.btn_add_menu_category_simpan)
        requestQueue = Volley.newRequestQueue(this)
        spinnerMenuCategories = findViewById(R.id.spinner_add_menu_activity_category)
    }

    private fun fetchMenuCategories() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.getMenuCategories()
                withContext(Dispatchers.Main) {
                    categories.clear()
                    categories.addAll(response.data)
                    setupSpinner()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@AddMenuActivity,
                        "Failed to fetch categories",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun setupSpinner() {
        // Buat adapter untuk spinner menggunakan kategori yang didapat dari API
        val categoryNames = categories.map { it.name }
        adapter =
            ArrayAdapter(this@AddMenuActivity, android.R.layout.simple_spinner_item, categoryNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerMenuCategories.adapter = adapter

        // Tambahkan listener untuk spinner
        spinnerMenuCategories.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {
                // Handling ketika item dipilih
                val selectedCategory = categories[position]
                // Lakukan sesuatu dengan kategori yang dipilih
                Log.i("infoApk", selectedCategory.name)
                category = selectedCategory._id
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Handling ketika tidak ada item yang dipilih
            }
        }
    }

    override fun onClick(v: View?) {
        when (v?.id) {
            R.id.btn_add_menu_category_simpan -> {
                name = edtName.text.toString().trim()
                price = edtPrice.text.toString().trim()

                if (name.isEmpty()) {
                    edtName.error = "Masukkan nama menu!"
                    return
                }

                if (price.isEmpty()) {
                    edtName.error = "Masukkan harga menu!"
                    return
                }

                checkMenuExists(name) { exists ->
                    if (exists) {
                        Toast.makeText(this, "Nama menu sudah terdaftar!", Toast.LENGTH_SHORT)
                            .show()
                        edtName.error = "Nama menu sudah terdaftar!"
                    } else {
                        val requestBody = JSONObject().apply {
                            put("data", JSONArray().apply {
                                put(JSONObject().apply {
                                    put("name", name)
                                    put("price", price.toInt())
                                    put("category", category)
                                })
                            })
                        }

                        val registerRequest = JsonObjectRequest(
                            Request.Method.POST, "$API_URL/menus", requestBody,
                            { response ->
                                Toast.makeText(
                                    this,
                                    "Berhasil menambah menu!",
                                    Toast.LENGTH_SHORT
                                ).show()
                                val intent = Intent(this, MenuActivity::class.java)
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
                        requestQueue.add(registerRequest)
                    }
                }
            }

//            R.id.img_add_menu_activity_image -> {
//                openGallery()
//            }

            R.id.img_add_menu_activity_back -> {
                val intent = Intent(this, MenuActivity::class.java)
                startActivity(intent)
                finish()
            }
        }
    }

    private fun openGallery() {
        val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
        startActivityForResult(intent, REQUEST_IMAGE_GALLERY)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == REQUEST_IMAGE_GALLERY && resultCode == RESULT_OK && data != null) {
            val selectedImage = data.data
//            imgPhoto.setImageURI(selectedImage)
            selectedImagePath = selectedImage?.path
            Log.i("infoApk", selectedImagePath.toString())
            uploadImageToImgBB()
        }
    }

    private fun uploadImageToImgBB() {
        selectedImagePath?.let { imagePath ->
            val file = File(imagePath)
            val requestFile = file.asRequestBody("image/*".toMediaTypeOrNull())
            val imagePart = MultipartBody.Part.createFormData("image", file.name, requestFile)

            CoroutineScope(Dispatchers.IO).launch {
                try {
                    // Menampilkan loading spinner
                    withContext(Dispatchers.Main) {
                        progressDialog.show()
                    }

                    // Kirim permintaan POST ke ImgBB
                    val response = imgbbApiService.uploadImage(imagePart)

                    // Menghilangkan loading spinner setelah selesai
                    withContext(Dispatchers.Main) {
                        progressDialog.dismiss()
                        if (response.success) {
                            // Simpan displayUrl ke dalam properti displayUrl
                            displayUrl = response.data.displayUrl
                            Toast.makeText(
                                this@AddMenuActivity,
                                "Image uploaded successfully. Display URL: $displayUrl",
                                Toast.LENGTH_SHORT
                            ).show()
                            // Simpan displayUrl ke dalam database atau gunakan sesuai kebutuhan aplikasi Anda
                        } else {
                            Toast.makeText(
                                this@AddMenuActivity,
                                "Failed to upload image",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                } catch (e: Exception) {
                    withContext(Dispatchers.Main) {
                        progressDialog.dismiss()
                        Toast.makeText(
                            this@AddMenuActivity,
                            "Failed to upload image: ${e.message}",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            }
        }
    }

    private fun checkMenuExists(name: String, callback: (Boolean) -> Unit) {
        val url = "$API_URL/menu/name/$name"
        val request = JsonObjectRequest(
            Request.Method.GET, url, null,
            { response ->
                Log.d("API_RESPONSE", response.toString())
                if (response.has("data")) {
                    callback(true)
                } else {
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