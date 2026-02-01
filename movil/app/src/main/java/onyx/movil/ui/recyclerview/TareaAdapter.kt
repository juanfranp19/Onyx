package onyx.movil.ui.recyclerview

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import onyx.movil.databinding.ItemTareaBinding
import onyx.movil.models.Tarea

class TareaAdapter(
    val context: Context,
    var items: List<Tarea>
) : RecyclerView.Adapter<TareaAdapter.TareaViewHolder>() {

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): TareaViewHolder {
        val binding = ItemTareaBinding.inflate(LayoutInflater.from(context), parent, false)
        return TareaViewHolder(context, binding)
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
        val binding: ItemTareaBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bind(tarea: Tarea) {
            binding.tareaNombre.text = tarea.titulo
        }
    }
}
