package com.proyecto.myattendanceapp.ui.perfil

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.proyecto.myattendanceapp.data.api.ApiClient
import com.proyecto.myattendanceapp.data.model.ActualizarPerfilRequest
import com.proyecto.myattendanceapp.data.model.PerfilResponse
import com.proyecto.myattendanceapp.databinding.ActivityInformacionPersonalBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class InformacionPersonalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityInformacionPersonalBinding

    private val prefs by lazy {
        getSharedPreferences(
            "MY_ATTENDANCE", MODE_PRIVATE
        )
    }

    private var idusuario: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityInformacionPersonalBinding.inflate(layoutInflater)

        setContentView(binding.root)

        cargarDatosUsuario()
        configurarEventos()
    }

    private fun configurarEventos() {

        binding.btnReturn.setOnClickListener {
            finish()
        }

        binding.btnGuardar.setOnClickListener {
            validarFormulario()
        }
    }

    private fun cargarDatosUsuario() {

        idusuario = prefs.getInt(
            "idusuario", 0
        )

        val nombres = prefs.getString(
            "nombres", ""
        ).orEmpty()

        val apellidos = prefs.getString(
            "apellidos", ""
        ).orEmpty()

        val email = prefs.getString(
            "email", ""
        ).orEmpty()

        val celular = prefs.getString(
            "celular", ""
        ).orEmpty()

        val direccion = prefs.getString(
            "direccion", ""
        ).orEmpty()

        binding.etNombres.setText(nombres)
        binding.etApellidos.setText(apellidos)
        binding.etEmail.setText(email)
        binding.etCelular.setText(celular)
        binding.etDireccion.setText(direccion)
    }

    private fun validarFormulario() {

        val celular = binding.etCelular.text?.toString()?.trim().orEmpty()

        val direccion = binding.etDireccion.text?.toString()?.trim().orEmpty()

        limpiarErrores()

        if (idusuario <= 0) {
            mostrarMensaje(
                "No se pudo identificar al usuario"
            )
            return
        }

        if (celular.isEmpty()) {
            binding.tilCelular.error = "Ingrese su número de celular"

            binding.etCelular.requestFocus()
            return
        }

        if (!celular.matches(Regex("^9\\d{8}$"))) {
            binding.tilCelular.error = "Ingrese un celular válido de 9 dígitos"

            binding.etCelular.requestFocus()
            return
        }

        if (direccion.isEmpty()) {
            binding.tilDireccion.error = "Ingrese su dirección"

            binding.etDireccion.requestFocus()
            return
        }

        if (direccion.length < 5) {
            binding.tilDireccion.error = "Ingrese una dirección válida"

            binding.etDireccion.requestFocus()
            return
        }

        actualizarPerfil(
            celular = celular, direccion = direccion
        )
    }

    private fun limpiarErrores() {
        binding.tilCelular.error = null
        binding.tilDireccion.error = null
    }

    private fun actualizarPerfil(
        celular: String, direccion: String
    ) {

        bloquearFormulario(true)

        val request = ActualizarPerfilRequest(
            direccion = direccion, celular = celular
        )

        ApiClient.apiService.actualizarPerfil(
                idusuario, request
            ).enqueue(object : Callback<PerfilResponse> {

                override fun onResponse(
                    call: Call<PerfilResponse>, response: Response<PerfilResponse>
                ) {

                    bloquearFormulario(false)

                    if (!response.isSuccessful) {
                        mostrarMensaje(
                            obtenerMensajeError(
                                response.code()
                            )
                        )
                        return
                    }

                    val perfil = response.body()

                    if (perfil == null) {
                        mostrarMensaje(
                            "El servidor devolvió una respuesta vacía"
                        )
                        return
                    }

                    guardarCambiosLocales(
                        celular = perfil.celular ?: celular,
                        direccion = perfil.direccion ?: direccion
                    )

                    mostrarMensaje(
                        "Perfil actualizado correctamente"
                    )

                    finish()
                }

                override fun onFailure(
                    call: Call<PerfilResponse>, throwable: Throwable
                ) {

                    bloquearFormulario(false)

                    mostrarMensaje(
                        "No se pudo conectar con el servidor: " + (throwable.message
                            ?: "Error desconocido")
                    )
                }
            })
    }

    private fun guardarCambiosLocales(
        celular: String, direccion: String
    ) {

        prefs.edit().putString(
                "celular", celular
            ).putString(
                "direccion", direccion
            ).apply()
    }

    private fun bloquearFormulario(
        bloquear: Boolean
    ) {

        binding.btnGuardar.isEnabled = !bloquear
        binding.etCelular.isEnabled = !bloquear
        binding.etDireccion.isEnabled = !bloquear

        binding.btnGuardar.text = if (bloquear) {
            "Guardando..."
        } else {
            "Guardar cambios"
        }
    }

    private fun obtenerMensajeError(
        codigo: Int
    ): String {

        return when (codigo) {
            400 -> "Revise los datos ingresados"
            404 -> "Usuario no encontrado"
            500 -> "Ocurrió un error en el servidor"
            else -> "No se pudo actualizar el perfil. Código: $codigo"
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