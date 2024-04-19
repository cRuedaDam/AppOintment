package com.example.appointment.user.view.activities.menu.editProfile

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.Toast
import com.example.appointment.R
import com.example.appointment.commerce.view.activities.menu.CommerceMenu
import com.example.appointment.commerce.view.activities.menu.editProfile.ChangeCommerceAddress
import com.example.appointment.commerce.view.activities.menu.editProfile.ChangeCommerceEmail
import com.example.appointment.commerce.view.activities.menu.editProfile.ChangeCommerceName
import com.example.appointment.commerce.view.activities.menu.editProfile.ChangeCommercePassword
import com.example.appointment.commerce.view.activities.menu.editProfile.ChangeCommercePhone
import com.example.appointment.commerce.view.fragments.CustomAlertDialog
import com.example.appointment.commerce.viewModel.FireBaseManager
import com.example.appointment.common.view.Login
import com.example.appointment.databinding.ActivityCommerceEditProfileBinding
import com.example.appointment.databinding.ActivityUserEditProfileBinding
import com.example.appointment.databinding.ActivityUserMenuBinding
import com.example.appointment.user.view.activities.menu.UserMenu
import com.google.firebase.auth.FirebaseAuth

class UserEditProfile : AppCompatActivity() {

    private lateinit var binding: ActivityUserEditProfileBinding
    private var firebaseManager = FireBaseManager()
    private val currentUser = FirebaseAuth.getInstance().currentUser
    private val userId = currentUser?.uid.toString()
    override fun onCreate(savedInstanceState: Bundle?) {
        binding = ActivityUserEditProfileBinding.inflate(layoutInflater)
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        fillListView()
        goToMenu()
    }

    private fun fillListView() {
        var listViewItems = binding.listViewEditProfile
        var itemNames = arrayOf(
            "Cambiar nombre",
            "Cambiar E-Mail",
            "Cambiar número de teléfono",
            "Cambiar contraseña",
            "Eliminar cuenta"
        )

        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, itemNames)
        listViewItems.adapter = adapter

        listViewItems.setOnItemClickListener { _, _, position, _ ->
            when (position) {
                0 -> startActivity(Intent(this, ChangeUserName::class.java))
                1 -> startActivity(Intent(this, ChangeUserEmail::class.java))
                2 -> startActivity(Intent(this, ChangeUserPhone::class.java))
                3 -> startActivity(Intent(this, ChangeUserPassword::class.java))
                4 -> CustomAlertDialog.showAlertDialog(
                    this@UserEditProfile,
                    "¿Estás seguro de querer eliminar la cuenta definitivamente?",
                    onAccept = {
                        firebaseManager.deleteUser(userId)
                        Toast.makeText(
                            this,
                            "La cuenta ha sido eliminada correctamente",
                            Toast.LENGTH_SHORT
                        ).show()
                        val intent = Intent(this@UserEditProfile, Login::class.java)
                        startActivity(intent)
                    },
                    onCancel = {

                    }
                )
            }
        }

    }

    private fun goToMenu() {
        binding.ivBackArrow.setOnClickListener {
            val intent = Intent(this@UserEditProfile, UserMenu::class.java)
            startActivity(intent)
        }
    }
}