package com.proyecto.myattendanceapp.ui.asistencia

import com.proyecto.myattendanceapp.R
import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.proyecto.myattendanceapp.databinding.ActivityEntradaBinding
import com.google.android.gms.location.Priority
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import com.proyecto.myattendanceapp.ui.confirmacion.ConfirmacionEntradaActivity
import java.io.File

class MarcarEntradaActivity : AppCompatActivity() {

        private lateinit var binding: ActivityEntradaBinding
        private lateinit var fusedLocationClient: FusedLocationProviderClient
        private var latitud: Double? = null
        private var longitud: Double? = null
        private var fotoUri: Uri? = null

    private val cameraPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { granted ->
            if (granted) {
                abrirCamara()
            } else {
                Toast.makeText(this, "Permiso de cámara denegado", Toast.LENGTH_SHORT).show()
            }
        }
    private val tomarFotoLauncher =
        registerForActivityResult(ActivityResultContracts.TakePicture()) { success ->

            Toast.makeText(this, "Resultado cámara: $success", Toast.LENGTH_LONG).show()

            if (success && fotoUri != null) {
                binding.imgPreviewFoto.setImageURI(fotoUri)
                binding.imgPreviewFoto.visibility = View.VISIBLE
                binding.txtFotoEstado.text = "Fotografía capturada correctamente"
                binding.btnContinuar.visibility = View.VISIBLE
            } else {
                binding.txtFotoEstado.text = "No se capturó la fotografía"
                binding.btnContinuar.isEnabled = true
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityEntradaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(this)

        obtenerUbicacion()

        binding.btnCancelar.setOnClickListener {
            finish()
        }

        binding.btnFoto.setOnClickListener {
            verificarPermisoCamara()
        }

        binding.btnContinuar.visibility = View.GONE

        binding.btnContinuar.setOnClickListener {
            val intent = Intent(this, ConfirmacionEntradaActivity::class.java)
            intent.putExtra("latitud", latitud ?: 0.0)
            intent.putExtra("longitud", longitud ?: 0.0)
            intent.putExtra("fotoUri", fotoUri?.toString())
            startActivity(intent)
        }
    }

    private fun obtenerUbicacion() {

        if (
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.ACCESS_FINE_LOCATION
            ) != PackageManager.PERMISSION_GRANTED
        ) {

            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                100
            )
            return
        }

        fusedLocationClient.getCurrentLocation(
            Priority.PRIORITY_HIGH_ACCURACY,
            null
        ).addOnSuccessListener { location ->

            if (location != null) {

                latitud = location.latitude
                longitud = location.longitude

                binding.txtGpsEstado.text =
                    "GPS capturado correctamente"

            } else {

                binding.txtGpsEstado.text =
                    "No se pudo obtener ubicación"
            }
        }
    }

    private fun verificarPermisoCamara() {
        if (
            ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.CAMERA
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            abrirCamara()
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(
            requestCode,
            permissions,
            grantResults
        )

        if (
            requestCode == 100 &&
            grantResults.isNotEmpty() &&
            grantResults[0] == PackageManager.PERMISSION_GRANTED
        ) {
            obtenerUbicacion()
        }
    }

    private fun abrirCamara() {
        try {
            val archivoFoto = File.createTempFile(
                "entrada_${System.currentTimeMillis()}",
                ".jpg",
                externalCacheDir
            )

            fotoUri = FileProvider.getUriForFile(
                this,
                "${packageName}.fileprovider",
                archivoFoto
            )

            tomarFotoLauncher.launch(fotoUri!!)
        } catch (e: Exception) {
            Toast.makeText(this, e.message, Toast.LENGTH_LONG).show()
        }
    }

}


