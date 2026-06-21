package com.proyecto.myattendanceapp.ui.asistencia

import android.Manifest
import android.content.pm.PackageManager
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority
import android.content.Intent
import android.net.Uri
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.FileProvider
import com.proyecto.myattendanceapp.databinding.ActivitySalidaBinding
import com.proyecto.myattendanceapp.ui.confirmacion.ConfirmacionSalidaActivity
import java.io.File

class MarcarSalidaActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySalidaBinding
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
                binding.imgPreviewFotosalida.setImageURI(fotoUri)
                binding.imgPreviewFotosalida.visibility = View.VISIBLE
                binding.txtFotoEstadosalida.text = "Fotografía capturada correctamente"
                binding.btnContinuarsalida.visibility = View.VISIBLE
            } else {
                binding.txtFotoEstadosalida.text = "No se capturó la fotografía"
                binding.btnContinuarsalida.isEnabled = true
            }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySalidaBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedLocationClient =
            LocationServices.getFusedLocationProviderClient(this)

        obtenerUbicacion()

        binding.btnCancelarsalida.setOnClickListener {
            finish()
        }

        binding.btnFotosalida.setOnClickListener {
            verificarPermisoCamara()
        }

        binding.btnContinuarsalida.visibility = View.GONE

        binding.btnContinuarsalida.setOnClickListener {
            val intent = Intent(this, ConfirmacionSalidaActivity::class.java)
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

                binding.txtGpsEstadosalida.text =
                    "GPS capturado correctamente"

            } else {

                binding.txtGpsEstadosalida.text =
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
                "salida_${System.currentTimeMillis()}",
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


