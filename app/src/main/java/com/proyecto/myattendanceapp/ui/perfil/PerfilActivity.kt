package com.proyecto.myattendanceapp.ui.perfil

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import com.proyecto.myattendanceapp.R
import com.proyecto.myattendanceapp.databinding.ActivityPerfilBinding
import com.proyecto.myattendanceapp.ui.auth.CambiarPasswordActivity
import com.proyecto.myattendanceapp.ui.auth.LoginActivity
import com.proyecto.myattendanceapp.ui.utils.BottomNavigationHelper

class PerfilActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPerfilBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityPerfilBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configurarEventos()
    }

    override fun onResume() {
        super.onResume()

        // Recarga los datos cuando regresamos de editar el perfil
        cargarDatosUsuario()

        BottomNavigationHelper.configurar(
            this,
            binding.bottomNavigation,
            R.id.nav_perfil
        )

    }

    private fun configurarEventos() {

        binding.btnReturn.setOnClickListener {
            finish()
        }

        binding.btnInfoPersonal.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    InformacionPersonalActivity::class.java
                )
            )
        }

        binding.btnCambiarPass.setOnClickListener {
            startActivity(
                Intent(
                    this,
                    CambiarPasswordActivity::class.java
                )
            )
        }

        binding.btnEditarFoto.setOnClickListener {
            // Se implementará luego con el selector de imágenes
        }

        binding.btnCerrarSesion.setOnClickListener {
            confirmarCerrarSesion()
        }

    }

    private fun confirmarCerrarSesion() {

        AlertDialog.Builder(this)
            .setTitle("Cerrar sesión")
            .setMessage("¿Desea cerrar la sesión?")
            .setPositiveButton("Sí") { _, _ ->
                cerrarSesion()
            }
            .setNegativeButton("Cancelar", null)
            .show()
    }
    private fun cargarDatosUsuario() {

        val prefs = getSharedPreferences(
            "MY_ATTENDANCE",
            MODE_PRIVATE
        )

        val nombres = prefs.getString("nombres", "").orEmpty()
        val apellidos = prefs.getString("apellidos", "").orEmpty()
        val email = prefs.getString("email", "").orEmpty()
        val fotoPerfil = prefs.getString("fotoperfil", "").orEmpty()

        binding.txtPerfilName.text =
            "$nombres $apellidos".trim()

        binding.txtPerfilEmail.text = email

        if (fotoPerfil.isNotBlank()) {
            try {
                binding.imgPerfil.setImageURI(
                    Uri.parse(fotoPerfil)
                )
            } catch (e: Exception) {
                binding.imgPerfil.setImageResource(
                    com.proyecto.myattendanceapp.R.drawable.icon_perfil
                )
            }
        }
    }

    private fun cerrarSesion() {

        val preferencias =
            getSharedPreferences(
                "MY_ATTENDANCE",
                MODE_PRIVATE
            )

        preferencias.edit()
            .clear()
            .apply()

        val intent = Intent(
            this,
            LoginActivity::class.java
        )

        intent.flags =
            Intent.FLAG_ACTIVITY_NEW_TASK or
                    Intent.FLAG_ACTIVITY_CLEAR_TASK

        startActivity(intent)

        finish()
    }
}