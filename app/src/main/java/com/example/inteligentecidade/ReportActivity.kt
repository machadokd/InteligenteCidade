package com.example.inteligentecidade

import android.Manifest.permission.CAMERA
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import com.google.android.gms.maps.model.LatLng
import it.sauronsoftware.ftp4j.FTPClient
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*


class ReportActivity : AppCompatActivity() {

    lateinit var imageView: ImageView
    lateinit var captureButton: ImageView

    lateinit var username : String
    lateinit var id_user : String

    var lat : String? = null
    var long: String? = null
    var morada: String? = null

    var image_uri: Uri? = null


    val REQUEST_IMAGE_CAPTURE = 1

    private val PERMISSION_REQUEST_CODE: Int = 101


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        lat = intent.getStringExtra("lat")
        long = intent.getStringExtra("long")
        morada = intent.getStringExtra("morada")
        username = intent.getStringExtra("username2").toString()
        id_user = intent.getStringExtra("id_user2").toString()

        var textlat = findViewById<TextView>(R.id.textViewLat)
        var textlong = findViewById<TextView>(R.id.textViewLong)
        var moradaEditText = findViewById<EditText>(R.id.morada_report)

        textlat.setText(lat)
        textlong.setText(long)
        moradaEditText.setText(morada)

        imageView = findViewById(R.id.imageViewContent)
        captureButton = findViewById(R.id.tirarfoto2)
        captureButton.setOnClickListener(View.OnClickListener {
            if (checkPersmission()) takePicture() else requestPermission()
        })
    }


    @SuppressLint("MissingSuperCall")
    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        when (requestCode) {
            PERMISSION_REQUEST_CODE -> {
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                        && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    takePicture()
                } else {
                    Toast.makeText(this, "Permissão Bloqueada", Toast.LENGTH_SHORT).show()
                }
                return
            }
            else -> {
                Toast.makeText(this, "Erro", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun takePicture() {
        val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val values = ContentValues()
        values.put(MediaStore.Images.Media.TITLE, "New Picture")
        image_uri = contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        intent.putExtra(MediaStore.EXTRA_OUTPUT, image_uri)
        startActivityForResult(intent, REQUEST_IMAGE_CAPTURE)
    }

    @SuppressLint("MissingSuperCall")
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            imageView.setImageURI(image_uri)
        }
    }

    private fun checkPersmission(): Boolean {
        return (ContextCompat.checkSelfPermission(this, android.Manifest.permission.CAMERA) ==
                PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
    }

    private fun requestPermission() {
        ActivityCompat.requestPermissions(this, arrayOf(READ_EXTERNAL_STORAGE, CAMERA), PERMISSION_REQUEST_CODE)
    }

}