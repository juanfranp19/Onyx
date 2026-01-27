package onyx.movil

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
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

        /* FLOATING ACTION BUTTON*/

        binding.floatingActionButton.setOnClickListener {

            val currentDestination = navController.currentDestination?.id

            if (currentDestination == R.id.gruposFragment) {
                navController.navigate(R.id.grupoCreateFragment)
            }

            if (currentDestination == R.id.tareasFragment) {
                navController.navigate(R.id.tareaCreateFragment)
            }
        }

        /* ELEMENTOS MOSTRADOS EN CADA FRAGMENT */

        navController.addOnDestinationChangedListener { _, destination, _ ->
            mostrarScaffold()
            esconderFloatingButton()
            when (destination.id) {
                R.id.gruposFragment -> {
                    binding.toolbar.title = this.getString(R.string.menu_grupos)
                    mostrarFloatingButton()
                    binding.bottomNavigationView.selectedItemId = R.id.bnm_grupos
                }
                R.id.grupoCreateFragment -> {
                    binding.toolbar.title = this.getString(R.string.menu_grupo_crear)
                }
                R.id.tareasFragment -> {
                    binding.toolbar.title = this.getString(R.string.menu_tareas)
                    mostrarFloatingButton()
                    binding.bottomNavigationView.selectedItemId = R.id.bnm_tareas
                }
                R.id.tareaCreateFragment -> {
                    binding.toolbar.title = this.getString(R.string.menu_tarea_crear)
                }
                R.id.perfilFragment -> {
                    binding.toolbar.title = this.getString(R.string.menu_perfil)
                    binding.bottomNavigationView.selectedItemId = R.id.bnm_perfil
                }
                R.id.tabLoginRegisterFragment -> {
                    esconderScaffold()
                }
                else -> {}
            }
        }
    }

    private fun mostrarScaffold() {
        binding.appBarLayout.visibility = View.VISIBLE
        binding.bottomNavigationView.visibility = View.VISIBLE

        val params = binding.fragmentContainerView.layoutParams as ConstraintLayout.LayoutParams
        params.topToTop = ConstraintLayout.LayoutParams.UNSET
        params.bottomToBottom = ConstraintLayout.LayoutParams.UNSET
        params.topToBottom = binding.appBarLayout.id
        params.bottomToTop = binding.bottomNavigationView.id
        binding.fragmentContainerView.layoutParams = params
    }

    private fun esconderScaffold() {
        binding.appBarLayout.visibility = View.GONE
        binding.bottomNavigationView.visibility = View.GONE

        val params = binding.fragmentContainerView.layoutParams as ConstraintLayout.LayoutParams
        params.topToBottom = ConstraintLayout.LayoutParams.UNSET
        params.bottomToTop = ConstraintLayout.LayoutParams.UNSET
        params.topToTop = ConstraintLayout.LayoutParams.PARENT_ID
        params.bottomToBottom = ConstraintLayout.LayoutParams.PARENT_ID
        binding.fragmentContainerView.layoutParams = params
    }

    private fun mostrarFloatingButton() {
        binding.floatingActionButton.visibility = View.VISIBLE
    }

    private fun esconderFloatingButton() {
        binding.floatingActionButton.visibility = View.GONE
    }
}
