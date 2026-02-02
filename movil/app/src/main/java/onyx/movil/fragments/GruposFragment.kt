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
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import onyx.movil.R
import onyx.movil.databinding.FragmentGruposBinding
import onyx.movil.local.SessionManager
import onyx.movil.providers.UserProvider
import onyx.movil.retrofit.RetrofitInstance
import onyx.movil.ui.recyclerview.GrupoAdapter
import onyx.movil.ui.states.UserUiState
import onyx.movil.ui.viewmodels.UserViewModel
import onyx.movil.ui.viewmodels.factories.UserViewModelFactory

class GruposFragment : Fragment() {
    private lateinit var binding: FragmentGruposBinding

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
        binding = FragmentGruposBinding.inflate(inflater, container, false)
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

                            // layout manager
                            binding.rv.layoutManager = LinearLayoutManager(requireContext())
                        }

                        is UserUiState.SuccessGetUsuario -> {

                            // obtiene los grupos
                            val grupos = state.usuario.grupos

                            if (!grupos.isEmpty()) {

                                val adapter = GrupoAdapter(requireContext(), grupos)
                                binding.rv.adapter = adapter

                                // cada grupo
                                adapter.setOnItemClickListener(object: GrupoAdapter.OnItemClickListener {
                                    override fun onItemClick(position: Int) {

                                        val grupo = grupos[position]

                                        // argumentos del fragment
                                        val bundle = Bundle().apply {
                                            putLong("idGrupo", grupo.id)
                                            putString("nombreGrupo", grupo.nombre)
                                            putString("descGrupo", grupo.descripcion)
                                            putString("fechaCreacionGrupo", grupo.fechaCreacion)
                                            //todo obtener el nombre de usuario
                                            //putString("creadorGrupo", grupo.creador.nombreUsuario)
                                        }

                                        findNavController().navigate(R.id.action_gruposFragment_to_grupoDetailsFragment, bundle)
                                    }
                                })

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
