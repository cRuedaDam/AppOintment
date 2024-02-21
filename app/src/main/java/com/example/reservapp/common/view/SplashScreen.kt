package com.example.reservapp.common.view

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import com.example.reservapp.R

class SplashScreen : AppCompatActivity() {

    private val SplasTimeOut: Long = 2000 // Este es el tiempo que durará el SplashScreen
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash_screen)

        //A través de este método el SplashScreen viaja a la pantalla indicada por medio de un intent
        Handler().postDelayed({
            val intent = Intent(this@SplashScreen, Login::class.java)
            startActivity(intent)
            finish()
        }, SplasTimeOut)
    }
}