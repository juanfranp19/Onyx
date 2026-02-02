package onyx.movil.fragments.tabs

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import onyx.movil.R
import onyx.movil.databinding.FragmentLoginBinding
import onyx.movil.local.SessionManager
import onyx.movil.providers.UserProvider
import onyx.movil.retrofit.RetrofitInstance
import onyx.movil.ui.states.UserUiState
import onyx.movil.ui.viewmodels.UserViewModel
import onyx.movil.ui.viewmodels.factories.UserViewModelFactory
import onyx.movil.utils.hideKeyboard

class LoginFragment : Fragment() {
    private lateinit var binding: FragmentLoginBinding

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
        binding = FragmentLoginBinding.inflate(inflater, container, false)
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
                            binding.btnLogin.isEnabled = false
                        }

                        is UserUiState.SuccessLogin -> {
                            val user = state.user

                            // guarda el id en la sesión
                            sessionManager.saveSession(user.id)

                            binding.btnLogin.isEnabled = true
                            findNavController().navigate(R.id.gruposFragment)
                        }

                        is UserUiState.Error -> {
                            binding.btnLogin.isEnabled = true
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

        binding.btnLogin.setOnClickListener {
            hideKeyboard()

            val user = binding.inputEditTextUser.text.toString()
            val passwd = binding.inputEditTextPassword.text.toString()

            userViewModel.login(user, passwd)
        }
    }
}
