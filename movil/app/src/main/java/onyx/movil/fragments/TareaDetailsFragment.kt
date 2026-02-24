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
import androidx.navigation.NavOptions
import androidx.navigation.fragment.findNavController
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import onyx.movil.R
import onyx.movil.databinding.FragmentTareaDetailsBinding
import onyx.movil.providers.GrupoProvider
import onyx.movil.providers.TareaProvider
import onyx.movil.providers.UserProvider
import onyx.movil.retrofit.RetrofitInstance
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

class TareaDetailsFragment : Fragment() {
    private lateinit var binding: FragmentTareaDetailsBinding

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

    private var tareaId: Long? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            tareaId = it.getLong("tareaId")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTareaDetailsBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        var grupoId: Long = 0
        var userId: Long?

        // get tarea
        tareaViewModel.getTarea(tareaId)

        binding.toolbar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId) {
                R.id.action_eliminar -> {
                    // alert
                    alertDialog(requireContext(), getString(R.string.dialog_eliminar_tarea), getString(R.string.dialog_pregunta_eliminar_esta_tarea), getString(R.string.btn_si), {
                        // delete tarea
                        tareaViewModel.deleteTarea(tareaId)
                    }, getString(R.string.btn_cancelar))
                    true
                }
                else -> false
            }
        }

        viewLifecycleOwner.lifecycleScope.launch {
            viewLifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {

                // TAREA
                launch {
                    tareaViewModel.uiState.collect { state ->
                        when (state) {
                            TareaUiState.Idle -> Unit
                            TareaUiState.Loading -> {
                                cargando()
                            }
                            TareaUiState.SuccessDeleteTarea -> {
                                // arguementos
                                val bundle = Bundle().apply {
                                    putLong("idGrupo", grupoId)
                                }

                                // navega al detail fragment del grupo de la tarea eliminada
                                findNavController().navigate(
                                    R.id.grupoDetailsFragment,
                                    bundle,
                                    NavOptions.Builder()
                                        // borra de la navegación los fragments que van por encima para que no crashée por info que no existe
                                        .setPopUpTo(R.id.grupoDetailsFragment, false)
                                        .build()
                                )
                            }
                            is TareaUiState.SuccessPutTarea -> {
                                val tarea = state.tarea

                                // mensaje dependiendo de si está completada o no
                                if (tarea.completada) {
                                    snackbar(getString(R.string.msg_tarea_marcada_completada))
                                } else {
                                    snackbar(getString(R.string.msg_tarea_marcada_no_completada))
                                }

                                // actualiza contenido
                                tareaViewModel.getTarea(tareaId)
                            }
                            is TareaUiState.SuccessGetTarea -> {
                                val tarea = state.tarea

                                // obtiene datos

                                grupoId = tarea.grupoId
                                userId = tarea.creadorId
                                binding.textViewNombreTarea.text = tarea.titulo
                                binding.textViewVencimiento.text = tarea.fechaVencimiento
                                binding.textViewDescTarea.text = tarea.descripcion

                                val fechaCreacion = formatearFechaHora(tarea.fechaCreacion)

                                // fecha de vencimiento
                                if (tarea.fechaVencimiento.isNullOrEmpty()) {
                                    binding.textViewVencimiento.text = getString(R.string.data_sin_fecha_vencimiento)
                                } else {
                                    val fechaVencimiento = formatearFechaHora(tarea.fechaVencimiento)
                                    binding.textViewVencimiento.text = getString(R.string.data_vence_el) + fechaVencimiento
                                }

                                // creador y grupo
                                binding.textViewCreadorFechaTarea.text = getString(R.string.data_creado_el) + fechaCreacion
                                binding.textViewNombreGrupo.text = "..."

                                // get grupo y user
                                grupoViewModel.getGrupo(grupoId)
                                userViewModel.getUsuario(userId)

                                // floating button que lleva al fragment para editar
                                binding.floatingActionButtonEdit.setOnClickListener {
                                    // argumentos del fragment
                                    val bundle = Bundle().apply {
                                        putLong("id", tarea.id)
                                        putString("titulo", tarea.titulo)
                                        putString("descripcion", tarea.descripcion)
                                        putLong("grupoId", tarea.grupoId)
                                        putString("fechaVencimiento", tarea.fechaVencimiento)
                                    }

                                    findNavController().navigate(R.id.action_tareaDetailsFragment_to_tareaEditFragment, bundle)
                                }

                                binding.floatingActionButtonCheck.setOnClickListener {

                                    if (tarea.completada) {

                                        alertDialog(requireContext(), getString(R.string.dialog_tarea_no_completada), getString(R.string.dialog_pregunta_tarea_no_completada), getString(R.string.btn_si), {
                                            // marcar tarea como no completada
                                            tareaViewModel.putTareaCompletada(tarea.id, false)
                                        }, getString(R.string.btn_cancelar))

                                    } else {

                                        alertDialog(requireContext(), getString(R.string.dialog_tarea_completada), getString(R.string.dialog_pregunta_tarea_completada), getString(R.string.btn_si), {
                                            // marcar tarea como completada
                                            tareaViewModel.putTareaCompletada(tarea.id, true)
                                        }, getString(R.string.btn_cancelar))
                                    }
                                }

                                cargado()
                            }
                            is TareaUiState.Error -> {
                                cargado()
                                snackbar(state.message)
                            }
                            else -> {}
                        }
                    }
                }

                // GRUPO
                launch {
                    grupoViewModel.uiState.collect { state ->
                        when (state) {
                            GrupoUiState.Idle -> Unit
                            GrupoUiState.Loading -> {
                                //
                            }
                            is GrupoUiState.SuccessGetGrupo -> {
                                val grupo = state.grupo
                                binding.textViewNombreGrupo.text = grupo.nombre
                            }
                            is GrupoUiState.Error -> {
                                //cargado()
                                snackbar(state.message)
                            }
                            else -> {}
                        }
                    }
                }

                // USER
                launch {
                    userViewModel.uiState.collect { state ->
                        when (state) {
                            UserUiState.Idle -> Unit
                            UserUiState.Loading -> {
                                //
                            }
                            is UserUiState.SuccessGetUsuario -> {
                                val user = state.usuario
                                binding.textViewCreadorFechaTarea.text = "${binding.textViewCreadorFechaTarea.text} por ${user.nombreUsuario}"
                            }
                            is UserUiState.Error -> {
                                //cargado()
                                snackbar(state.message)
                            }
                            else -> {}
                        }
                    }
                }
            }
        }
    }

    private fun cargando() {
        binding.contenido.visibility = View.GONE
        binding.progressBar.visibility = View.VISIBLE
    }

    private fun cargado() {
        binding.contenido.visibility = View.VISIBLE
        binding.progressBar.visibility = View.GONE
    }

    private fun snackbar(message: String) {
        Snackbar.make(
            binding.root,
            message,
            Snackbar.LENGTH_LONG
        ).show()
    }
}
