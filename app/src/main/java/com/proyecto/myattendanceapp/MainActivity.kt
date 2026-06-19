package com.proyecto.myattendanceapp

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.proyecto.myattendanceapp.databinding.ActivityMainBinding
import com.proyecto.myattendanceapp.ui.auth.LoginActivity
import com.proyecto.myattendanceapp.ui.auth.RegistroActivity

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnLogin.setOnClickListener {
            startActivity(Intent(this, LoginActivity::class.java))
        }

        binding.btnRegistrar.setOnClickListener {
            startActivity(Intent(this, RegistroActivity::class.java))
        }
    }
}