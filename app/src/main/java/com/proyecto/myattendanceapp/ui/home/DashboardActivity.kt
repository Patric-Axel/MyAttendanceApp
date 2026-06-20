package com.proyecto.myattendanceapp.ui.home

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.proyecto.myattendanceapp.R
import com.proyecto.myattendanceapp.databinding.ActivityDashboardBinding
import com.proyecto.myattendanceapp.ui.asistencia.HistorialActivity
import com.proyecto.myattendanceapp.ui.perfil.PerfilActivity

class DashboardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDashboardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityDashboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        navegarMenu()
    }

    private fun navegarMenu() {

        binding.bottomNavigation.setOnItemSelectedListener { item ->

            when (item.itemId) {

                R.id.nav_inicio -> {
                    true
                }

                R.id.nav_historial -> {
                    startActivity(
                        Intent(this, HistorialActivity::class.java)
                    )
                    true
                }

                R.id.nav_perfil -> {
                    startActivity(
                        Intent(this, PerfilActivity::class.java)
                    )
                    true
                }

                else -> false
            }
        }
    }
}