package com.example.restogo

import android.app.Activity
import android.content.Intent
import android.os.Bundle

class SplashScreenActivity : Activity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        val thread = Thread {
            try {
                Thread.sleep(2000)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            } finally {
                startActivity(Intent(this, ChoiceRoleActivity::class.java))
                finish()
            }
        }
        thread.start()
    }
}