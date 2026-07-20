package com.proyecto.myattendanceapp.ui.auth

import android.os.Bundle
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.proyecto.myattendanceapp.data.api.ApiClient
import com.proyecto.myattendanceapp.data.model.ActualizarPasswordRequest
import com.proyecto.myattendanceapp.data.model.PerfilResponse
import com.proyecto.myattendanceapp.databinding.ActivityCambiarPasswordBinding
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class CambiarPasswordActivity : AppCompatActivity() {

    private lateinit var binding: ActivityCambiarPasswordBinding

    private val prefs by lazy {
        getSharedPreferences(
            "MY_ATTENDANCE",
            MODE_PRIVATE
        )
    }

    private var idusuario: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityCambiarPasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        idusuario = prefs.getInt("idusuario", 0)

        if (idusuario <= 0) {
            mostrarMensaje("No se encontró la sesión del usuario")
            finish()
            return
        }

        configurarEventos()
    }

    private fun configurarEventos() {

        binding.btnReturn.setOnClickListener {
            finish()
        }

        binding.btnActualizarPassword.setOnClickListener {
            validarFormulario()
        }
    }

    private fun validarFormulario() {

        limpiarErrores()

        val passwordActual =
            binding.etPasswordActual.text
                ?.toString()
                .orEmpty()

        val passwordNueva =
            binding.etPasswordNueva.text
                ?.toString()
                .orEmpty()

        val confirmarPassword =
            binding.etConfirmarPassword.text
                ?.toString()
                .orEmpty()

        when {
            passwordActual.isBlank() -> {
                binding.tilPasswordActual.error =
                    "Ingrese su contraseña actual"

                binding.etPasswordActual.requestFocus()
            }

            passwordNueva.isBlank() -> {
                binding.tilPasswordNueva.error =
                    "Ingrese una nueva contraseña"

                binding.etPasswordNueva.requestFocus()
            }

            passwordNueva.length < 6 -> {
                binding.tilPasswordNueva.error =
                    "Debe tener al menos 6 caracteres"

                binding.etPasswordNueva.requestFocus()
            }

            confirmarPassword.isBlank() -> {
                binding.tilConfirmarPassword.error =
                    "Confirme la nueva contraseña"

                binding.etConfirmarPassword.requestFocus()
            }

            passwordNueva != confirmarPassword -> {
                binding.tilConfirmarPassword.error =
                    "Las contraseñas no coinciden"

                binding.etConfirmarPassword.requestFocus()
            }

            passwordActual == passwordNueva -> {
                binding.tilPasswordNueva.error =
                    "La nueva contraseña debe ser diferente"

                binding.etPasswordNueva.requestFocus()
            }

            else -> {
                ocultarTeclado()

                actualizarPassword(
                    passwordActual,
                    passwordNueva,
                    confirmarPassword
                )
            }
        }
    }

    private fun actualizarPassword(
        passwordActual: String,
        passwordNueva: String,
        confirmarPassword: String
    ) {

        mostrarCarga(true)

        val request = ActualizarPasswordRequest(
            passwordActual = passwordActual,
            passwordNueva = passwordNueva,
            confirmarPassword = confirmarPassword
        )

        ApiClient.apiService
            .actualizarPassword(
                idusuario,
                request
            )
            .enqueue(
                object : Callback<PerfilResponse> {

                    override fun onResponse(
                        call: Call<PerfilResponse>,
                        response: Response<PerfilResponse>
                    ) {

                        mostrarCarga(false)

                        if (response.isSuccessful &&
                            response.body() != null
                        ) {

                            mostrarMensaje(
                                "Contraseña actualizada correctamente"
                            )

                            limpiarCampos()

                            finish()
                            return
                        }

                        mostrarMensaje(
                            obtenerMensajeError(response)
                        )
                    }

                    override fun onFailure(
                        call: Call<PerfilResponse>,
                        throwable: Throwable
                    ) {

                        mostrarCarga(false)

                        mostrarMensaje(
                            "Error de conexión: ${
                                throwable.message
                                    ?: "No se pudo conectar con el servidor"
                            }"
                        )
                    }
                }
            )
    }

    private fun obtenerMensajeError(
        response: Response<PerfilResponse>
    ): String {

        val codigo = response.code()

        return when (codigo) {
            400 -> "Verifique los datos ingresados"
            401 -> "La contraseña actual es incorrecta"
            404 -> "Usuario no encontrado"
            500 -> "No se pudo actualizar la contraseña"
            else -> "Error al actualizar la contraseña"
        }
    }

    private fun limpiarErrores() {
        binding.tilPasswordActual.error = null
        binding.tilPasswordNueva.error = null
        binding.tilConfirmarPassword.error = null
    }

    private fun limpiarCampos() {
        binding.etPasswordActual.text?.clear()
        binding.etPasswordNueva.text?.clear()
        binding.etConfirmarPassword.text?.clear()
    }

    private fun mostrarCarga(mostrar: Boolean) {

        binding.progressBar.visibility =
            if (mostrar) View.VISIBLE else View.GONE

        binding.btnActualizarPassword.isEnabled = !mostrar

        binding.etPasswordActual.isEnabled = !mostrar
        binding.etPasswordNueva.isEnabled = !mostrar
        binding.etConfirmarPassword.isEnabled = !mostrar
    }

    private fun ocultarTeclado() {

        val inputMethodManager =
            getSystemService(INPUT_METHOD_SERVICE)
                    as InputMethodManager

        inputMethodManager.hideSoftInputFromWindow(
            binding.root.windowToken,
            0
        )
    }

    private fun mostrarMensaje(mensaje: String) {

        Toast.makeText(
            this,
            mensaje,
            Toast.LENGTH_SHORT
        ).show()
    }
}