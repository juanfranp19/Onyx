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
import kotlinx.coroutines.launch
import onyx.movil.R
import onyx.movil.databinding.FragmentGrupoDetailsBinding
import onyx.movil.providers.GrupoProvider
import onyx.movil.providers.TareaProvider
import onyx.movil.providers.UserProvider
import onyx.movil.retrofit.RetrofitInstance
import onyx.movil.ui.recyclerview.TareaAdapter
import onyx.movil.ui.states.GrupoUiState
import onyx.movil.ui.states.TareaUiState
import onyx.movil.ui.states.UserUiState
import onyx.movil.ui.viewmodels.GrupoViewModel
import onyx.movil.ui.viewmodels.TareaViewModel
import onyx.movil.ui.viewmodels.UserViewModel
import onyx.movil.ui.viewmodels.factories.GrupoViewModelFactory
import onyx.movil.ui.viewmodels.factories.TareaViewModelFactory
import onyx.movil.ui.viewmodels.factories.UserViewModelFactory
import onyx.movil.utils.alertDialog
import onyx.movil.utils.formatearFechaHora
import onyx.movil.utils.longSnack

class GrupoDetailsFragment : Fragment() {
    private lateinit var binding: FragmentGrupoDetailsBinding

    private val tareaViewModel: TareaViewModel by lazy {
        val provider = TareaProvider(RetrofitInstance.api)
        val factory = TareaViewModelFactory(provider)
        ViewModelProvider(this, factory)[TareaViewModel::class.java]
    }

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

    private var idGrupo: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            idGrupo = it.getLong("idGrupo")
        }
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

        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_eliminar -> {
                    alertDialog(requireContext(), getString(R.string.dialog_eliminar_grupo), getString(R.string.dialog_pregunta_eliminar_este_grupo), getString(R.string.btn_si), {
                        grupoViewModel.deleteGrupo(idGrupo)
                    }, getString(R.string.btn_cancelar))
                    true
                }
                else -> false
            }
        }

        var creadorId: Long?

        // get grupo
        grupoViewModel.getGrupo(idGrupo)

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
                launch {
                    grupoViewModel.uiState.collect { state ->
                        when (state) {
                            GrupoUiState.Idle -> Unit
                            GrupoUiState.Loading -> {
                                cargando()
                            }
                            GrupoUiState.SuccessDeleteGrupo -> {
                                findNavController().navigate(R.id.action_grupoDetailsFragment_to_gruposFragment)
                            }
                            is GrupoUiState.SuccessGetGrupo -> {
                                val grupo = state.grupo

                                // obtiene datos

                                binding.textViewNombreGrupo.text = grupo.nombre
                                binding.textViewDescGrupo.text = grupo.descripcion

                                val fechaCreacion = formatearFechaHora(grupo.fechaCreacion)
                                binding.textViewCreadorFechaGrupo.text = getString(R.string.data_creado_el) + " " + fechaCreacion

                                // get tareas por grupo
                                tareaViewModel.getTareasByGrupo(idGrupo)

                                // get creador
                                creadorId = grupo.creadorId
                                userViewModel.getUsuario(creadorId)
                            }
                            is GrupoUiState.Error -> {
                                mostrarError(getString(state.message.toInt()))
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
                                //
                            }
                            is UserUiState.SuccessGetUsuario -> {
                                val creador = state.usuario
                                // actualiza el text view
                                binding.textViewCreadorFechaGrupo.text = "${binding.textViewCreadorFechaGrupo.text}" + " " + getString(R.string.data_by) + " " + creador.nombreUsuario
                            }
                            is UserUiState.Error -> {
                                mostrarError(getString(state.message.toInt()))
                            }
                            else -> {}
                        }
                    }
                }
                launch {
                    tareaViewModel.uiState.collect { state ->
                        when (state) {
                            TareaUiState.Idle -> Unit
                            TareaUiState.Loading -> {
                                //
                            }
                            TareaUiState.Empty -> {
                                // aparece el texto empty
                                cargado()
                                binding.rv.visibility = View.GONE
                                binding.tareasEmpty.visibility = View.VISIBLE
                            }
                            is TareaUiState.SuccessGetTareasByGrupo -> {

                                val tareas = state.tareas

                                val adapter = TareaAdapter(requireContext(), tareas)
                                // layout manager
                                binding.rv.layoutManager = LinearLayoutManager(requireContext())
                                binding.rv.adapter = adapter

                                // cada tarea
                                adapter.setOnItemClickListener(object: TareaAdapter.OnItemClickListener {
                                    override fun onItemClick(position: Int) {

                                        val tarea = tareas[position]

                                        // argumentos del fragment
                                        val bundle = Bundle().apply {
                                            putLong("tareaId", tarea.id)
                                        }

                                        findNavController().navigate(R.id.action_grupoDetailsFragment_to_tareaDetailsFragment, bundle)
                                    }
                                })

                                cargado()
                            }
                            is TareaUiState.Error -> {
                                cargando()

                                mostrarError(getString(state.message.toInt()))
                            }
                            else -> {}
                        }
                    }
                }
            }
        }

        binding.floatingActionButton.setOnClickListener {

            if (idGrupo != null) {
                val bundle = Bundle().apply {
                    putLong("idGrupo", idGrupo!!)
                }

                findNavController().navigate(R.id.action_grupoDetailsFragment_to_tareaCreateFragment, bundle)
            }
        }
    }

    private fun cargando() {
        // aparece la progressbar y desaparece el rv
        binding.progressBar.visibility = View.VISIBLE
        binding.contenedor.visibility = View.GONE
    }

    private fun cargado() {
        // aparece la progressbar y desaparece el rv
        binding.progressBar.visibility = View.GONE
        binding.contenedor.visibility = View.VISIBLE
    }

    private fun mostrarError(message: String) {
        longSnack(binding.root, message)
    }
}
