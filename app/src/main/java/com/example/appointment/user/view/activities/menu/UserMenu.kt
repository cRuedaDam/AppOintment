package com.example.appointment.user.view.activities.menu

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.appointment.commerce.view.fragments.CustomAlertDialog
import com.example.appointment.common.view.Login
import com.example.appointment.databinding.ActivityUserMenuBinding
import com.example.appointment.user.view.activities.home.UserHome
import com.example.appointment.user.view.activities.menu.editProfile.UserEditProfile
import com.google.firebase.auth.FirebaseAuth

class UserMenu : AppCompatActivity() {
    private lateinit var binding: ActivityUserMenuBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityUserMenuBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)
        goToEditProfile()
        goToHistory()
        goToUserHome()
        //goToSpecialities()
        userLogOut()

    }
    private fun userLogOut() {
        binding.txtLogout.setOnClickListener{
            CustomAlertDialog.showAlertDialog(
                this@UserMenu,
                "¿Estás seguro de querer cerrar sesión?",
                onAccept = {
                    FirebaseAuth.getInstance().signOut()
                    val intent = Intent(this@UserMenu, Login::class.java)
                    startActivity(intent)
                },
                onCancel = {

                }
            )
        }
    }

    private fun goToEditProfile() {
        binding.txtEditProfile.setOnClickListener {
            val intent = Intent(this@UserMenu, UserEditProfile::class.java)
            startActivity(intent)
        }
    }

    private fun goToUserHome() {
        binding.ivBackArrow.setOnClickListener {
            val intent = Intent(this@UserMenu, UserHome::class.java)
            startActivity(intent)
        }
    }

    private fun goToHistory() {
        binding.txtHistory.setOnClickListener {
            val intent = Intent(this@UserMenu, UserHistory::class.java)
            startActivity(intent)
        }
    }

    /*private fun goToSpecialities() {
        binding.txtSpecialities.setOnClickListener {
            val intent = Intent(this@CommerceMenu, CommerceSpecialities::class.java)
            startActivity(intent)
        }
    }*/
}