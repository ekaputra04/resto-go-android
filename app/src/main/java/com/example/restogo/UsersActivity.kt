package com.example.restogo

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.android.volley.AuthFailureError
import com.android.volley.toolbox.JsonObjectRequest
import com.android.volley.toolbox.Volley
import com.example.restogo.model.ApiService
import com.example.restogo.model.User
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

class UsersActivity : Activity(), View.OnClickListener {
    private lateinit var imgBack: ImageView
    private lateinit var recycleView: RecyclerView
    private lateinit var adapter: UserAdapter
    private lateinit var requestQueue: com.android.volley.RequestQueue
    private val API_URL = Env.apiUrl
    private val users = mutableListOf<User>()

    private val apiService: ApiService by lazy {
        Retrofit.Builder()
            .baseUrl(API_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()
            .create(ApiService::class.java)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_users)
        initComponents()
        recycleView.layoutManager = LinearLayoutManager(this)

        updateUIRecycleView()

//        adapter = UserAdapter(users) { user ->
//            showUpdateDialog(user)
//        }
//        recycleView.adapter = adapter
//
//        fetchUsers()

        imgBack.setOnClickListener(this)
    }

    private fun updateUIRecycleView() {
        adapter = UserAdapter(users) { user ->
            showUpdateDialog(user)
        }
        recycleView.adapter = adapter

        fetchUsers()
    }

    private fun initComponents() {
        imgBack = findViewById(R.id.img_user_activity_back)
        recycleView = findViewById(R.id.rv_user_activity)
        requestQueue = Volley.newRequestQueue(this)
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.img_user_activity_back) {
            finish()
        }
    }

    override fun onResume() {
        super.onResume()
        fetchUsers()
    }

    private fun fetchUsers() {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                val response = apiService.getUsers()
                withContext(Dispatchers.Main) {
                    users.clear()
                    users.addAll(response.data)
                    adapter.notifyDataSetChanged()
                }
            } catch (e: Exception) {
                withContext(Dispatchers.Main) {
                    Toast.makeText(
                        this@UsersActivity,
                        "Failed to fetch users",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    private fun showUpdateDialog(user: User) {
        val options = arrayOf("Admin", "Pelanggan")
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Edit Role: ${user.name}")
            .setItems(options) { _, which ->
                when (which) {
                    0 -> {
                        updateRoleUser(user._id, true)
                        updateUIRecycleView()
                    }

                    1 -> {
                        updateRoleUser(user._id, false)
                        updateUIRecycleView()
                    }
                }
            }
        val dialog = builder.create()
        dialog.show()
    }

    private fun updateRoleUser(userId: String, isAdmin: Boolean) {
        val url = "$API_URL/users/role/$userId"
        val params = JSONObject().apply {
            put("isAdmin", isAdmin)
        }

        val request = object : JsonObjectRequest(
            Method.PUT,
            url,
            params,
            { response ->
                Log.d("API_RESPONSE", response.toString())
                if (response.has("message") && response.getString("message") == "Berhasil mengedit role user!") {
                    Toast.makeText(this, "Berhasil mengedit role user!", Toast.LENGTH_SHORT)
                        .show()
                } else {
                    Toast.makeText(this, "Gagal mengedit role user!", Toast.LENGTH_SHORT)
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