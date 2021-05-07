package com.example.inteligentecidade

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.location.Geocoder
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import androidx.lifecycle.ViewModelProvider
import com.example.inteligentecidade.api.*
import com.example.inteligentecidade.viewModel.NotaViewModel
import com.squareup.picasso.Picasso
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*

lateinit var tipo : String
var id_report : String? = null
@SuppressLint("StaticFieldLeak")
lateinit var captureButton: ImageView
@SuppressLint("StaticFieldLeak")
lateinit var imageView: ImageView
var image_uri: Uri? = null
private var id : String? = null


val REQUEST_IMAGE_CAPTURE = 1
private val PERMISSION_REQUEST_CODE: Int = 101


class MeuReport : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_meu_report)

        val tipos = resources.getStringArray(R.array.tipo)
        val spinner: Spinner = findViewById(R.id.spinnerMeuReport)
        // Create an ArrayAdapter using the string array and a default spinner layout
        ArrayAdapter.createFromResource(
            this,
            R.array.tipo,
            android.R.layout.simple_spinner_item
        ).also { adapter ->
            // Specify the layout to use when the list of choices appears
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            // Apply the adapter to the spinner
            spinner.adapter = adapter
        }

        spinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(parent: AdapterView<*>,
                                        view: View, position: Int, id: Long) {
                tipo = tipos[position]
                Log.d("MACHAS", tipo)
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        id_report = intent.getStringExtra("id_report")
        getReport(id_report?.toInt())

        imageView = findViewById(R.id.imageViewMeuReport)
        captureButton = findViewById(R.id.tirarfotoMeuReport)
        captureButton.setOnClickListener(View.OnClickListener {
            if (checkPersmission()) takePicture() else requestPermission()
        })

    }

    private fun getAddress(lat :Double, long: Double):String?{
        val geocoder = Geocoder(this)
        val list = geocoder.getFromLocation(lat, long, 1)
        return list[0].getAddressLine(0)
    }

    private fun getReport(id_report : Int?){
        val request = ServiceBuilder.buildService(EndPoints::class.java)
        val call = request.getReportById(id_report)

        var report : Report? = null

        call.enqueue(object : Callback<Report> {

            override fun onResponse(call: Call<Report>, response: Response<Report>) {
                if (response.isSuccessful){
                    report=response.body()
                    findViewById<EditText>(R.id.tituloMeuReportEdit).setText(report?.titulo)
                    findViewById<EditText>(R.id.descricaoMeuReportEdit).setText(report?.descrição)
                    val imageView : ImageView = findViewById(R.id.imageViewMeuReport)
                    Log.d("MACHAS", report?.fotografia.toString())
                    val url = "https://cidadeinteligentecm.000webhostapp.com/meuslim/fotos_reports/"+ report?.fotografia
                    Picasso.get().load(url).into(imageView)
                    var spinner = findViewById<Spinner>(R.id.spinnerMeuReport)
                    val tipos = resources.getStringArray(R.array.tipo)
                    id = report?.id_report
                    var i = 0
                    var aux = 0
                    tipos.forEach {
                        if(it == report?.tipo){
                            aux = i
                        }
                        i++
                    }
                    spinner.setSelection(aux)
                    val address = getAddress(report!!.latitude.toDouble(), report!!.longitude.toDouble())
                    findViewById<EditText>(R.id.moradaMeuReportEdit).setText(address)
                }
            }

            override fun onFailure(call: Call<Report>, t: Throwable) {
                Toast.makeText(this@MeuReport, R.string.loginerro, Toast.LENGTH_SHORT).show()
                Log.d("MACHAS", t.toString())
            }
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
                Toast.makeText(this, R.string.loginerro, Toast.LENGTH_SHORT).show()
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
        ActivityCompat.requestPermissions(this, arrayOf(
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA
        ), PERMISSION_REQUEST_CODE)
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu_editreport, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        // Handle item selection
        return when (item.itemId) {
            R.id.apagarReport -> {
                val request = ServiceBuilder.buildService(EndPoints::class.java)
                val call = request.deleteReportById(id?.toInt())

                call.enqueue(object : Callback<OutputDeleteReport> {

                    override fun onResponse(call: Call<OutputDeleteReport>, response: Response<OutputDeleteReport>) {
                        if (response.isSuccessful){
                            Toast.makeText(this@MeuReport, R.string.deleteReport, Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    }

                    override fun onFailure(call: Call<OutputDeleteReport>, t: Throwable) {
                        Toast.makeText(this@MeuReport, R.string.loginerro, Toast.LENGTH_SHORT).show()
                        Log.d("MACHAS", t.toString())
                    }
                })
                true
            }

            else -> super.onOptionsItemSelected(item)
        }

    }

    private fun convertBitmapToFile(fileName: String, bitmap: Bitmap): File {
        //create a file to write bitmap data
        val file = File(this@MeuReport.cacheDir, fileName)
        file.createNewFile()

        //Convert bitmap to byte array
        val bos = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 50, bos)
        val bitMapData = bos.toByteArray()

        //write the bytes in file
        var fos: FileOutputStream? = null
        try {
            fos = FileOutputStream(file)
        } catch (e: FileNotFoundException) {
            e.printStackTrace()
        }
        try {
            fos?.write(bitMapData)
            fos?.flush()
            fos?.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return file
    }

    fun update(view: View){
        var tit = findViewById<EditText>(R.id.tituloMeuReportEdit)
        var desc = findViewById<EditText>(R.id.descricaoMeuReportEdit)

        val imgBitmap: Bitmap = findViewById<ImageView>(R.id.imageViewMeuReport).drawable.toBitmap()
        val imageFile: File = convertBitmapToFile("file", imgBitmap)

        val imgFileRequest: RequestBody = RequestBody.create(MediaType.parse("image/*"), imageFile)
        val foto: MultipartBody.Part = MultipartBody.Part.createFormData("file", imageFile.name, imgFileRequest)

        val id: RequestBody = RequestBody.create(MediaType.parse("text/plain"), id)
        val titulo: RequestBody = RequestBody.create(MediaType.parse("text/plain"), tit.text.toString())
        val descricao: RequestBody = RequestBody.create(MediaType.parse("text/plain"), desc.text.toString())
        val tipo: RequestBody = RequestBody.create(MediaType.parse("text/plain"), tipo)


        val request = ServiceBuilder.buildService(EndPoints::class.java)

        val call = request.updateReportById(foto, id, titulo, descricao, tipo)

        call.enqueue(object : Callback<OutputUpdateReport> {

            override fun onResponse(call: Call<OutputUpdateReport>, response: Response<OutputUpdateReport>) {
                if (response.isSuccessful){
                    Toast.makeText(this@MeuReport, R.string.updateReport, Toast.LENGTH_SHORT).show()
                    finish()
                }
            }

            override fun onFailure(call: Call<OutputUpdateReport>, t: Throwable) {
                Toast.makeText(this@MeuReport, R.string.loginerro, Toast.LENGTH_SHORT).show()
                Log.d("MACHAS", t.toString())
            }
        })
    }
}