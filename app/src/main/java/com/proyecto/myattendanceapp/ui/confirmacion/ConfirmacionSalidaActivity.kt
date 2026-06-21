package com.proyecto.myattendanceapp.ui.confirmacion

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.proyecto.myattendanceapp.data.api.ApiClient
import com.proyecto.myattendanceapp.data.model.AsistenciaResponse
import com.proyecto.myattendanceapp.data.model.SalidaRequest
import com.proyecto.myattendanceapp.databinding.ActivityConfirmacionSalidaBinding
import com.proyecto.myattendanceapp.ui.home.DashboardActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class ConfirmacionSalidaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityConfirmacionSalidaBinding

    private var latitud = 0.0
    private var longitud = 0.0
    private var fotoUri: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityConfirmacionSalidaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        latitud = intent.getDoubleExtra("latitud", 0.0)
        longitud = intent.getDoubleExtra("longitud", 0.0)
        fotoUri = intent.getStringExtra("fotoUri")

        binding.txtUbicacionConfirmacionsalida.text = "Lat: $latitud\nLng: $longitud"

        binding.txtFechaConfirmacionsalida.text = SimpleDateFormat(
            "dd/MM/yyyy HH:mm",
            Locale("es", "PE")
        ).format(Date())

        fotoUri?.let {
            binding.imgFotoConfirmacionsalida.setImageURI(Uri.parse(it))
        }

        binding.btnIniciosalida.text = "Registrar salida"

        binding.btnIniciosalida.setOnClickListener {
            registrarSalida()
        }
    }

    private fun registrarSalida() {
        val prefs = getSharedPreferences("MY_ATTENDANCE", MODE_PRIVATE)
        val idusuario = prefs.getInt("idusuario", 0)

        val request = SalidaRequest(
            idusuario = idusuario,
            latitudsalida = latitud,
            longitudsalida = longitud,
            dirsalida = "Ubicación capturada",
            fotosalida = fotoUri
        )

        ApiClient.apiService.registrarSalida(request)
            .enqueue(object : Callback<AsistenciaResponse> {
                override fun onResponse(
                    call: Call<AsistenciaResponse>,
                    response: Response<AsistenciaResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {
                        Toast.makeText(
                            this@ConfirmacionSalidaActivity,
                            response.body()!!.message,
                            Toast.LENGTH_SHORT
                        ).show()

                        val intent = Intent(
                            this@ConfirmacionSalidaActivity,
                            DashboardActivity::class.java
                        )

                        intent.flags =
                            Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP

                        startActivity(intent)
                        finish()
                    } else {
                        Toast.makeText(
                            this@ConfirmacionSalidaActivity,
                            "No se pudo registrar salida",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(call: Call<AsistenciaResponse>, t: Throwable) {
                    Toast.makeText(
                        this@ConfirmacionSalidaActivity,
                        "Error: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }
}