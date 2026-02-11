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
import onyx.movil.providers.GrupoProvider
import onyx.movil.retrofit.RetrofitInstance
import onyx.movil.ui.recyclerview.GrupoAdapter
import onyx.movil.ui.states.GrupoUiState
import onyx.movil.ui.viewmodels.GrupoViewModel
import onyx.movil.ui.viewmodels.factories.GrupoViewModelFactory

class GruposFragment : Fragment() {
    private lateinit var binding: FragmentGruposBinding

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
            grupoViewModel.getGrupos(userId)

            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                grupoViewModel.uiState.collect { state ->
                    when (state) {
                        GrupoUiState.Idle -> Unit

                        GrupoUiState.Loading -> {
                            // aparece la progressbar y desaparece el rv
                            binding.progressBar.visibility = View.VISIBLE
                            binding.rv.visibility = View.GONE

                            // layout manager
                            binding.rv.layoutManager = LinearLayoutManager(requireContext())
                        }

                        GrupoUiState.Empty -> {
                            // aparece mensaje empty
                            binding.mensajeEmpty.visibility = View.VISIBLE
                            // desaparece progressbar
                            binding.progressBar.visibility = View.GONE
                        }

                        is GrupoUiState.SuccessGetGrupos -> {

                            // obtiene los grupos
                            val grupos = state.grupos

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
                                        }

                                        findNavController().navigate(R.id.action_gruposFragment_to_grupoDetailsFragment, bundle)
                                    }
                                })

                                // aparece el rv
                                binding.rv.visibility = View.VISIBLE

                            }

                            // desaparece progressbar
                            binding.progressBar.visibility = View.GONE
                        }

                        is GrupoUiState.Error -> {
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

        binding.floatingActionButton.setOnClickListener {
            findNavController().navigate(R.id.action_gruposFragment_to_grupoCreateFragment)
        }
    }
}
