package com.example.inteligentecidade

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.location.Geocoder
import android.location.Location
import android.location.LocationManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.CheckBox
import androidx.core.app.ActivityCompat
import androidx.lifecycle.ViewModelProvider
import com.example.inteligentecidade.api.EndPoints
import com.example.inteligentecidade.api.Report
import com.example.inteligentecidade.api.ServiceBuilder
import com.example.inteligentecidade.viewModel.NotaViewModel
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.floatingactionbutton.FloatingActionButton
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MapActivity : AppCompatActivity(), OnMapReadyCallback, GoogleMap.OnMarkerClickListener{

    private lateinit var map: GoogleMap
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private lateinit var lastLocation: Location
    lateinit var username : String
    lateinit var password : String
    lateinit var id_user : String
    private lateinit var sharedPreferences: SharedPreferences

    companion object {
        private const val LOCATION_PERMISSION_REQUEST_CODE = 1
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map)

        id_user = intent.getStringExtra("id_user").toString()
        username = intent.getStringExtra("username").toString()

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        val mapFragment = supportFragmentManager
            .findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync(this)
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        val fab = findViewById<FloatingActionButton>(R.id.fabMap)
        fab.setOnClickListener {
            abreReportFab(lastLocation)
        }
    }

    override fun onResume() {
        super.onResume()
        carregaReports()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menumap, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.logout -> {
                val sharedPref : SharedPreferences = getSharedPreferences(getString(R.string.preference_key), Context.MODE_PRIVATE)
                with(sharedPref.edit()){
                    clear()
                    commit()
                    apply()
                    val intent = Intent(this@MapActivity, MainActivity::class.java)
                    startActivity(intent)
                    finishAffinity()
                }
                true
            }

            else -> super.onOptionsItemSelected(item)
        }

    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        map.uiSettings.isZoomControlsEnabled = true
        map.setOnMarkerClickListener(this)

        setUpMap()
        setMapLongClick(map)
    }

    private fun setUpMap() {
        if (ActivityCompat.checkSelfPermission(this,
                        android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    arrayOf(android.Manifest.permission.ACCESS_FINE_LOCATION), LOCATION_PERMISSION_REQUEST_CODE)
            return
        }
        map.isMyLocationEnabled = true

// 2
        fusedLocationClient.lastLocation.addOnSuccessListener(this) { location ->
            // Got last known location. In some rare situations this can be null.
            // 3
            if (location != null) {
                lastLocation = location
                val currentLatLng = LatLng(location.latitude, location.longitude)
                map.animateCamera(CameraUpdateFactory.newLatLngZoom(currentLatLng, 12f))
            }
        }
    }

    override fun onMarkerClick(p0: Marker?) = false

    private fun getAddress(lat :Double, long: Double):String?{
        val geocoder = Geocoder(this)
        val list = geocoder.getFromLocation(lat, long, 1)
        return list[0].getAddressLine(0)
    }

    private fun setMapLongClick(map: GoogleMap) {
        map.setOnMapLongClickListener { latLng ->
            map.addMarker(
                    MarkerOptions()
                            .position(latLng)
            )
            val intent = Intent(this@MapActivity, ReportActivity::class.java)
            intent.putExtra("lat", latLng.latitude.toString())
            intent.putExtra("long", latLng.longitude.toString())
            val address = getAddress(latLng.latitude, latLng.longitude)
            intent.putExtra("morada", address)
            startActivity(intent)
        }
    }

    private fun carregaReports(){
        val request = ServiceBuilder.buildService(EndPoints::class.java)

        val call = request.getReports()

        call.enqueue(object : Callback<List<Report>> {

            override fun onResponse(call: Call<List<Report>>, response: Response<List<Report>>) {
                if (response.isSuccessful){
                    var reports = response.body();
                    sharedPreferences = getSharedPreferences(getString(R.string.preference_key), Context.MODE_PRIVATE)
                    val id_user = sharedPreferences.getString(getString(R.string.id_user), "0")
                    reports?.forEach {
                        if(it.id_user==id_user){
                            val posicao = LatLng(it.latitude.toDouble(), it.longitude.toDouble())
                            map.addMarker(
                                    MarkerOptions()
                                            .position(posicao)
                                            .title(it.titulo)
                                            .snippet(it.tipo)
                                            .icon(
                                                    BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_GREEN)
                                            )
                            )
                        }else{
                            val posicao = LatLng(it.latitude.toDouble(), it.longitude.toDouble())
                            map.addMarker(
                                    MarkerOptions()
                                            .position(posicao)
                                            .title(it.titulo)
                                            .snippet(it.tipo)
                                            .icon(
                                                    BitmapDescriptorFactory.defaultMarker(BitmapDescriptorFactory.HUE_RED)
                                            )
                            )
                        }
                    }
                }
            }

            override fun onFailure(call: Call<List<Report>>, t: Throwable) {
                Log.d("REPORTS" , "NAO CARREGADOS")
            }
        })
    }

    private fun abreReportFab(lastLocation: Location){
        val intent = Intent(this@MapActivity, ReportActivity::class.java)
        intent.putExtra("lat", lastLocation.latitude.toString())
        intent.putExtra("long", lastLocation.longitude.toString())
        val address = getAddress(lastLocation.latitude, lastLocation.longitude)
        intent.putExtra("morada", address)
        startActivity(intent)
    }

}