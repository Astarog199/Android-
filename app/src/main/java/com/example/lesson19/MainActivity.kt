package com.example.lesson19

import android.Manifest
import android.annotation.SuppressLint
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Looper
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.lesson19.databinding.ActivityMainBinding
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationResult
import com.google.android.gms.location.Priority
import com.yandex.mapkit.MapKitFactory
import com.yandex.mapkit.mapview.MapView

val API_KEY = "cf392e39-1083-4b44-9db4-56b272957353"
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    //объект FusedLocationProviderClient используется для управления запросами местоположения.
    private lateinit var fusedClient: FusedLocationProviderClient
    private lateinit var mapView: MapView

    private val launcher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ){map ->
        if (map.values.isNotEmpty() && map.values.all { it }){
            startLocation()
        }
    }

    private val locationCallback = object : LocationCallback(){
        override fun onLocationResult(result: LocationResult) {
//            binding.message.text = result.locations.toString()
        }
    }

    /**
     * Получение координат c интервалом в 1 секунду
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
        MapKitFactory.setApiKey(API_KEY)
        MapKitFactory.initialize(this)
        mapView = findViewById(R.id.mapview)
    }

    /**
     * проверяются разрешения на доступ к местоположению пользователя.
     */
    override fun onStart() {
        super.onStart()
        checkPermissions()
        MapKitFactory.getInstance().onStart()
        mapView.onStart()
    }

    /**
     * отменяются все обновления местоположения, чтобы избежать ненужного использования ресурсов.
     */
    override fun onStop() {
        super.onStop()
        fusedClient.removeLocationUpdates(locationCallback)
        MapKitFactory.getInstance().onStop()
        super.onStop()
    }


    /**
     * Метод проверяет предоставлены ли все необходимые разрешения.
     * Если нет, то вызывается launcher.launch для запроса разрешений у пользователя.
     */
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