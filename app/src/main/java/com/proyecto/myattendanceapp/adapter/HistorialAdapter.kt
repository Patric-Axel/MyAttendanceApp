package com.proyecto.myattendanceapp.ui.historial

import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.proyecto.myattendanceapp.data.model.HistorialAsistenciaResponse
import com.proyecto.myattendanceapp.databinding.ActivityItemHistorialBinding
import com.proyecto.myattendanceapp.ui.home.DetalleHistorialActivity

class HistorialAdapter(
    private val lista: List<HistorialAsistenciaResponse>
) : RecyclerView.Adapter<HistorialAdapter.HistorialViewHolder>() {

    inner class HistorialViewHolder(val binding: ActivityItemHistorialBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): HistorialViewHolder {
        val binding = ActivityItemHistorialBinding.inflate(
            LayoutInflater.from(parent.context),
            parent,
            false
        )
        return HistorialViewHolder(binding)
    }

    override fun onBindViewHolder(holder: HistorialViewHolder, position: Int) {
        val item = lista[position]

        holder.binding.tvFechaItem.text = item.fecha ?: "--"
        holder.binding.tvEstadoItem.text = item.estado ?: "--"
        holder.binding.tvHoraEntradaItem.text = item.horaentrada ?: "--:--"
        holder.binding.tvHoraSalidaItem.text = item.horasalida ?: "--:--"
        holder.binding.tvHorasItem.text = "${item.horastrabajadas ?: 0.0} h"

        holder.binding.cardItem.setOnClickListener {
            val context = holder.itemView.context

            val intent = Intent(context, DetalleHistorialActivity::class.java)
            intent.putExtra("idasistencia", item.idasistencia)

            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int = lista.size
}