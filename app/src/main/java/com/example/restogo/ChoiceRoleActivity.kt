package com.example.restogo

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button

class ChoiceRoleActivity : Activity(), View.OnClickListener {
    private lateinit var btnRegister: Button
    private lateinit var btnLogin: Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_choice_role)
        initComponents()
        btnRegister.setOnClickListener(this)
        btnLogin.setOnClickListener(this)
    }

    private fun initComponents() {
        btnRegister = findViewById(R.id.btn_choice_register)
        btnLogin = findViewById(R.id.btn_choice_login)
    }

    override fun onClick(v: View?) {
        if (v?.id == R.id.btn_choice_register) {
            try {
                val intent = Intent(this, RegisterActivity::class.java)
                startActivity(intent)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            } finally {
                finish()
            }
        }

        if (v?.id == R.id.btn_choice_login) {
            try {
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            } finally {
                finish()
            }
        }
    }
}