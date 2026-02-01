package onyx.movil.fragments

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import onyx.movil.databinding.FragmentGrupoDetailsBinding
import onyx.movil.utils.formatearFechaHora

class GrupoDetailsFragment : Fragment() {
    private lateinit var binding: FragmentGrupoDetailsBinding

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

        binding.textViewNombreGrupo.text = nombreGrupo
        binding.textViewDescGrupo.text = descGrupo
        binding.textViewCreadorFechaGrupo.text = creadorFecha
    }
}
