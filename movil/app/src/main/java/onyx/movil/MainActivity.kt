package onyx.movil

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.fragment.NavHostFragment
import onyx.movil.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // nav controller
        val navHostFragment = supportFragmentManager.findFragmentById(R.id.fragmentContainerView) as NavHostFragment
        val navController = navHostFragment.navController

        /* TOOLBAR */

        this.setSupportActionBar(binding.toolbar)

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

            when (destination.id) {
                R.id.gruposFragment -> {
                    binding.toolbar.title = this.getString(R.string.menu_grupos)
                    mostrarScaffold()
                    binding.bottomNavigationView.selectedItemId = R.id.bnm_grupos
                }
                R.id.tareasFragment -> {
                    binding.toolbar.title = this.getString(R.string.menu_tareas)
                    mostrarScaffold()
                    binding.bottomNavigationView.selectedItemId = R.id.bnm_tareas
                }
                R.id.perfilFragment -> {
                    binding.toolbar.title = this.getString(R.string.menu_perfil)
                    mostrarScaffold()
                    binding.bottomNavigationView.selectedItemId = R.id.bnm_perfil
                }
                R.id.tabLoginRegisterFragment -> {
                    esconderScaffold()
                }
                else -> {
                    binding.bottomNavigationView.selectedItemId = R.id.bnm_grupos
                }
            }
        }
    }

    private fun mostrarScaffold() {
        binding.appBarLayout.visibility = View.VISIBLE
        binding.bottomNavigationView.visibility = View.VISIBLE
    }

    private fun esconderScaffold() {
        binding.appBarLayout.visibility = View.GONE
        binding.bottomNavigationView.visibility = View.GONE
    }
}
