package com.example.appointment.common.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.example.appointment.R

class SplashScreen : AppCompatActivity() {

    private val splashTimeOut: Long = 2000 // Tiempo que durara el splash

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        splashToLogin()
    }

    /**
     * Este metodo deriva a la pantalla designada despues del splashScreen
     * */
    private fun splashToLogin() {
        Handler(Looper.getMainLooper()).postDelayed({
            val intent = Intent(this@SplashScreen, Login::class.java)
            startActivity(intent)
            finish()
        }, splashTimeOut)
    }
}