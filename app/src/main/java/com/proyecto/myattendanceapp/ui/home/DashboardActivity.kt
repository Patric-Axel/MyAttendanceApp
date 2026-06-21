package com.proyecto.myattendanceapp.ui.home

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.proyecto.myattendanceapp.R
import com.proyecto.myattendanceapp.data.api.ApiClient
import com.proyecto.myattendanceapp.data.model.AsistenciaHoyResponse
import com.proyecto.myattendanceapp.databinding.ActivityDashboardBinding
import com.proyecto.myattendanceapp.ui.asistencia.MarcarEntradaActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding
    private var tieneEntrada = false
    private var tieneSalida = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        cargarUsuario()
        cargarFecha()
        mostrarAsistenciaVacia()
        configurarBoton()
    }

    override fun onResume() {
        super.onResume()
        cargarAsistenciaHoy()
    }
    private fun cargarFecha() {
        val formato = SimpleDateFormat("EEEE, dd 'de' MMMM yyyy", Locale("es", "PE"))
        binding.txtFecha.text = formato.format(Date())
    }

    private fun configurarBoton() {
        binding.btnMarcarAsistencia.setOnClickListener {
            if (!tieneEntrada) {
                startActivity(
                    Intent(this, MarcarEntradaActivity::class.java)
                )
            } else if (tieneEntrada && !tieneSalida) {
                Toast.makeText(this, "Registrar salida pendiente", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun cargarUsuario() {
        val prefs = getSharedPreferences("MY_ATTENDANCE", MODE_PRIVATE)
        val nombres = prefs.getString("nombres", "Usuario")

        binding.txtSaludo.text = "Hola, $nombres"
    }

    private fun mostrarAsistenciaVacia() {
        binding.txtEstadoDia.text = getString(R.string.sin_registro)
        binding.txtMensajeSalida.text = getString(R.string.sin_entrada)
        binding.txtHoraEntrada.text = "--:--"
        binding.txtHoraSalida.text = "--:--"
        binding.txtHorasTrabajadas.text = getString(R.string.cero_horas)
        binding.btnMarcarAsistencia.text = getString(R.string.registrar_entrada)
    }

    private fun cargarAsistenciaHoy() {
        val prefs = getSharedPreferences("MY_ATTENDANCE", MODE_PRIVATE)
        val idusuario = prefs.getInt("idusuario", 0)

        Toast.makeText(this, "Consultando ID: $idusuario", Toast.LENGTH_SHORT).show()

        ApiClient.apiService.obtenerAsistenciaHoy(idusuario)
            .enqueue(object : Callback<AsistenciaHoyResponse> {
                override fun onResponse(
                    call: Call<AsistenciaHoyResponse>,
                    response: Response<AsistenciaHoyResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        val asistencia = response.body()!!

                        binding.txtEstadoDia.text = asistencia.estado ?: "Registrado"
                        binding.txtHoraEntrada.text = asistencia.horaentrada ?: "--:--"
                        binding.txtHoraSalida.text = asistencia.horasalida ?: "--:--"
                        binding.txtHorasTrabajadas.text =
                            asistencia.horastrabajadas?.toString() ?: "0 h"

                        tieneEntrada = asistencia.horaentrada != null
                        tieneSalida = asistencia.horasalida != null

                        if (tieneEntrada && !tieneSalida) {
                            binding.txtMensajeSalida.text = "Entrada registrada correctamente"
                            binding.btnMarcarAsistencia.text = "Registrar salida"
                        }

                    } else {
                        Toast.makeText(
                            this@DashboardActivity,
                            "HTTP ${response.code()}",
                            Toast.LENGTH_LONG
                        ).show()

                        mostrarAsistenciaVacia()
                    }
                }

                override fun onFailure(call: Call<AsistenciaHoyResponse>, t: Throwable) {
                    Toast.makeText(
                        this@DashboardActivity,
                        "Error: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()

                    mostrarAsistenciaVacia()
                }
            })
    }

}