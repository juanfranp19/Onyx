package onyx.movil

import android.os.Bundle
import android.view.View
import androidx.activity.addCallback
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.navigation.findNavController
import androidx.navigation.fragment.NavHostFragment
import kotlinx.coroutines.launch
import onyx.movil.databinding.ActivityMainBinding
import onyx.movil.local.SessionManager

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // nav controller
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController

        /* SESSION MANAGER */

        val sessionManager = SessionManager(this)

        lifecycleScope.launch {
            // obtiene el ID del usuario de la sesión
            if (sessionManager.getUserId() != null) {
                navController.navigate(R.id.gruposFragment)
            }
        }

        /* BOTTOM NAVIGATION MENU */

        binding.bottomNavigationView.setOnItemSelectedListener { item ->
            val currentDestination = navController.currentDestination?.id

            // maneja a qué fragment navega en cada item del menú
            when (item.itemId) {
                R.id.bnm_grupos -> {
                    if (currentDestination != R.id.gruposFragment) {
                        navController.navigate(R.id.gruposFragment)
                    }
                    true
                }
                R.id.bnm_tareas -> {
                    if (currentDestination != R.id.tareasFragment) {
                        navController.navigate(R.id.tareasFragment)
                    }
                    true
                }
                R.id.bnm_perfil -> {
                    if (currentDestination != R.id.perfilFragment) {
                        navController.navigate(R.id.perfilFragment)
                    }
                    true
                }
                else -> false
            }
        }

        /* ELEMENTOS MOSTRADOS EN CADA FRAGMENT */

        navController.addOnDestinationChangedListener { _, destination, _ ->
            // muestra el bnm
            binding.bottomNavigationView.visibility = View.VISIBLE
            when (destination.id) {
                // grupos
                R.id.gruposFragment -> {
                    binding.bottomNavigationView.selectedItemId = R.id.bnm_grupos
                }
                // tareas
                R.id.tareasFragment -> {
                    binding.bottomNavigationView.selectedItemId = R.id.bnm_tareas
                }
                // perfil
                R.id.perfilFragment -> {
                    binding.bottomNavigationView.selectedItemId = R.id.bnm_perfil
                }
                // login register
                R.id.tabLoginRegisterFragment -> {
                    // esconde el bnm
                    binding.bottomNavigationView.visibility = View.GONE
                }
                else -> {}
            }
        }

        /* MANEJA QUÉ FRAGMENT NO PUEDE IR PARA ATRÁS EN LA NAVEGACIÓN */

        onBackPressedDispatcher.addCallback(this) {
            val containerView = findNavController(R.id.fragmentContainerView)

            when (containerView.currentDestination?.id) {
                in listOf(
                    R.id.tabLoginRegisterFragment,
                    R.id.gruposFragment
                ) -> {
                    // cierra la app
                    finish()
                }

                in listOf(
                    R.id.tareasFragment,
                    R.id.perfilFragment
                ) -> {
                    navController.navigate(R.id.gruposFragment)
                }

                else -> {
                    // continua navegando
                    containerView.navigateUp()
                }
            }
        }
    }
}
