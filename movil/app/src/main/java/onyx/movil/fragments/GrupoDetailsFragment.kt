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
import onyx.movil.databinding.FragmentGrupoDetailsBinding
import onyx.movil.providers.TareaProvider
import onyx.movil.retrofit.RetrofitInstance
import onyx.movil.ui.recyclerview.TareaAdapter
import onyx.movil.ui.states.TareaUiState
import onyx.movil.ui.viewmodels.TareaViewModel
import onyx.movil.ui.viewmodels.factories.TareaViewModelFactory
import onyx.movil.utils.formatearFechaHora

class GrupoDetailsFragment : Fragment() {
    private lateinit var binding: FragmentGrupoDetailsBinding

    private val tareaViewModel: TareaViewModel by lazy {
        val provider = TareaProvider(RetrofitInstance.api)
        val factory = TareaViewModelFactory(provider)
        ViewModelProvider(this, factory)[TareaViewModel::class.java]
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentGrupoDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // datos del budle
        val idGrupo = arguments?.getLong("idGrupo")
        val nombreGrupo = arguments?.getString("nombreGrupo") ?: "Vacío"
        val descGrupo = arguments?.getString("descGrupo") ?: "Vacío"
        val fechaCreacionGrupo = arguments?.getString("fechaCreacionGrupo") ?: "Vacío"
        val creadorGrupo = arguments?.getString("creadorGrupo") ?: "Vacío"
        val creadorFecha = "Creado por $creadorGrupo el " + formatearFechaHora(fechaCreacionGrupo)

        // textviews
        binding.textViewNombreGrupo.text = nombreGrupo
        binding.textViewDescGrupo.text = descGrupo
        binding.textViewCreadorFechaGrupo.text = creadorFecha

        // get tareas por grupo
        tareaViewModel.getTareasByGrupo(idGrupo)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                tareaViewModel.uiState.collect { state ->
                    when (state) {

                        TareaUiState.Idle -> Unit

                        TareaUiState.Loading -> {
                            // aparece la progressbar y desaparece el rv
                            binding.progressBar.visibility = View.VISIBLE
                            binding.rv.visibility = View.GONE
                        }

                        TareaUiState.Empty -> {
                            // aparece el texto empty
                            binding.progressBar.visibility = View.GONE
                            binding.tareasEmpty.visibility = View.VISIBLE
                        }

                        is TareaUiState.SuccessGetTareasByGrupo -> {

                            val tareas = state.tareas

                            val adapter = TareaAdapter(requireContext(), tareas)
                            // layout manager
                            binding.rv.layoutManager = LinearLayoutManager(requireContext())
                            binding.rv.adapter = adapter

                            // aparece el rv
                            binding.progressBar.visibility = View.GONE
                            binding.rv.visibility = View.VISIBLE
                        }

                        is TareaUiState.Error -> {
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

            if (idGrupo != null) {
                val bundle = Bundle().apply {
                    putLong("idGrupo", idGrupo)
                }

                findNavController().navigate(R.id.action_grupoDetailsFragment_to_tareaCreateFragment, bundle)
            }
        }
    }
}
