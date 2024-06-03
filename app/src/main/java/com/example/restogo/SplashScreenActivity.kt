package com.example.restogo

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button

class SplashScreenActivity : Activity(), View.OnClickListener {
    private lateinit var btnPesan: Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)
        initComponents()
        btnPesan.setOnClickListener(this)
    }

    private fun initComponents() {
        btnPesan = findViewById(R.id.btn_splash_pesan)
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.btn_splash_pesan) {
            try {
                val intent = Intent(this, ChoiceRoleActivity::class.java)
                startActivity(intent)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            } finally {
                finish()
            }
        }
    }
}