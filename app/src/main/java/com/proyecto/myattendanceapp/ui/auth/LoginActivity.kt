package com.proyecto.myattendanceapp.ui.auth

import android.content.Intent
import android.widget.Toast
import com.proyecto.myattendanceapp.data.api.ApiClient
import com.proyecto.myattendanceapp.data.model.LoginRequest
import com.proyecto.myattendanceapp.data.model.SesionResponse
import com.proyecto.myattendanceapp.ui.home.DashboardActivity
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.proyecto.myattendanceapp.databinding.ActivityLoginBinding
    class LoginActivity : AppCompatActivity() {

        private lateinit var binding: ActivityLoginBinding

        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)

            binding = ActivityLoginBinding.inflate(layoutInflater)
            setContentView(binding.root)

            binding.tvRegistrar.setOnClickListener {

                startActivity(
                    Intent(this, RegistroActivity::class.java)
                )
                finish()
            }
            binding.btnLogin.setOnClickListener {
                validarLogin()
            }

        }

        //validacion de acceso
        private fun validarLogin() {
            val email = binding.etEmail.text.toString().trim()
            val password = binding.etPassword.text.toString().trim()

            binding.tilEmail.error = null
            binding.tilPassword.error = null

            if (email.isEmpty()) {
                binding.tilEmail.error = "Ingrese su correo"
                return
            }

            if (!android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                binding.tilEmail.error = "Correo inválido"
                return
            }

            if (password.isEmpty()) {
                binding.tilPassword.error = "Ingrese su contraseña"
                return
            }

            if (password.length < 8) {
                binding.tilPassword.error = "Mínimo 8 caracteres"
                return
            }
                loginApi(email, password)
        }

        // Aquí luego irá Retrofit
        private fun loginApi(email: String, password: String) {

            val request = LoginRequest(email, password)

            ApiClient.apiService.login(request).enqueue(object : Callback<SesionResponse> {
                override fun onResponse(
                    call: Call<SesionResponse>,
                    response: Response<SesionResponse>
                ) {
                    if (response.isSuccessful && response.body() != null) {

                        val respuesta = response.body()!!

                        if (respuesta.success) {

                            val prefs = getSharedPreferences("MY_ATTENDANCE", MODE_PRIVATE)

                            prefs.edit()
                                .putInt("idusuario", respuesta.userId ?: 0)
                                .putString("nombres", respuesta.nombres ?: "")
                                .putString("apellidos", respuesta.apellidos ?: "")
                                .putString("email", respuesta.email ?: "")
                                .putInt("idrol", respuesta.idrol ?: 0)
                                .putInt("idestado", respuesta.idestado ?: 0)
                                .putString("celular", respuesta.celular ?: "")
                                .putString("direccion", respuesta.direccion ?: "")
                                .putString("fotoperfil", respuesta.fotoperfil ?: "")
                                .apply()

                            Toast.makeText(this@LoginActivity, respuesta.message, Toast.LENGTH_SHORT).show()

                            val intent = Intent(this@LoginActivity, DashboardActivity::class.java)

                            intent.putExtra("idusuario", respuesta.userId)
                            intent.putExtra("nombres", respuesta.nombres)
                            intent.putExtra("apellidos", respuesta.apellidos)
                            intent.putExtra("email", respuesta.email)

                            startActivity(intent)
                            finish()

                        } else {
                            Toast.makeText(this@LoginActivity, respuesta.message, Toast.LENGTH_SHORT).show()
                        }

                    } else {
                        Toast.makeText(this@LoginActivity, "Credenciales incorrectas", Toast.LENGTH_SHORT).show()
                    }

                }

                override fun onFailure(call: Call<SesionResponse>, t: Throwable) {
                    Toast.makeText(this@LoginActivity, "Error de conexión: ${t.message}", Toast.LENGTH_LONG).show()
                }
            })
        }


    }