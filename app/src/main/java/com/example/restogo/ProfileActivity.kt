package com.example.restogo

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView

class ProfileActivity : Activity(), View.OnClickListener {
    private lateinit var btnBack: ImageView
    private lateinit var imgProfile: ImageView
    private lateinit var tvNameProfile: TextView
    private lateinit var tvTelephoneProfile: TextView
    private lateinit var btnEditName: ImageView
    private lateinit var btnEditTelephone: ImageView
    private lateinit var btnShowMyOrders: ImageView
    private lateinit var btnShowMenuCategories: ImageView
    private lateinit var btnShowMenus: ImageView
    private lateinit var btnShowExtraMenus: ImageView
    private lateinit var btnShowUsers: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_profile)
        initComponents()
        btnBack.setOnClickListener(this)
        btnEditName.setOnClickListener(this)
        btnEditTelephone.setOnClickListener(this)
        btnShowMyOrders.setOnClickListener(this)
        btnShowMenuCategories.setOnClickListener(this)
        btnShowMenus.setOnClickListener(this)
        btnShowExtraMenus.setOnClickListener(this)
        btnShowUsers.setOnClickListener(this)
    }

    private fun initComponents() {
        btnBack = findViewById(R.id.btn_profile_back)
        imgProfile = findViewById(R.id.img_profile_photo)
        tvNameProfile = findViewById(R.id.tv_profile_name)
        tvTelephoneProfile = findViewById(R.id.tv_profile_telephone)
        btnEditName = findViewById(R.id.btn_profile_detail_name)
        btnEditTelephone = findViewById(R.id.btn_profile_detail_telephone)
        btnShowMyOrders = findViewById(R.id.btn_profile_detail_pesanan_saya)
        btnShowMenuCategories = findViewById(R.id.btn_profile_detail_kategori_menu)
        btnShowMenus = findViewById(R.id.btn_profile_detail_menu)
        btnShowExtraMenus = findViewById(R.id.btn_profile_detail_extra_menu)
        btnShowUsers = findViewById(R.id.btn_profile_detail_pelanggan)
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.btn_profile_back) {
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
            finish()
        }

        if (v?.id == R.id.btn_profile_detail_name) {

        }

        if (v?.id == R.id.btn_profile_detail_telephone) {

        }

        if (v?.id == R.id.btn_profile_detail_pesanan_saya) {

        }

        if (v?.id == R.id.btn_profile_detail_kategori_menu) {
            val intent = Intent(this, MenuCategoryActivity::class.java)
            startActivity(intent)
            finish()
        }

        if (v?.id == R.id.btn_profile_detail_menu) {
            val intent = Intent(this, MenuActivity::class.java)
            startActivity(intent)
            finish()
        }

        if (v?.id == R.id.btn_profile_detail_extra_menu) {

        }

        if (v?.id == R.id.btn_profile_detail_pelanggan) {

        }
    }
}