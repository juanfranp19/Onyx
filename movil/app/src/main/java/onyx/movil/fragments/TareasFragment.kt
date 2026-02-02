package onyx.movil.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import onyx.movil.databinding.FragmentTareasBinding
import onyx.movil.local.SessionManager
import onyx.movil.models.Tarea
import onyx.movil.providers.UserProvider
import onyx.movil.retrofit.RetrofitInstance
import onyx.movil.ui.recyclerview.TareaAdapter
import onyx.movil.ui.states.UserUiState
import onyx.movil.ui.viewmodels.UserViewModel
import onyx.movil.ui.viewmodels.factories.UserViewModelFactory

class TareasFragment : Fragment() {
    private lateinit var binding: FragmentTareasBinding

    private val userViewModel: UserViewModel by lazy {
        val provider = UserProvider(RetrofitInstance.api)
        val factory = UserViewModelFactory(provider)
        ViewModelProvider(this, factory)[UserViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTareasBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // session manager
        val sessionManager = SessionManager(requireContext())

        viewLifecycleOwner.lifecycleScope.launch {

            // obtiene el usuario
            val userId = sessionManager.getUserId()
            userViewModel.getUsuario(userId)

            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                userViewModel.uiState.collect { state ->
                    when (state) {
                        UserUiState.Idle -> Unit

                        UserUiState.Loading -> {
                            // aparece la progressbar y desaparece el rv
                            binding.progressBar.visibility = View.VISIBLE
                            binding.rv.visibility = View.GONE
                        }

                        is UserUiState.SuccessGetUsuario -> {

                            // lista tareas
                            var tareas: MutableList<Tarea> = mutableListOf()

                            // obtiene los grupos
                            val grupos = state.usuario.grupos

                            // carga la lista de tareas
                            for (grupo in grupos) {
                                for (tarea in grupo.tareas) {
                                    tareas.add(tarea)
                                }
                            }

                            if (!tareas.isEmpty()) {

                                val adapter = TareaAdapter(requireContext(), tareas)
                                binding.rv.layoutManager = LinearLayoutManager(requireContext())
                                binding.rv.adapter = adapter


                                // aparece el rv
                                binding.rv.visibility = View.VISIBLE

                            } else {
                                // aparece mensaje empty
                                binding.mensajeEmpty.visibility = View.VISIBLE
                            }

                            // desaparece progressbar
                            binding.progressBar.visibility = View.GONE
                        }

                        is UserUiState.Error -> {
                            binding.progressBar.visibility = View.GONE
                            binding.rv.visibility = View.VISIBLE

                            Snackbar.make(
                                binding.root,
                                state.message,
                                Snackbar.LENGTH_LONG
                            ).show()
                        }

                        else -> {}
                    }
                }
            }
        }
    }
}
