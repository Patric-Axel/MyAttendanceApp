package com.proyecto.myattendanceapp.ui.home

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.proyecto.myattendanceapp.data.api.ApiClient
import com.proyecto.myattendanceapp.data.model.HistorialAsistenciaResponse
import com.proyecto.myattendanceapp.databinding.ActivityHistorialBinding
import com.proyecto.myattendanceapp.ui.historial.HistorialAdapter
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class HistorialActivity : AppCompatActivity() {

    private lateinit var binding: ActivityHistorialBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHistorialBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarRecyclerView()
        cargarHistorial()
    }

    private fun configurarRecyclerView() {
        binding.rvHistorial.layoutManager = LinearLayoutManager(this)
    }

    private fun cargarHistorial() {

        val prefs = getSharedPreferences("MY_ATTENDANCE", MODE_PRIVATE)
        val idusuario = prefs.getInt("idusuario", 0)

        if (idusuario == 0) {
            Toast.makeText(this, "Usuario no encontrado", Toast.LENGTH_SHORT).show()
            return
        }

        ApiClient.apiService.listarHistorial(idusuario)
            .enqueue(object : Callback<List<HistorialAsistenciaResponse>> {

                override fun onResponse(
                    call: Call<List<HistorialAsistenciaResponse>>,
                    response: Response<List<HistorialAsistenciaResponse>>
                ) {

                    if (response.isSuccessful) {

                        val lista = response.body() ?: emptyList()

                        binding.rvHistorial.adapter = HistorialAdapter(lista)

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

                    Toast.makeText(
                        this@HistorialActivity,
                        "Error: ${t.localizedMessage}",
                        Toast.LENGTH_SHORT
                    ).show()

                }
            })
    }
}