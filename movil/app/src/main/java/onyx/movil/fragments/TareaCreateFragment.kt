package onyx.movil.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import androidx.core.widget.addTextChangedListener
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import androidx.lifecycle.repeatOnLifecycle
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import onyx.movil.R
import onyx.movil.databinding.FragmentTareaCreateBinding
import onyx.movil.local.SessionManager
import onyx.movil.models.Grupo
import onyx.movil.providers.GrupoProvider
import onyx.movil.providers.TareaProvider
import onyx.movil.retrofit.RetrofitInstance
import onyx.movil.ui.states.GrupoUiState
import onyx.movil.ui.states.TareaUiState
import onyx.movil.ui.viewmodels.GrupoViewModel
import onyx.movil.ui.viewmodels.TareaViewModel
import onyx.movil.ui.viewmodels.factories.GrupoViewModelFactory
import onyx.movil.ui.viewmodels.factories.TareaViewModelFactory

class TareaCreateFragment : Fragment() {
    private lateinit var binding: FragmentTareaCreateBinding
    private lateinit var grupos: List<Grupo>

    private val grupoViewModel: GrupoViewModel by lazy {
        val provider = GrupoProvider(RetrofitInstance.api)
        val factory = GrupoViewModelFactory(provider)
        ViewModelProvider(this, factory)[GrupoViewModel::class.java]
    }

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
        binding = FragmentTareaCreateBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // arguemntos
        val idGrupo = arguments?.getLong("idGrupo")

        // session manager
        val sessionManager = SessionManager(requireContext())

        // corrutina para obtener datos
        viewLifecycleOwner.lifecycleScope.launch {

            // obtiene el usuario
            val userId = sessionManager.getUserId()

            // carga los grupos
            grupoViewModel.getGrupos(userId)

            binding.btnCrearTarea.setOnClickListener {

                // obtiene info de los campos
                val titulo = binding.tituloEditText.text.toString()
                val desc = binding.descEditText.text.toString()
                val grupoSeleccionado = binding.spinnerGrupo.selectedItem as Grupo

                // crea la tarea
                tareaViewModel.postTarea(titulo, desc, userId, grupoSeleccionado.id)
            }
        }

        // corrutina para el estado
        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    tareaViewModel.uiState.collect { state ->
                        when (state) {
                            TareaUiState.Idle -> Unit

                            TareaUiState.Loading -> {
                                // deshabilita el btn
                                binding.btnCrearTarea.isEnabled = false
                            }

                            is TareaUiState.SuccessPostTarea -> {
                                // tarea creada
                                val tarea = state.tarea

                                // argumentos del fragment
                                val bundle = Bundle().apply {
                                    putLong("tareaId", tarea.id)
                                }

                                findNavController().navigate(R.id.action_tareaCreateFragment_to_tareaDetailsFragment, bundle)

                                // habilita el btn
                                binding.btnCrearTarea.isEnabled = true
                            }

                            is TareaUiState.Error -> {
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
                    grupoViewModel.uiState.collect { state ->
                        when (state) {
                            GrupoUiState.Idle -> Unit

                            GrupoUiState.Loading -> {
                                // desaparece el contenido
                                binding.progressBar.visibility = View.VISIBLE
                                binding.contenido.visibility = View.GONE
                            }

                            is GrupoUiState.SuccessGetGrupos -> {
                                // obtiene los grupos
                                grupos = state.grupos

                                // lista para el spinner
                                val grupos: MutableList<Grupo> = grupos as MutableList<Grupo>
                                // grupo para la opción sin grupos
                                grupos.add(0, Grupo(-1, getString(R.string.msg_empty_grupos), "", "", 0, emptyList()))

                                // adapter del spinner
                                val adapter = ArrayAdapter(
                                    requireContext(),
                                    android.R.layout.simple_spinner_dropdown_item,
                                    grupos
                                )

                                binding.spinnerGrupo.adapter = adapter

                                if (idGrupo?.toInt() != 0) {
                                    // si hay idGrupo cargado, selecciona en el spinner y lo deshabilita
                                    val posicion = grupos.indexOfFirst { it.id == idGrupo }
                                    binding.spinnerGrupo.setSelection(posicion)
                                    binding.spinnerGrupo.isEnabled = false

                                } else {
                                    // selecciona el primer grupo de la lista, el sin grupo
                                    binding.spinnerGrupo.setSelection(0)
                                }

                                // aparece el contenido
                                binding.progressBar.visibility = View.GONE
                                binding.contenido.visibility = View.VISIBLE
                            }

                            is GrupoUiState.Error -> {
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

        /* manejar el estado el btn */

        fun noHayCamposVacios(): Boolean {
            // obtiene el grupo seleccionado
            val grupoSeleccionado = binding.spinnerGrupo.selectedItem as Grupo

            // devuelve true si el titulo no está vacio y el id del grupo no coincide con el del grupo vacio
            return !binding.tituloEditText.text?.isEmpty()!! && grupoSeleccionado.id.toInt() != -1
        }

        // valor inicial
        binding.btnCrearTarea.isEnabled = false

        // cambian el estado del botón por cada cambio

        binding.tituloEditText.addTextChangedListener {
            binding.btnCrearTarea.isEnabled = noHayCamposVacios()
        }

        binding.spinnerGrupo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                binding.btnCrearTarea.isEnabled = noHayCamposVacios()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }
}
