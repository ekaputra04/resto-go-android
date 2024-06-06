package com.example.restogo

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView

class MainActivity : Activity(), View.OnClickListener {
    private lateinit var tvNameUser: TextView
    private lateinit var tvRoleUser: TextView
    private lateinit var tvSilahkanPilihMenu: TextView
    private lateinit var imgProfile: ImageView
    private lateinit var imgCart: ImageView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        initComponents()
        imgProfile.setOnClickListener(this)
        imgCart.setOnClickListener(this)

        val user = getUserFromPreferences(this)
        tvNameUser.text = user?.name
        tvSilahkanPilihMenu.text = "Hai ${user?.name}, Silahkan Pilih Menu"

        if (user?.isAdmin == true) {
            tvRoleUser.text = "Admin"
        } else {
            tvRoleUser.text = "Pelanggan"
        }

    }

    private fun initComponents() {
        tvNameUser = findViewById(R.id.tv_home_name)
        tvRoleUser = findViewById(R.id.tv_home_role)
        tvSilahkanPilihMenu = findViewById(R.id.tv_home_silahkan_pilih_menu)
        imgProfile = findViewById(R.id.img_home_profile)
        imgCart = findViewById(R.id.img_home_cart)
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

    override fun onClick(v: View?) {
        if (v?.id == R.id.img_home_profile) {
            val intent = Intent(this, ProfileActivity::class.java)
            startActivity(intent)
        }

        if (v?.id == R.id.img_home_cart) {
            val intent = Intent(this, CartActivity::class.java)
            startActivity(intent)
        }
    }
}
