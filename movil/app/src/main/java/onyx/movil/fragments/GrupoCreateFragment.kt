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
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import onyx.movil.R
import onyx.movil.databinding.FragmentGrupoCreateBinding
import onyx.movil.local.SessionManager
import onyx.movil.providers.GrupoProvider
import onyx.movil.retrofit.RetrofitInstance
import onyx.movil.ui.states.GrupoUiState
import onyx.movil.ui.viewmodels.GrupoViewModel
import onyx.movil.ui.viewmodels.factories.GrupoViewModelFactory

class GrupoCreateFragment : Fragment() {
    private lateinit var binding: FragmentGrupoCreateBinding

    private val grupoViewModel: GrupoViewModel by lazy {
        val provider = GrupoProvider(RetrofitInstance.api)
        val factory = GrupoViewModelFactory(provider)
        ViewModelProvider(this, factory)[GrupoViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGrupoCreateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sessionManager = SessionManager(requireContext())

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                grupoViewModel.uiState.collect { state ->
                    when (state) {
                        GrupoUiState.Idle -> Unit

                        GrupoUiState.Loading -> {
                            // desactiva el botón
                            binding.btnCrearGrupo.isEnabled = false
                        }

                        is GrupoUiState.SuccessPostGrupo -> {
                            val grupo = state.grupo

                            // argumentos del fragment
                            val bundle = Bundle().apply {
                                putLong("idGrupo", grupo.id)
                                putString("nombreGrupo", grupo.nombre)
                                putString("descGrupo", grupo.descripcion)
                                putString("fechaCreacionGrupo", grupo.fechaCreacion)
                                //todo obtener el nombre de usuario
                                //putString("creadorGrupo", grupo.creador.nombreUsuario)
                            }

                            binding.btnCrearGrupo.isEnabled = true
                            findNavController().navigate(R.id.action_grupoCreateFragment_to_grupoDetailsFragment, bundle)
                        }

                        is GrupoUiState.Error -> {
                            binding.btnCrearGrupo.isEnabled = true
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

        binding.btnCrearGrupo.setOnClickListener {
            lifecycleScope.launch {
                val creadorId = sessionManager.getUserId()

                val nombre = binding.inputNombre.text.toString()
                val desc = binding.inputDesc.text.toString()

                grupoViewModel.postGrupo(nombre, desc, creadorId)
            }
        }
    }
}
