package com.proyecto.myattendanceapp.ui.asistencia

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.proyecto.myattendanceapp.R
import com.proyecto.myattendanceapp.data.api.ApiClient
import com.proyecto.myattendanceapp.data.model.DetalleAsistenciaResponse
import com.proyecto.myattendanceapp.databinding.ActivityDetalleAsistenciaBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetalleAsistenciaActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetalleAsistenciaBinding

    private var latitudEntrada: Double? = null
    private var longitudEntrada: Double? = null

    private var latitudSalida: Double? = null
    private var longitudSalida: Double? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDetalleAsistenciaBinding.inflate(layoutInflater)

        setContentView(binding.root)

        val idasistencia = intent.getIntExtra(
            "idasistencia", 0
        )

        configurarEventos()

        if (idasistencia <= 0) {
            mostrarMensaje("No se encontró la asistencia")
            finish()
            return
        }

        cargarDetalleAsistencia(idasistencia)
    }

    private fun configurarEventos() {

        binding.btnReturn.setOnClickListener {
            finish()
        }

        binding.btnMapaEntrada.setOnClickListener {
            abrirMapa(
                latitudEntrada, longitudEntrada, "Ubicación de entrada"
            )
        }

        binding.btnMapaSalida.setOnClickListener {
            abrirMapa(
                latitudSalida, longitudSalida, "Ubicación de salida"
            )
        }
    }

    private fun cargarDetalleAsistencia(
        idasistencia: Int
    ) {

        ApiClient.apiService.obtenerDetalleAsistencia(idasistencia)
            .enqueue(object : Callback<DetalleAsistenciaResponse> {

                override fun onResponse(
                    call: Call<DetalleAsistenciaResponse>,
                    response: Response<DetalleAsistenciaResponse>
                ) {

                    if (!response.isSuccessful) {
                        mostrarMensaje(
                            "No se pudo cargar el detalle"
                        )
                        return
                    }

                    val detalle = response.body()

                    if (detalle == null) {
                        mostrarMensaje(
                            "El servidor devolvió una respuesta vacía"
                        )
                        return
                    }

                    mostrarDetalle(detalle)
                }

                override fun onFailure(
                    call: Call<DetalleAsistenciaResponse>, throwable: Throwable
                ) {

                    mostrarMensaje(
                        "Error de conexión: ${
                            throwable.message ?: "Error desconocido"
                        }"
                    )
                }
            })
    }

    private fun mostrarDetalle(
        detalle: DetalleAsistenciaResponse
    ) {

        binding.txtFecha.text = "Fecha: ${detalle.fecha ?: "--"}"

        binding.txtEstado.text = "Estado: ${detalle.estado ?: "Sin estado"}"

        binding.txtHoraEntrada.text = "Hora de entrada: ${
            formatearHora(detalle.horaentrada)
        }"

        binding.txtHoraSalida.text = "Hora de salida: ${
            formatearHora(detalle.horasalida)
        }"

        binding.txtHorasTrabajadas.text = "Horas trabajadas: ${
            detalle.horastrabajadas ?: "--"
        }"

        binding.txtDireccionEntrada.text =
            detalle.direntrada?.takeIf { it.isNotBlank() } ?: "Dirección de entrada no disponible"

        binding.txtDireccionSalida.text =
            detalle.dirsalida?.takeIf { it.isNotBlank() } ?: "Dirección de salida no disponible"

        binding.txtObservacion.text =
            detalle.observacion?.takeIf { it.isNotBlank() } ?: "Sin observaciones"

        latitudEntrada = detalle.latitudentrada
        longitudEntrada = detalle.longitudentrada

        latitudSalida = detalle.latitudsalida
        longitudSalida = detalle.longitudsalida

        binding.btnMapaEntrada.isEnabled = coordenadasValidas(
            latitudEntrada, longitudEntrada
        )

        binding.btnMapaSalida.isEnabled = coordenadasValidas(
            latitudSalida, longitudSalida
        )

        mostrarImagen(
            detalle.fotoentrada, binding.imgEntrada
        )

        mostrarImagen(
            detalle.fotosalida, binding.imgSalida
        )
    }

    private fun formatearHora(
        hora: String?
    ): String {

        if (hora.isNullOrBlank()) {
            return "--"
        }

        return if (hora.length >= 5) {
            hora.substring(0, 5)
        } else {
            hora
        }
    }

    private fun coordenadasValidas(
        latitud: Double?, longitud: Double?
    ): Boolean {

        return latitud != null && longitud != null && latitud != 0.0 && longitud != 0.0
    }

    private fun abrirMapa(
        latitud: Double?, longitud: Double?, etiqueta: String
    ) {

        if (!coordenadasValidas(latitud, longitud)) {
            mostrarMensaje("Ubicación no disponible")
            return
        }

        val uri = Uri.parse(
            "geo:$latitud,$longitud?q=$latitud,$longitud" + "(${Uri.encode(etiqueta)})"
        )

        val intent = Intent(
            Intent.ACTION_VIEW, uri
        )

        intent.setPackage(
            "com.google.android.apps.maps"
        )

        if (intent.resolveActivity(packageManager) != null) {
            startActivity(intent)
        } else {

            val navegador = Intent(
                Intent.ACTION_VIEW, Uri.parse(
                    "https://www.google.com/maps/search/" + "?api=1&query=$latitud,$longitud"
                )
            )

            startActivity(navegador)
        }
    }

    private fun mostrarImagen(
        ruta: String?, imageView: ImageView
    ) {

        if (ruta.isNullOrBlank()) {
            imageView.setImageResource(
                R.drawable.icon_perfil
            )
            return
        }

        try {
            imageView.setImageURI(
                Uri.parse(ruta)
            )
        } catch (exception: Exception) {
            imageView.setImageResource(
                R.drawable.icon_perfil
            )
        }
    }

    private fun mostrarMensaje(
        mensaje: String
    ) {

        Toast.makeText(
            this, mensaje, Toast.LENGTH_SHORT
        ).show()
    }
}