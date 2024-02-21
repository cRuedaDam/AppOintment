package com.example.reservapp.user

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import androidx.core.view.get
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.example.reservapp.R
import com.example.reservapp.common.view.Login
import com.example.reservapp.databinding.ActivityCustomerHomeBinding
import com.google.firebase.auth.FirebaseAuth


class CustomerHome : AppCompatActivity() {
    private lateinit var binding: ActivityCustomerHomeBinding
    private lateinit var navController: NavController

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomerHomeBinding.inflate(layoutInflater)
        setContentView(binding.root)//fragment de CustomerHome

        //se busca el princio del nav controler para que se pueda poner el el bottomMenu para poder navegar por el menu de este
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragment_user_home) as NavHostFragment
        navController = navHostFragment.navController

        val bottomNav = binding.bottomBarNavUsr
        val toolBar = binding.toolbarLogout
        //se le asigna a el bottomMenu el navControler para que sepa los caminos que tiene que seguir dictado por el navigation_fragments
        bottomNav.setupWithNavController(navController)

        //cuando se clique en alguno de los dos iconos del menu superior hace una cosa o otra si se presiona el de lougout se cierra la sesion y se borra el stack
        //de fragments para que al darle al boton de atras vuelva a un framgento de cuando estaba logeado el usuario. Si se pulsa la x depende del camino si es el más largo de cuando se
        //esta pidiendo una cita al clicar a la x vuelve al home.
        toolBar.setOnMenuItemClickListener {menuItem ->
            when(menuItem.itemId){
                R.id.logoutBtn -> {
                    val intent = Intent(this@CustomerHome, Login::class.java)
                    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK
                    startActivity(intent)
                    FirebaseAuth.getInstance().signOut()
                }
                R.id.exitFragment -> {
                    if(navController.currentDestination?.id == R.id.customerListAppoint ||
                        navController.currentDestination?.id  == R.id.customerChoiceAppoint||
                        navController.currentDestination?.id  == R.id.customerListAvailability ||
                        navController.currentDestination?.id  == R.id.customerSummaryAppoint) {
                        navController.popBackStack(R.id.customerHome, false)
                    }
                    else{
                        navController.popBackStack()
                    }
                }
            }
            true
        }

        //dependiendo del fragment en el que este se muestra el icono de logout o de x de salir y muestra o oculta el menu inferior
        navController.addOnDestinationChangedListener{
                _, destination, _ ->
            toolBar.menu[0].isVisible = false
            toolBar.menu[1].isVisible = false
            if(destination.id == R.id.editProfile ||
                destination.id == R.id.favorites ||
                destination.id == R.id.customerHome ||
                destination.id == R.id.searchCommerceOrService ||
                destination.id == R.id.historial) {
                toolBar.menu[1].isVisible = true
                bottomNav.visibility = View.VISIBLE
            } else {
                toolBar.menu[0].isVisible = true
                toolBar.menu[1].isVisible = false
                bottomNav.visibility = View.GONE
            }
        }
    }
}