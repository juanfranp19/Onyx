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
import onyx.movil.databinding.FragmentGrupoCreateBinding
import onyx.movil.local.SessionManager
import onyx.movil.models.User
import onyx.movil.providers.GrupoProvider
import onyx.movil.providers.UserProvider
import onyx.movil.retrofit.RetrofitInstance
import onyx.movil.ui.recyclerview.UserAdapter
import onyx.movil.ui.states.GrupoUiState
import onyx.movil.ui.states.UserUiState
import onyx.movil.ui.viewmodels.GrupoViewModel
import onyx.movil.ui.viewmodels.UserViewModel
import onyx.movil.ui.viewmodels.factories.GrupoViewModelFactory
import onyx.movil.ui.viewmodels.factories.UserViewModelFactory

class GrupoCreateFragment : Fragment() {
    private lateinit var binding: FragmentGrupoCreateBinding

    private val grupoViewModel: GrupoViewModel by lazy {
        val provider = GrupoProvider(RetrofitInstance.api)
        val factory = GrupoViewModelFactory(provider)
        ViewModelProvider(this, factory)[GrupoViewModel::class.java]
    }

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
        binding = FragmentGrupoCreateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sessionManager = SessionManager(requireContext())

        binding.usersRv.layoutManager = LinearLayoutManager(requireContext())
        val users: ArrayList<User> = ArrayList()

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
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
                launch {
                    userViewModel.uiState.collect { state ->
                        when (state) {
                            UserUiState.Idle -> Unit
                            UserUiState.Loading -> {
                                binding.btnAddUser.isEnabled = false
                            }
                            is UserUiState.SuccessGetUsuario -> {
                                val usuarioAdd = state.usuario
                                // añade el usuario a la lista si no está en ella y además no es el mismo usuario que crea el grupo
                                if (!users.contains(usuarioAdd) && usuarioAdd.id != sessionManager.getUserId()) {
                                    users.add(usuarioAdd)
                                } else {
                                    // notifica al usuario que ya está añadido
                                    Snackbar.make(
                                        binding.root,
                                        getString(R.string.err_user_already_add),
                                        Snackbar.LENGTH_LONG
                                    ).show()
                                }

                                // actualiza rv
                                val userAdapter = UserAdapter(requireContext(), users)
                                binding.usersRv.adapter = userAdapter

                                binding.btnAddUser.isEnabled = true
                            }
                            is UserUiState.Error -> {
                                Snackbar.make(
                                    binding.root,
                                    state.message,
                                    Snackbar.LENGTH_LONG
                                ).show()
                                binding.btnAddUser.isEnabled = true
                            }
                            else -> {}
                        }
                    }
                }
            }
        }

        binding.btnAddUser.setOnClickListener {
            val username = binding.inputUser.text.toString()

            // obtiene el usuario del input
            userViewModel.getUsuarioByUsername(username)
        }

        binding.btnCrearGrupo.setOnClickListener {

            val usersId = users.map { it.id }

            lifecycleScope.launch {
                val creadorId = sessionManager.getUserId()

                val nombre = binding.inputNombre.text.toString()
                val desc = binding.inputDesc.text.toString()

                grupoViewModel.postGrupo(nombre, desc, creadorId, usersId)
            }
        }
    }
}
