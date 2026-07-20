package com.proyecto.myattendanceapp.ui.utils

import android.app.Activity
import android.content.Intent
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.proyecto.myattendanceapp.R
import com.proyecto.myattendanceapp.ui.home.DashboardActivity
import com.proyecto.myattendanceapp.ui.home.HistorialActivity
import com.proyecto.myattendanceapp.ui.perfil.PerfilActivity

object BottomNavigationHelper {

    fun configurar(
        activity: Activity,
        bottomNavigation: BottomNavigationView,
        selectedItem: Int
    ) {

        bottomNavigation.selectedItemId = selectedItem

        bottomNavigation.setOnItemSelectedListener { item ->

            if (item.itemId == selectedItem) {
                return@setOnItemSelectedListener true
            }

            when (item.itemId) {

                R.id.nav_inicio -> {
                    activity.startActivity(
                        Intent(activity, DashboardActivity::class.java)
                    )
                    activity.finish()
                    true
                }

                R.id.nav_historial -> {
                    activity.startActivity(
                        Intent(activity, HistorialActivity::class.java)
                    )
                    activity.finish()
                    true
                }

                R.id.nav_perfil -> {
                    activity.startActivity(
                        Intent(activity, PerfilActivity::class.java)
                    )
                    activity.finish()
                    true
                }

                else -> false
            }
        }
    }
}