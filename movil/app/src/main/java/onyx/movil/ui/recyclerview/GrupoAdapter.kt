package onyx.movil.ui.recyclerview

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import onyx.movil.databinding.ItemGrupoBinding
import onyx.movil.models.Grupo

class GrupoAdapter(val context: Context, var items: List<Grupo>) : RecyclerView.Adapter<GrupoAdapter.GrupoViewHolder>() {

    // para manejar clicks en los items
    private lateinit var mListener: OnItemClickListener

    // define la función del click
    interface OnItemClickListener {
        fun onItemClick(position: Int)
    }

    // para asignar el listener desde fuera
    fun setOnItemClickListener(listener: OnItemClickListener)
    {
        mListener = listener
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): GrupoViewHolder {
        val binding = ItemGrupoBinding.inflate(LayoutInflater.from(context), parent, false)
        return GrupoViewHolder(context, binding, mListener)
    }

    override fun onBindViewHolder(
        holder: GrupoViewHolder,
        position: Int
    ) {
        holder.bind(position, items[position])
    }

    override fun getItemCount(): Int {
        return items.size
    }

    class GrupoViewHolder(
        val context: Context,
        val binding: ItemGrupoBinding,
        listener: OnItemClickListener
    ) : RecyclerView.ViewHolder(binding.root) {

        init {
            binding.root.setOnClickListener {
                // devuelve la posición del item al hacer click
                listener.onItemClick(absoluteAdapterPosition)
            }
        }

        fun bind(position: Int, grupo: Grupo) {
            binding.grupoNombre.text = grupo.nombre
        }
    }
}
