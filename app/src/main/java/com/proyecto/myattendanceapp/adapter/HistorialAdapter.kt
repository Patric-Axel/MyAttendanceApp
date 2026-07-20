package com.proyecto.myattendanceapp.adapter
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.proyecto.myattendanceapp.data.model.HistorialAsistenciaResponse
import com.proyecto.myattendanceapp.databinding.ActivityItemHistorialBinding
import com.proyecto.myattendanceapp.ui.asistencia.DetalleAsistenciaActivity

class HistorialAdapter(
    private val lista: List<HistorialAsistenciaResponse>
) : RecyclerView.Adapter<HistorialAdapter.HistorialViewHolder>() {

    inner class HistorialViewHolder(
        val binding: ActivityItemHistorialBinding
    ) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): HistorialViewHolder {

        val binding = ActivityItemHistorialBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )

        return HistorialViewHolder(binding)
    }

    override fun onBindViewHolder(
        holder: HistorialViewHolder,
        position: Int
    ) {

        val item = lista[position]

        holder.binding.tvFechaItem.text =
            item.fecha ?: "--"

        holder.binding.tvEstadoItem.text =
            item.estado ?: "--"

        holder.binding.tvHoraEntradaItem.text =
            formatearHora(item.horaentrada)

        holder.binding.tvHoraSalidaItem.text =
            formatearHora(item.horasalida)

        holder.binding.tvHorasItem.text =
            item.horastrabajadas
                ?.let { "$it h" }
                ?: "--"

        holder.binding.cardItem.setOnClickListener {

            val context = holder.itemView.context

            val idasistencia = item.idasistencia

            if (idasistencia == null || idasistencia <= 0) {
                return@setOnClickListener
            }

            val intent = Intent(
                context,
                DetalleAsistenciaActivity::class.java
            ).apply {
                putExtra("idasistencia", idasistencia)
            }

            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = lista.size

    private fun formatearHora(hora: String?): String {

        if (hora.isNullOrBlank()) {
            return "--:--"
        }

        return if (hora.length >= 5) {
            hora.substring(0, 5)
        } else {
            hora
        }
    }
}