package com.proyecto.myattendanceapp.ui.auth


import android.content.Intent
import android.widget.Toast
import com.proyecto.myattendanceapp.data.api.ApiClient
import com.proyecto.myattendanceapp.data.model.RegisterRequest
import com.proyecto.myattendanceapp.data.model.SesionResponse
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.os.Bundle
import android.util.Patterns
import androidx.appcompat.app.AppCompatActivity
import com.proyecto.myattendanceapp.databinding.ActivityRegistroBinding

class RegistroActivity : AppCompatActivity() {

    private lateinit var binding: ActivityRegistroBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityRegistroBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnVolver.setOnClickListener {
            finish()
        }

        binding.tvIniciarSesion.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }

        binding.btnRegistrar.setOnClickListener {
            validarRegistro()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun validarRegistro() {
        val nombres = binding.etNombres.text.toString().trim()
        val apellidos = binding.etApellidos.text.toString().trim()
        val email = binding.etEmailReg.text.toString().trim()
        val direccion = binding.etdireccion.text.toString().trim()
        val telefono = binding.etTelefono.text.toString().trim()
        val password = binding.etPassReg.text.toString().trim()
        val confirmar = binding.etConfirmPass.text.toString().trim()
        val confirmarPassword = binding.etConfirmPass.text.toString().trim()

        limpiarErrores()

        if (nombres.isEmpty()) {
            binding.tilNombres.error = "Ingrese sus nombres"
            return
        }

        if (apellidos.isEmpty()) {
            binding.tilApellidos.error = "Ingrese sus apellidos"
            return
        }

        if (direccion.isEmpty()) {
            binding.tilDireccion.error = "Ingrese su direccion"
            return
        }

        if (email.isEmpty()) {
            binding.tilEmailReg.error = "Ingrese su correo"
            return
        }

        if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            binding.tilEmailReg.error = "Correo inválido"
            return
        }

        if (telefono.isEmpty()) {
            binding.tilTelefono.error = "Ingrese su teléfono"
            return
        }

        if (telefono.length < 9) {
            binding.tilTelefono.error = "Ingrese un teléfono válido"
            return
        }

        if (password.isEmpty()) {
            binding.tilPassReg.error = "Ingrese su contraseña"
            return
        }

        if (password.length < 8) {
            binding.tilPassReg.error = "Mínimo 8 caracteres"
            return
        }

        if (confirmar != password) {
            binding.tilConfirmPass.error = "Las contraseñas no coinciden"
            return
        }

        if (!binding.cbTerminos.isChecked) {
            binding.cbTerminos.error = "Debe aceptar los términos"
            return
        }
        if (confirmarPassword.isEmpty()) {
            binding.tilConfirmPass.error = "Confirme su contraseña"
            return
        }

        if (confirmarPassword.length < 8) {
            binding.tilConfirmPass.error = "Mínimo 8 caracteres"
            return
        }

        if (password != confirmarPassword) {
            binding.tilConfirmPass.error = "Las contraseñas no coinciden"
            return
        }

        registrarApi(nombres, apellidos, direccion, email, telefono, password)


    }

    private fun registrarApi(
        nombres: String,
        apellidos: String,
        direccion: String,
        email: String,
        celular: String,
        password: String
    ) {
        val request = RegisterRequest(
            nombres = nombres,
            apellidos = apellidos,
            direccion = direccion,
            email = email,
            celular = celular,
            password = password
        )

        ApiClient.apiService.register(request).enqueue(object : Callback<SesionResponse> {

            override fun onResponse(
                call: Call<SesionResponse>,
                response: Response<SesionResponse>
            ) {
                if (response.isSuccessful && response.body() != null) {
                    val respuesta = response.body()!!

                    Toast.makeText(
                        this@RegistroActivity,
                        respuesta.message,
                        Toast.LENGTH_SHORT
                    ).show()

                    if (respuesta.success) {
                        startActivity(Intent(this@RegistroActivity, LoginActivity::class.java))
                        finish()
                    }

                } else {
                    Toast.makeText(
                        this@RegistroActivity,
                        "No se pudo registrar el usuario",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }

            override fun onFailure(call: Call<SesionResponse>, t: Throwable) {
                Toast.makeText(
                    this@RegistroActivity,
                    "Error de conexión: ${t.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
        })
    }

    private fun limpiarErrores() {
        binding.tilNombres.error = null
        binding.tilApellidos.error = null
        binding.tilDireccion.error = null
        binding.tilEmailReg.error = null
        binding.tilTelefono.error = null
        binding.tilPassReg.error = null
        binding.tilConfirmPass.error = null
        binding.cbTerminos.error = null
        binding.tilConfirmPass.error = null
    }
}