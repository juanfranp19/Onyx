package onyx.movil.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import onyx.movil.R
import onyx.movil.databinding.FragmentPerfilBinding
import onyx.movil.local.SessionManager
import onyx.movil.providers.UserProvider
import onyx.movil.retrofit.RetrofitInstance
import onyx.movil.ui.states.UserUiState
import onyx.movil.ui.viewmodels.UserViewModel
import onyx.movil.ui.viewmodels.factories.UserViewModelFactory
import onyx.movil.utils.formatearFechaHora

class PerfilFragment : Fragment() {
    private lateinit var binding: FragmentPerfilBinding

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
        binding = FragmentPerfilBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sessionManager = SessionManager(requireContext())
        var userId: Long? = null

        // obtiene datos del usuario
        lifecycleScope.launch {
            userId = sessionManager.getUserId()
            userViewModel.getUsuario(userId)
        }

        binding.floatingActionButtonLogout.setOnClickListener {
            // alert
            AlertDialog.Builder(requireContext())
                .setTitle(getString(R.string.dialog_cerrar_sesion))
                .setPositiveButton(getString(R.string.btn_si)) { _, _ ->
                    // elimina la sesión y navega al login
                    lifecycleScope.launch {
                        sessionManager.clearSession()
                        findNavController().navigate(R.id.tabLoginRegisterFragment)
                    }
                }
                .setNegativeButton(getString(R.string.btn_cancelar), null)
                .show()
        }

        binding.btnGuardarCambios.setOnClickListener {
            // datos de los input
            val nombre = binding.inputNombre.text.toString()
            val email = binding.inputEmail.text.toString()
            val passwd = binding.inputCambiarPasswd.text.toString()

            // actualiza
            userViewModel.putUser(userId, nombre, email, passwd)
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                userViewModel.uiState.collect { state ->
                    when (state) {
                        UserUiState.Idle -> Unit

                        UserUiState.Loading -> {
                            cargando()
                        }

                        is UserUiState.SuccessGetUsuario -> {
                            val user = state.usuario

                            val fechaRegistro = formatearFechaHora(user.fechaRegistro)

                            // datos en los campos
                            binding.registro.text = getString(R.string.data_creado_el) + " $fechaRegistro"
                            binding.inputNombre.setText(user.nombreUsuario)
                            binding.inputEmail.setText(user.email)

                            cargado()
                        }

                        is UserUiState.SuccessPutUser -> {
                            binding.inputCambiarPasswd.setText("")
                            cargado()

                            // alert
                            AlertDialog.Builder(requireContext())
                                .setTitle(getString(R.string.dialog_actualizado_correctamente))
                                .setPositiveButton(getString(R.string.btn_ok)){ _, _ ->
                                    //
                                }
                                .show()
                        }

                        is UserUiState.Error -> {
                            cargado()

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

    private fun cargando() {
        binding.progressBar.visibility = View.VISIBLE
        binding.contenido.visibility = View.GONE
    }

    private fun cargado() {
        binding.progressBar.visibility = View.GONE
        binding.contenido.visibility = View.VISIBLE
    }
}
