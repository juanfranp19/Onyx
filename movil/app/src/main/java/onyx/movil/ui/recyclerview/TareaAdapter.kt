package onyx.movil.ui.recyclerview

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import onyx.movil.databinding.ItemTareaBinding
import onyx.movil.models.Tarea
import onyx.movil.utils.formatearFechaHora

class TareaAdapter(
    val context: Context,
    var items: List<Tarea>
) : RecyclerView.Adapter<TareaAdapter.TareaViewHolder>() {

    // para manejar clicks en los items
    private lateinit var mListener: OnItemClickListener

    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener) {
        mListener = listener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TareaViewHolder {
        val binding = ItemTareaBinding.inflate(LayoutInflater.from(context), parent, false)
        return TareaViewHolder(context, binding, mListener)
    }

    override fun onBindViewHolder(
        holder: TareaViewHolder,
        position: Int
    ) {
        holder.bind(items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class TareaViewHolder(
        val context: Context,
        val binding: ItemTareaBinding,
        listener: OnItemClickListener
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                // devuelve la posición del item al hacer click
                listener.onItemClick(absoluteAdapterPosition)
            }
        }

        fun bind(tarea: Tarea) {

            binding.tareaNombre.text = tarea.titulo
            binding.tareaDescripcion.text = tarea.descripcion

            // fecha y hora de vencimiento
            if (!tarea.fechaVencimiento.isNullOrEmpty()) {
                binding.tareaFechaVenc.text = formatearFechaHora(tarea.fechaVencimiento)
            }

            // icono de completada
            if (tarea.completada) {
                binding.imageViewTareaDone.visibility = View.VISIBLE
            } else {
                binding.imageViewTareaDone.visibility = View.GONE
            }
        }
    }
}
