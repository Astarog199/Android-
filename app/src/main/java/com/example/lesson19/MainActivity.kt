package com.example.lesson19

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.location.Location
import android.location.LocationRequest
import android.os.Bundle
import android.os.Looper
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContract
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.lesson19.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.LocationServices
import com.google.android.gms.location.Priority

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private lateinit var fusedClient: FusedLocationProviderClient

    private val launcher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ){map ->
        if (map.values.isNotEmpty() && map.values.all { it }){
            startLocation()
        }
    }

    private val locationCallback = object : LocationCallback(){
        override fun onLocationResult(result: LocationResult) {
            binding.message.text = result.locations.toString()
        }
    }

    /**
     * Получение координат
     */
    @SuppressLint("MissingPermission")
    private fun startLocation() {
       val request = com.google.android.gms.location.LocationRequest.create()
           .setInterval(1000)
           .setPriority(Priority.PRIORITY_HIGH_ACCURACY)

        fusedClient.requestLocationUpdates(
            request,
            locationCallback,
            Looper.getMainLooper()
        )
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        fusedClient = LocationServices.getFusedLocationProviderClient(this)

    }

    override fun onStart() {
        super.onStart()
        checkPermissions()
    }

    override fun onStop() {
        super.onStop()
        fusedClient.removeLocationUpdates(locationCallback)
    }

    private fun checkPermissions(){
        if (REQUIRED_PERMISSIONS.all { permission->
                ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED
            }) {
            startLocation()
        } else{
            launcher.launch(REQUIRED_PERMISSIONS)
        }
    }

    companion object{
        private val REQUIRED_PERMISSIONS: Array<String> = arrayOf(
            Manifest.permission.ACCESS_FINE_LOCATION,
            Manifest.permission.ACCESS_COARSE_LOCATION

        )
    }
}