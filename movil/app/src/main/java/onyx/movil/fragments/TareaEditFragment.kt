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
import com.google.android.material.datepicker.CalendarConstraints
import com.google.android.material.datepicker.DateValidatorPointForward
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import kotlinx.coroutines.launch
import onyx.movil.R
import onyx.movil.databinding.FragmentTareaEditBinding
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
import onyx.movil.utils.formatearFechaHora
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

class TareaEditFragment : Fragment() {
    private lateinit var binding: FragmentTareaEditBinding
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

    private var id: Long? = null
    private var titulo: String? = null
    private var descripcion: String? = null
    private var grupoId: Long? = null
    private var fechaVencimiento: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            id = it.getLong("id")
            titulo = it.getString("titulo")
            descripcion = it.getString("descripcion")
            grupoId = it.getLong("grupoId")
            fechaVencimiento = it.getString("fechaVencimiento")
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentTareaEditBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // autocompletando los campos con los datos de la tarea
        binding.tituloEditText.setText(titulo)
        binding.descEditText.setText(descripcion)
        if (!fechaVencimiento.isNullOrEmpty()) {
            binding.fechaVencEditText.setText(formatearFechaHora(fechaVencimiento.toString()))
        }

        // session manager
        val sessionManager = SessionManager(requireContext())

        // corrutina para obtener datos
        viewLifecycleOwner.lifecycleScope.launch {

            // obtiene el usuario
            val userId = sessionManager.getUserId()

            // carga los grupos
            grupoViewModel.getGrupos(userId)

            // boton guardar
            binding.btnGuardarTarea.setOnClickListener {

                // obtiene info de los campos
                val titulo = binding.tituloEditText.text.toString()
                val desc = binding.descEditText.text.toString()
                val grupoSeleccionado = binding.spinnerGrupo.selectedItem as Grupo
                val fechaVenc = binding.fechaVencEditText.text.toString()

                // guardar cambios de la tarea
                tareaViewModel.putTarea(id, titulo, desc, fechaVenc, grupoSeleccionado.id)
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
                                binding.btnGuardarTarea.isEnabled = false
                            }

                            is TareaUiState.SuccessPutTarea -> {
                                // tarea creada
                                val tarea = state.tarea

                                // argumentos del fragment
                                val bundle = Bundle().apply {
                                    putLong("tareaId", tarea.id)
                                }

                                findNavController().navigate(R.id.action_tareaEditFragment_to_tareaDetailsFragment, bundle)

                                // habilita el btn
                                binding.btnGuardarTarea.isEnabled = true
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

                                // selecciona del spinner el grupo de la tarea
                                val posicion = grupos.indexOfFirst { it.id == grupoId }
                                binding.spinnerGrupo.setSelection(posicion)

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

        /* DateTimePicker */
        binding.fechaVencEditText.setOnClickListener {

            // no permitir fecha anterior a hoy
            val constraints = CalendarConstraints.Builder()
                .setValidator(DateValidatorPointForward.now())
                .build()

            // fecha
            val datePicker = MaterialDatePicker
                .Builder
                .datePicker()
                .setCalendarConstraints(constraints)
                .setTitleText("Seleccionar fecha")
                .build()

            datePicker.addOnPositiveButtonClickListener { timestamp ->

                // hora
                val timePicker = MaterialTimePicker.Builder()
                    .setTimeFormat(TimeFormat.CLOCK_24H)
                    .setTitleText("Seleccionar hora")
                    .build()

                timePicker.addOnPositiveButtonClickListener {
                    val hour = timePicker.hour
                    val minute = timePicker.minute

                    // combinar fecha y hora
                    val calendar = Calendar.getInstance().apply {
                        timeInMillis = timestamp
                        set(Calendar.HOUR_OF_DAY, hour)
                        set(Calendar.MINUTE, minute)
                    }

                    val formato = SimpleDateFormat("dd/MM/yyyy HH:mm", Locale.getDefault())
                    binding.fechaVencEditText.setText(formato.format(calendar.time))
                }

                timePicker.show(parentFragmentManager, "time_picker")
            }

            datePicker.show(parentFragmentManager, "date_picker")
        }

        /* manejar el estado el btn */

        fun noHayCamposVacios(): Boolean {
            // obtiene el grupo seleccionado
            val grupoSeleccionado = binding.spinnerGrupo.selectedItem as Grupo

            // devuelve true si el titulo no está vacio y el id del grupo no coincide con el del grupo vacio
            return !binding.tituloEditText.text?.isEmpty()!! && grupoSeleccionado.id.toInt() != -1
        }

        // valor inicial
        binding.btnGuardarTarea.isEnabled = true

        // cambian el estado del botón por cada cambio

        binding.tituloEditText.addTextChangedListener {
            binding.btnGuardarTarea.isEnabled = noHayCamposVacios()
        }

        binding.spinnerGrupo.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, position: Int, id: Long) {
                binding.btnGuardarTarea.isEnabled = noHayCamposVacios()
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }
}
