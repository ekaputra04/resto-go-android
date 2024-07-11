package com.example.restogo

import android.annotation.SuppressLint
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
    private lateinit var btnShowCoupons: ImageView
    private lateinit var btnShowAllOrders: ImageView
    private lateinit var imgNameDetail: ImageView
    private lateinit var imgTelephoneDetail: ImageView
    private lateinit var imgMyOrdersDetail: ImageView
    private lateinit var imgMenuCategoriesDetail: ImageView
    private lateinit var imgMenusDetail: ImageView
    private lateinit var imgExtraMenusDetail: ImageView
    private lateinit var imgUsersDetail: ImageView
    private lateinit var imgCouponsDetail: ImageView
    private lateinit var imgAllOrdersDetail: ImageView
    private lateinit var imgLogout: ImageView
    private lateinit var imgLogoutDetail: ImageView
    private lateinit var tvMenuCategories: TextView
    private lateinit var tvMenus: TextView
    private lateinit var tvExtraMenus: TextView
    private lateinit var tvDaftarUsers: TextView
    private lateinit var tvDaftarCoupons: TextView
    private lateinit var tvDaftarOrders: TextView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        initComponents()

        val user = getUserFromPreferences(this)
        tvNameProfile.text = user?.name
        tvTelephoneProfile.text = user?.telephone
        tvNameProfileDetail.text = user?.name
        tvTelephoneProfileDetail.text = user?.telephone

        if (user?.isAdmin == false) {
            imgMenuCategoriesDetail.visibility = View.GONE
            imgMenusDetail.visibility = View.GONE
            imgExtraMenusDetail.visibility = View.GONE
            imgUsersDetail.visibility = View.GONE
            imgCouponsDetail.visibility = View.GONE
            imgAllOrdersDetail.visibility = View.GONE

            tvMenuCategories.visibility = View.GONE
            tvMenus.visibility = View.GONE
            tvExtraMenus.visibility = View.GONE
            tvDaftarUsers.visibility = View.GONE
            tvDaftarCoupons.visibility = View.GONE
            tvDaftarOrders.visibility = View.GONE

            btnShowMenuCategories.visibility = View.GONE
            btnShowMenus.visibility = View.GONE
            btnShowExtraMenus.visibility = View.GONE
            btnShowUsers.visibility = View.GONE
            btnShowCoupons.visibility = View.GONE
            btnShowAllOrders.visibility = View.GONE
        }

        btnBack.setOnClickListener(this)
        btnEditName.setOnClickListener(this)
        btnEditTelephone.setOnClickListener(this)
        btnShowMyOrders.setOnClickListener(this)
        btnShowMenuCategories.setOnClickListener(this)
        btnShowMenus.setOnClickListener(this)
        btnShowExtraMenus.setOnClickListener(this)
        btnShowUsers.setOnClickListener(this)
        btnShowCoupons.setOnClickListener(this)
        btnShowAllOrders.setOnClickListener(this)
        imgLogout.setOnClickListener(this)
        imgLogoutDetail.setOnClickListener(this)
    }

    @SuppressLint("CutPasteId")
    private fun initComponents() {
        btnBack = findViewById(R.id.btn_profile_back)
        imgProfile = findViewById(R.id.img_profile_photo)
        tvNameProfile = findViewById(R.id.tv_profile_name)
        tvTelephoneProfile = findViewById(R.id.tv_profile_telephone)

        imgNameDetail = findViewById(R.id.img_profile_icon_name)
        imgTelephoneDetail = findViewById(R.id.img_profile_icon_telephone)
        imgMyOrdersDetail = findViewById(R.id.img_profile_icon_pesanan_saya)
        imgMenuCategoriesDetail = findViewById(R.id.img_profile_icon_daftar_kategori_menu)
        imgMenusDetail = findViewById(R.id.img_profile_icon_daftar_menu)
        imgExtraMenusDetail = findViewById(R.id.img_profile_icon_daftar_extra_menu)
        imgUsersDetail = findViewById(R.id.img_profile_icon_daftar_pelanggan)
        imgCouponsDetail = findViewById(R.id.img_profile_icon_daftar_kupon)
        imgAllOrdersDetail = findViewById(R.id.img_profile_icon_daftar_pesanan)
        imgLogout = findViewById(R.id.img_profile_icon_logout)

        tvNameProfileDetail = findViewById(R.id.tv_profile_description_name)
        tvTelephoneProfileDetail = findViewById(R.id.tv_profile_description_telephone)

        btnEditName = findViewById(R.id.btn_profile_detail_name)
        btnEditTelephone = findViewById(R.id.btn_profile_detail_telephone)
        btnShowMyOrders = findViewById(R.id.btn_profile_detail_pesanan_saya)
        btnShowMenuCategories = findViewById(R.id.btn_profile_detail_kategori_menu)
        btnShowMenus = findViewById(R.id.btn_profile_detail_menu)
        btnShowExtraMenus = findViewById(R.id.btn_profile_detail_extra_menu)
        btnShowUsers = findViewById(R.id.btn_profile_detail_pelanggan)
        btnShowCoupons = findViewById(R.id.btn_profile_detail_kupon)
        btnShowAllOrders = findViewById(R.id.btn_profile_detail_pesanan)
        imgLogoutDetail = findViewById(R.id.btn_profile_detail_logout)

        tvMenuCategories = findViewById(R.id.tv_profile_kategori_menu)
        tvMenus = findViewById(R.id.tv_profile_daftar_menu)
        tvExtraMenus = findViewById(R.id.tv_profile_extra_menu)
        tvDaftarUsers = findViewById(R.id.tv_profile_daftar_pelanggan)
        tvDaftarCoupons = findViewById(R.id.tv_profile_daftar_kupon)
        tvDaftarOrders = findViewById(R.id.tv_profile_daftar_pesanan)
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.btn_profile_back) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

        if (v?.id == R.id.btn_profile_detail_name || v?.id == R.id.img_profile_icon_name) {
            val intent = Intent(this, UpdateNameUserActivity::class.java)
            startActivity(intent)
        }

        if (v?.id == R.id.btn_profile_detail_telephone || v?.id == R.id.img_profile_icon_telephone) {
            val intent = Intent(this, UpdateTelephoneUserActivity::class.java)
            startActivity(intent)
        }

        if (v?.id == R.id.btn_profile_detail_pesanan_saya || v?.id == R.id.img_profile_icon_pesanan_saya) {
            val intent = Intent(this, MyOrderActivity::class.java)
            startActivity(intent)
        }

        if (v?.id == R.id.btn_profile_detail_kategori_menu || v?.id == R.id.img_profile_icon_daftar_kategori_menu) {
            val intent = Intent(this, MenuCategoryActivity::class.java)
            startActivity(intent)
        }

        if (v?.id == R.id.btn_profile_detail_menu || v?.id == R.id.img_profile_icon_daftar_menu) {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
        }

        if (v?.id == R.id.btn_profile_detail_extra_menu || v?.id == R.id.img_profile_icon_daftar_extra_menu) {
            val intent = Intent(this, ExtraMenuActivity::class.java)
            startActivity(intent)
        }

        if (v?.id == R.id.btn_profile_detail_pelanggan || v?.id == R.id.img_profile_icon_daftar_pelanggan) {
            val intent = Intent(this, UserActivity::class.java)
            startActivity(intent)
        }

        if (v?.id == R.id.btn_profile_detail_kupon || v?.id == R.id.img_profile_icon_daftar_kupon) {
            val intent = Intent(this, CouponActivity::class.java)
            startActivity(intent)
        }

        if (v?.id == R.id.btn_profile_detail_pesanan || v?.id == R.id.img_profile_icon_daftar_pesanan) {
            val intent = Intent(this, AllOrderActivity::class.java)
            startActivity(intent)
        }

        if (v?.id == R.id.img_profile_icon_logout || v?.id == R.id.btn_profile_detail_logout) {
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