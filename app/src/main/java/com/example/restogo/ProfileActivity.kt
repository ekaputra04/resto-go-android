package com.example.restogo

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import com.example.restogo.model.OrderObject
import com.example.restogo.model.User

class ProfileActivity : Activity(), View.OnClickListener {
    private lateinit var btnBack: ImageView
    private lateinit var imgProfile: ImageView
    private lateinit var tvNameProfile: TextView
    private lateinit var tvTelephoneProfile: TextView
    private lateinit var tvNameProfileDetail: TextView
    private lateinit var tvTelephoneProfileDetail: TextView
    private lateinit var btnEditName: ImageView
    private lateinit var btnEditTelephone: ImageView
    private lateinit var btnShowMyOrders: ImageView
    private lateinit var btnShowMenuCategories: ImageView
    private lateinit var btnShowMenus: ImageView
    private lateinit var btnShowExtraMenus: ImageView
    private lateinit var btnShowUsers: ImageView
    private lateinit var imgNameDetail: ImageView
    private lateinit var imgTelephoneDetail: ImageView
    private lateinit var imgMyOrdersDetail: ImageView
    private lateinit var imgMenuCategoriesDetail: ImageView
    private lateinit var imgMenusDetail: ImageView
    private lateinit var imgExtraMenusDetail: ImageView
    private lateinit var imgUsersDetail: ImageView
    private lateinit var imgLogout: ImageView
    private lateinit var imgLogoutDetail: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        initComponents()

        val user = getUserFromPreferences(this)
        tvNameProfile.text = user?.name
        tvTelephoneProfile.text = user?.telephone
        tvNameProfileDetail.text = user?.name
        tvTelephoneProfileDetail.text = user?.telephone

        btnBack.setOnClickListener(this)
        btnEditName.setOnClickListener(this)
        btnEditTelephone.setOnClickListener(this)
        btnShowMyOrders.setOnClickListener(this)
        btnShowMenuCategories.setOnClickListener(this)
        btnShowMenus.setOnClickListener(this)
        btnShowExtraMenus.setOnClickListener(this)
        btnShowUsers.setOnClickListener(this)
        imgLogout.setOnClickListener(this)
        imgLogoutDetail.setOnClickListener(this)
    }

    private fun initComponents() {
        btnBack = findViewById(R.id.btn_profile_back)
        imgProfile = findViewById(R.id.img_profile_photo)
        tvNameProfile = findViewById(R.id.tv_profile_name)
        tvTelephoneProfile = findViewById(R.id.tv_profile_telephone)
        tvNameProfileDetail = findViewById(R.id.tv_profile_description_name)
        tvTelephoneProfileDetail = findViewById(R.id.tv_profile_description_telephone)
        btnEditName = findViewById(R.id.btn_profile_detail_name)
        btnEditTelephone = findViewById(R.id.btn_profile_detail_telephone)
        btnShowMyOrders = findViewById(R.id.btn_profile_detail_pesanan_saya)
        btnShowMenuCategories = findViewById(R.id.btn_profile_detail_kategori_menu)
        btnShowMenus = findViewById(R.id.btn_profile_detail_menu)
        btnShowExtraMenus = findViewById(R.id.btn_profile_detail_extra_menu)
        btnShowUsers = findViewById(R.id.btn_profile_detail_pelanggan)
        imgNameDetail = findViewById(R.id.img_profile_icon_name)
        imgTelephoneDetail = findViewById(R.id.img_profile_icon_telephone)
        imgMyOrdersDetail = findViewById(R.id.img_profile_icon_pesanan_saya)
        imgMenuCategoriesDetail = findViewById(R.id.img_profile_icon_daftar_kategori_menu)
        imgMenusDetail = findViewById(R.id.img_profile_icon_daftar_menu)
        imgExtraMenusDetail = findViewById(R.id.img_profile_icon_daftar_extra_menu)
        imgUsersDetail = findViewById(R.id.img_profile_icon_daftar_pelanggan)
        imgLogout = findViewById(R.id.img_profile_icon_logout)
        imgLogoutDetail = findViewById(R.id.img_profile_detail_logout)
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.btn_profile_back) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        if (v?.id == R.id.btn_profile_detail_name || v?.id == R.id.img_profile_icon_name) {
            val intent = Intent(this, UpdateNameUserActivity::class.java)
            startActivity(intent)
            finish()
        }

        if (v?.id == R.id.btn_profile_detail_telephone || v?.id == R.id.img_profile_icon_telephone) {
            val intent = Intent(this, UpdateTelephoneUserActivity::class.java)
            startActivity(intent)
            finish()
        }

        if (v?.id == R.id.btn_profile_detail_pesanan_saya || v?.id == R.id.img_profile_icon_pesanan_saya) {

        }

        if (v?.id == R.id.btn_profile_detail_kategori_menu || v?.id == R.id.img_profile_icon_daftar_kategori_menu) {
            val intent = Intent(this, MenuCategoryActivity::class.java)
            startActivity(intent)
            finish()
        }

        if (v?.id == R.id.btn_profile_detail_menu || v?.id == R.id.img_profile_icon_daftar_menu) {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
            finish()
        }

        if (v?.id == R.id.btn_profile_detail_extra_menu || v?.id == R.id.img_profile_icon_daftar_extra_menu) {
            val intent = Intent(this, ExtraMenuActivity::class.java)
            startActivity(intent)
            finish()
        }

        if (v?.id == R.id.btn_profile_detail_pelanggan || v?.id == R.id.img_profile_icon_daftar_pelanggan) {

        }

        if (v?.id == R.id.img_profile_icon_logout || v?.id == R.id.img_profile_detail_logout) {
//            Reset semua data
            OrderObject.user = null
            OrderObject.coupon = null
            OrderObject.totalPrice = 0.0f
            OrderObject.details = emptyList()

            val intent = Intent(this, SplashScreenActivity::class.java)
            startActivity(intent)
            finish()

        }
    }

    private fun getUserFromPreferences(context: Context): User? {
        val sharedPref =
            context.getSharedPreferences("USER_PREF", Context.MODE_PRIVATE) ?: return null
        val _id = sharedPref.getString("_id", null) ?: return null
        val name = sharedPref.getString("name", null) ?: return null
        val telephone = sharedPref.getString("telephone", null) ?: return null
        val isAdmin = sharedPref.getBoolean("isAdmin", false)
        return User(_id, name, telephone, isAdmin)
    }
}