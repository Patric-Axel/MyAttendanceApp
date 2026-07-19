package com.proyecto.myattendanceapp.ui.confirmacion

import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.net.Uri
import android.os.Build
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

    private var direccionObtenida = "Dirección no disponible"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityConfirmacionSalidaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        latitud = intent.getDoubleExtra("latitud", 0.0)
        longitud = intent.getDoubleExtra("longitud", 0.0)
        fotoUri = intent.getStringExtra("fotoUri")

        binding.txtUbicacionConfirmacionsalida.text =
            "Lat: $latitud\nLng: $longitud"

        binding.txtdireccion.text = "Buscando dirección..."
        binding.btnIniciosalida.isEnabled = false

        mostrarFechaHora()
        mostrarFoto()
        obtenerDireccion(latitud, longitud)

        binding.btnIniciosalida.text = "Registrar salida"

        binding.btnIniciosalida.setOnClickListener {
            registrarSalida()
        }
    }

    private fun mostrarFechaHora() {
        val fechaActual = Date()
        val localePeru = Locale("es", "PE")

        binding.txtFechaConfirmacionsalida.text = SimpleDateFormat(
            "dd/MM/yyyy",
            localePeru
        ).format(fechaActual)

        binding.txtHoraConfirmacionsalida.text = SimpleDateFormat(
            "HH:mm",
            localePeru
        ).format(fechaActual)
    }

    private fun mostrarFoto() {
        fotoUri?.let { uri ->
            try {
                binding.imgFotoConfirmacionsalida.setImageURI(
                    Uri.parse(uri)
                )
            } catch (e: Exception) {
                Toast.makeText(
                    this,
                    "No se pudo mostrar la fotografía",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun obtenerDireccion(lat: Double, lng: Double) {

        if (lat == 0.0 && lng == 0.0) {
            direccionObtenida = "Coordenadas no válidas"
            binding.txtdireccion.text = direccionObtenida
            binding.btnIniciosalida.isEnabled = true
            return
        }

        if (!Geocoder.isPresent()) {
            direccionObtenida = "Servicio de dirección no disponible"
            binding.txtdireccion.text = direccionObtenida
            binding.btnIniciosalida.isEnabled = true
            return
        }

        val geocoder = Geocoder(
            this,
            Locale("es", "PE")
        )

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            geocoder.getFromLocation(
                lat,
                lng,
                1,
                object : Geocoder.GeocodeListener {

                    override fun onGeocode(addresses: MutableList<Address>) {
                        runOnUiThread {
                            procesarDirecciones(addresses)
                        }
                    }

                    override fun onError(errorMessage: String?) {
                        runOnUiThread {
                            direccionObtenida = "Dirección no disponible"
                            binding.txtdireccion.text = direccionObtenida
                            binding.btnIniciosalida.isEnabled = true

                            Toast.makeText(
                                this@ConfirmacionSalidaActivity,
                                errorMessage
                                    ?: "No se pudo obtener la dirección",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            )

        } else {

            Thread {
                try {
                    @Suppress("DEPRECATION")
                    val direcciones = geocoder.getFromLocation(
                        lat,
                        lng,
                        1
                    )

                    runOnUiThread {
                        procesarDirecciones(
                            direcciones ?: emptyList()
                        )
                    }

                } catch (e: Exception) {
                    runOnUiThread {
                        direccionObtenida = "Dirección no disponible"
                        binding.txtdireccion.text = direccionObtenida
                        binding.btnIniciosalida.isEnabled = true
                    }
                }
            }.start()
        }
    }

    private fun procesarDirecciones(
        direcciones: List<Address>
    ) {
        direccionObtenida = if (direcciones.isNotEmpty()) {
            direcciones[0].getAddressLine(0)
                ?: construirDireccion(direcciones[0])
        } else {
            "Dirección no disponible"
        }

        binding.txtdireccion.text = direccionObtenida
        binding.btnIniciosalida.isEnabled = true
    }

    private fun construirDireccion(
        address: Address
    ): String {
        return listOfNotNull(
            address.thoroughfare,
            address.subThoroughfare,
            address.subLocality,
            address.locality,
            address.adminArea,
            address.countryName
        )
            .filter { it.isNotBlank() }
            .distinct()
            .joinToString(", ")
            .ifBlank {
                "Dirección no disponible"
            }
    }

    private fun registrarSalida() {
        val prefs = getSharedPreferences(
            "MY_ATTENDANCE",
            MODE_PRIVATE
        )

        val idusuario = prefs.getInt(
            "idusuario",
            0
        )

        if (idusuario == 0) {
            Toast.makeText(
                this,
                "No se encontró la sesión del usuario",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        if (latitud == 0.0 && longitud == 0.0) {
            Toast.makeText(
                this,
                "No se obtuvo una ubicación válida",
                Toast.LENGTH_SHORT
            ).show()
            return
        }

        val request = SalidaRequest(
            idusuario = idusuario,
            latitudsalida = latitud,
            longitudsalida = longitud,
            dirsalida = direccionObtenida,
            fotosalida = fotoUri
        )

        binding.btnIniciosalida.isEnabled = false

        ApiClient.apiService.registrarSalida(request)
            .enqueue(object : Callback<AsistenciaResponse> {

                override fun onResponse(
                    call: Call<AsistenciaResponse>,
                    response: Response<AsistenciaResponse>
                ) {
                    binding.btnIniciosalida.isEnabled = true

                    if (
                        response.isSuccessful &&
                        response.body() != null
                    ) {
                        Toast.makeText(
                            this@ConfirmacionSalidaActivity,
                            response.body()!!.message,
                            Toast.LENGTH_SHORT
                        ).show()

                        val intent = Intent(
                            this@ConfirmacionSalidaActivity,
                            DashboardActivity::class.java
                        ).apply {
                            flags =
                                Intent.FLAG_ACTIVITY_CLEAR_TOP or
                                        Intent.FLAG_ACTIVITY_SINGLE_TOP
                        }

                        startActivity(intent)
                        finish()

                    } else {
                        Toast.makeText(
                            this@ConfirmacionSalidaActivity,
                            "No se pudo registrar la salida",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }

                override fun onFailure(
                    call: Call<AsistenciaResponse>,
                    t: Throwable
                ) {
                    binding.btnIniciosalida.isEnabled = true

                    Toast.makeText(
                        this@ConfirmacionSalidaActivity,
                        "Error: ${t.message}",
                        Toast.LENGTH_LONG
                    ).show()
                }
            })
    }
}