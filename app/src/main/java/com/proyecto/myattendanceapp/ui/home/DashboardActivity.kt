package com.proyecto.myattendanceapp.ui.home

import com.proyecto.myattendanceapp.ui.asistencia.MarcarSalidaActivity
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.proyecto.myattendanceapp.R
import com.proyecto.myattendanceapp.data.api.ApiClient
import com.proyecto.myattendanceapp.data.model.AsistenciaHoyResponse
import com.proyecto.myattendanceapp.databinding.ActivityDashboardBinding
import com.proyecto.myattendanceapp.ui.asistencia.MarcarEntradaActivity
import com.proyecto.myattendanceapp.ui.perfil.PerfilActivity
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

        binding.bottomNavigation.setOnItemSelectedListener { item ->

            when (item.itemId) {

                R.id.nav_inicio -> {
                    true
                }

                R.id.nav_historial -> {
                    startActivity(Intent(this, HistorialActivity::class.java))
                    true
                }

                R.id.nav_perfil -> {
                    startActivity(Intent(this, PerfilActivity::class.java))
                    true
                }

                else -> false
            }
        }

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
                startActivity(
                    Intent(this, MarcarSalidaActivity::class.java)
                )
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

                        if (asistencia.estado == "Falta") {
                            binding.txtEstadoDia.text = "Falta"
                            binding.txtMensajeSalida.text = "Horario excedido. Se registró falta."
                            binding.txtHoraEntrada.text = "--:--"
                            binding.txtHoraSalida.text = "--:--"
                            binding.txtHorasTrabajadas.text = "0 h"
                            binding.btnMarcarAsistencia.text = "Falta registrada"
                            binding.btnMarcarAsistencia.isEnabled = false
                            tieneEntrada = false
                            tieneSalida = false
                            return
                        }

                        binding.txtEstadoDia.text = asistencia.estado ?: "Registrado"
                        binding.txtHoraEntrada.text = asistencia.horaentrada ?: "--:--"
                        binding.txtHoraSalida.text = asistencia.horasalida ?: "--:--"
                        binding.txtHorasTrabajadas.text =
                            asistencia.horastrabajadas?.toString() ?: "0 h"

                        tieneEntrada = asistencia.horaentrada != null
                        tieneSalida = asistencia.horasalida != null

                        when {
                            !tieneEntrada -> {
                                binding.txtMensajeSalida.text = "Aún no has registrado tu entrada"
                                binding.btnMarcarAsistencia.text = "Registrar entrada"
                                binding.btnMarcarAsistencia.isEnabled = true
                            }

                            tieneEntrada && !tieneSalida -> {
                                binding.txtMensajeSalida.text = "Entrada registrada correctamente"
                                binding.btnMarcarAsistencia.text = "Registrar salida"
                                binding.btnMarcarAsistencia.isEnabled = true
                            }

                            tieneEntrada && tieneSalida -> {
                                binding.txtMensajeSalida.text = "Asistencia completada correctamente"
                                binding.btnMarcarAsistencia.text = "✓ Completado"
                                binding.btnMarcarAsistencia.isEnabled = false
                            }
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