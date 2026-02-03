package onyx.movil.fragments.tabs

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
import onyx.movil.databinding.FragmentRegisterBinding
import onyx.movil.local.SessionManager
import onyx.movil.providers.UserProvider
import onyx.movil.retrofit.RetrofitInstance
import onyx.movil.ui.states.UserUiState
import onyx.movil.ui.viewmodels.UserViewModel
import onyx.movil.ui.viewmodels.factories.UserViewModelFactory
import onyx.movil.utils.hideKeyboard

class RegisterFragment : Fragment() {
    private lateinit var binding: FragmentRegisterBinding

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
        binding = FragmentRegisterBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val sessionManager = SessionManager(requireContext())

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                userViewModel.uiState.collect { state ->
                    when (state) {
                        UserUiState.Idle -> Unit

                        UserUiState.Loading -> {
                            // desactiva el botón
                            binding.btnRegister.isEnabled = false
                        }

                        is UserUiState.SuccessRegister -> {
                            val user = state.user

                            // guarda el id en la sesión
                            sessionManager.saveSession(user.id)

                            binding.btnRegister.isEnabled = true
                            findNavController().navigate(R.id.gruposFragment)
                        }

                        is UserUiState.Error -> {
                            binding.btnRegister.isEnabled = true
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

        binding.btnRegister.setOnClickListener {
            hideKeyboard()

            val user = binding.inputEditTextNombre.text.toString()
            val email = binding.inputEditTextCorreo.text.toString()
            val password = binding.inputEditTextPassword.text.toString()
            val passwordConfirm = binding.inputEditTextConfirmPassword.text.toString()

            if (passwordConfirm == password) {

                userViewModel.register(user, email, password)

            }
        }
    }
}
