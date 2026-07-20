package com.proyecto.myattendanceapp.ui.home

import android.app.DatePickerDialog
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.proyecto.myattendanceapp.R
import com.proyecto.myattendanceapp.adapter.HistorialAdapter
import com.proyecto.myattendanceapp.data.api.ApiClient
import com.proyecto.myattendanceapp.data.model.HistorialAsistenciaResponse
import com.proyecto.myattendanceapp.databinding.ActivityHistorialBinding
import com.proyecto.myattendanceapp.ui.utils.BottomNavigationHelper
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.Calendar
import java.util.Locale

class HistorialActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistorialBinding
    private lateinit var historialAdapter: HistorialAdapter

    private val listaCompleta =
        mutableListOf<HistorialAsistenciaResponse>()

    private val listaVisible =
        mutableListOf<HistorialAsistenciaResponse>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding =
            ActivityHistorialBinding.inflate(layoutInflater)

        setContentView(binding.root)

        configurarRecyclerView()
        configurarFiltro()
        cargarHistorial()

        BottomNavigationHelper.configurar(
            this,
            binding.bottomNavigation,
            R.id.nav_historial
        )
    }

    private fun configurarRecyclerView() {

        historialAdapter =
            HistorialAdapter(listaVisible)

        binding.rvHistorial.apply {
            layoutManager =
                LinearLayoutManager(this@HistorialActivity)

            adapter = historialAdapter
        }
    }

    private fun configurarFiltro() {

        binding.etBuscarFecha.setOnClickListener {
            mostrarSelectorFecha()
        }

        binding.tilBuscarFecha.setStartIconOnClickListener {
            mostrarSelectorFecha()
        }

        binding.tilBuscarFecha.setEndIconOnClickListener {
            limpiarFiltro()
        }
    }

    private fun cargarHistorial() {

        val prefs = getSharedPreferences(
            "MY_ATTENDANCE",
            MODE_PRIVATE
        )

        val idusuario =
            prefs.getInt("idusuario", 0)

        if (idusuario == 0) {

            Toast.makeText(
                this,
                "Usuario no encontrado",
                Toast.LENGTH_SHORT
            ).show()

            return
        }

        mostrarCarga(true)

        ApiClient.apiService
            .listarHistorial(idusuario)
            .enqueue(
                object :
                    Callback<List<HistorialAsistenciaResponse>> {

                    override fun onResponse(
                        call: Call<List<HistorialAsistenciaResponse>>,
                        response: Response<List<HistorialAsistenciaResponse>>
                    ) {

                        mostrarCarga(false)

                        if (response.isSuccessful) {

                            val lista =
                                response.body().orEmpty()

                            listaCompleta.clear()
                            listaCompleta.addAll(lista)

                            mostrarListaCompleta()

                        } else {

                            Toast.makeText(
                                this@HistorialActivity,
                                "No se pudo cargar el historial",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }

                    override fun onFailure(
                        call: Call<List<HistorialAsistenciaResponse>>,
                        t: Throwable
                    ) {

                        mostrarCarga(false)

                        Toast.makeText(
                            this@HistorialActivity,
                            "Error: ${
                                t.localizedMessage
                                    ?: "No se pudo conectar con el servidor"
                            }",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }
            )
    }

    private fun mostrarSelectorFecha() {

        val calendario =
            Calendar.getInstance()

        val fechaActual =
            binding.etBuscarFecha.text
                ?.toString()
                .orEmpty()

        if (fechaActual.isNotBlank()) {

            val partes =
                fechaActual.split("/")

            if (partes.size == 3) {

                val dia =
                    partes[0].toIntOrNull()

                val mes =
                    partes[1].toIntOrNull()

                val anio =
                    partes[2].toIntOrNull()

                if (
                    dia != null &&
                    mes != null &&
                    anio != null
                ) {
                    calendario.set(
                        anio,
                        mes - 1,
                        dia
                    )
                }
            }
        }

        val dialogo = DatePickerDialog(
            this,
            { _, anio, mes, dia ->

                val fechaApi = String.format(
                    Locale.US,
                    "%04d-%02d-%02d",
                    anio,
                    mes + 1,
                    dia
                )

                val fechaVisual = String.format(
                    Locale.getDefault(),
                    "%02d/%02d/%04d",
                    dia,
                    mes + 1,
                    anio
                )

                binding.etBuscarFecha.setText(fechaVisual)

                aplicarFiltro(fechaApi)
            },
            calendario.get(Calendar.YEAR),
            calendario.get(Calendar.MONTH),
            calendario.get(Calendar.DAY_OF_MONTH)
        )

        dialogo.datePicker.maxDate =
            System.currentTimeMillis()

        dialogo.show()
    }

    private fun aplicarFiltro(
        fechaSeleccionada: String
    ) {

        listaVisible.clear()

        val resultados =
            listaCompleta.filter { asistencia ->

                normalizarFecha(
                    asistencia.fecha.toString()
                ) == fechaSeleccionada
            }

        listaVisible.addAll(resultados)

        historialAdapter.notifyDataSetChanged()

        actualizarEstadoLista()
    }

    private fun limpiarFiltro() {

        binding.etBuscarFecha.text?.clear()

        mostrarListaCompleta()
    }

    private fun mostrarListaCompleta() {

        listaVisible.clear()
        listaVisible.addAll(listaCompleta)

        historialAdapter.notifyDataSetChanged()

        actualizarEstadoLista()
    }

    private fun normalizarFecha(
        fecha: String
    ): String {

        return fecha
            .substringBefore("T")
            .trim()
    }

    private fun actualizarEstadoLista() {

        val listaVacia =
            listaVisible.isEmpty()

        binding.rvHistorial.visibility =
            if (listaVacia) {
                View.GONE
            } else {
                View.VISIBLE
            }

        binding.txtSinResultados.visibility =
            if (listaVacia) {
                View.VISIBLE
            } else {
                View.GONE
            }
    }

    private fun mostrarCarga(
        mostrar: Boolean
    ) {

        binding.progressBarHistorial.visibility =
            if (mostrar) {
                View.VISIBLE
            } else {
                View.GONE
            }

        if (mostrar) {
            binding.rvHistorial.visibility =
                View.GONE

            binding.txtSinResultados.visibility =
                View.GONE
        }
    }
}