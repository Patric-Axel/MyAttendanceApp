package com.proyecto.myattendanceapp.ui.confirmacion

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.proyecto.myattendanceapp.data.api.ApiClient
import com.proyecto.myattendanceapp.data.model.AsistenciaResponse
import com.proyecto.myattendanceapp.data.model.EntradaRequest
import com.proyecto.myattendanceapp.databinding.ActivityConfirmacionEntradaBinding
import com.proyecto.myattendanceapp.ui.home.DashboardActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ConfirmacionEntradaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityConfirmacionEntradaBinding

    private var latitud = 0.0
    private var longitud = 0.0
    private var fotoUri: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityConfirmacionEntradaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        latitud = intent.getDoubleExtra("latitud", 0.0)
        longitud = intent.getDoubleExtra("longitud", 0.0)
        fotoUri = intent.getStringExtra("fotoUri")

        binding.txtUbicacionConfirmacion.text = "Lat: $latitud\nLng: $longitud"

        binding.txtFechaConfirmacion.text = SimpleDateFormat(
            "dd/MM/yyyy HH:mm",
            Locale("es", "PE")
        ).format(Date())

        fotoUri?.let {
            binding.imgFotoConfirmacion.setImageURI(Uri.parse(it))
        }

        binding.btnInicio.setOnClickListener {
            registrarEntrada()
        }
    }

    private fun registrarEntrada() {
        val prefs = getSharedPreferences("MY_ATTENDANCE", MODE_PRIVATE)
        val idusuario = prefs.getInt("idusuario", 0)

        val request = EntradaRequest(
            idusuario = idusuario,
            latitudentrada = latitud,
            longitudentrada = longitud,
            direntrada = "Ubicación capturada",
            fotoentrada = fotoUri
        )

        ApiClient.apiService.registrarEntrada(request)
            .enqueue(object : Callback<AsistenciaResponse> {
                override fun onResponse(
                    call: Call<AsistenciaResponse>,
                    response: Response<AsistenciaResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        Toast.makeText(
                            this@ConfirmacionEntradaActivity,
                            response.body()!!.message,
                            Toast.LENGTH_SHORT
                        ).show()

                        val intent = Intent(
                            this@ConfirmacionEntradaActivity,
                            DashboardActivity::class.java
                        )
                        intent.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK
                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(
                            this@ConfirmacionEntradaActivity,
                            "No se pudo registrar entrada",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<AsistenciaResponse>,
                    t: Throwable
                ) {
                    Toast.makeText(
                        this@ConfirmacionEntradaActivity,
                        "Error de conexión",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
    }
}