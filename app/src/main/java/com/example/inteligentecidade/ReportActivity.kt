package com.example.inteligentecidade

import android.Manifest.permission.CAMERA
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.annotation.SuppressLint
import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.net.Uri
import android.os.Bundle
import android.os.Environment
import android.os.FileUtils
import android.provider.MediaStore
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toBitmap
import com.example.inteligentecidade.api.*
import okhttp3.MediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.*
import kotlin.math.log


class ReportActivity : AppCompatActivity() {

    lateinit var imageView: ImageView
    lateinit var captureButton: ImageView

    var username : String? = null
    var id_user : String? = null

    lateinit var tipo : String


    var lat : String? = null
    var long: String? = null
    var morada: String? = null

    var image_uri: Uri? = null


    val REQUEST_IMAGE_CAPTURE = 1

    private val PERMISSION_REQUEST_CODE: Int = 101


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report)

        val sharedPref : SharedPreferences = getSharedPreferences(getString(R.string.preference_key), Context.MODE_PRIVATE)

        id_user = sharedPref.getString(getString(R.string.id_user), "default")

        Log.d("SHARED_IDUSER", id_user.toString())



        lat = intent.getStringExtra("lat")
        long = intent.getStringExtra("long")
        morada = intent.getStringExtra("morada")

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

        val tipos = resources.getStringArray(R.array.tipo)
        val spinner: Spinner = findViewById(R.id.spinner2)
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
            }

            override fun onNothingSelected(parent: AdapterView<*>) {
                // write code to perform some action
            }
        }

        val botaoReport = findViewById<Button>(R.id.reportar2)
        botaoReport.setOnClickListener {
            report()
        }
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

    /*private fun report() {
        val imgBitmap: Bitmap = findViewById<ImageView>(R.id.imageViewContent).drawable.toBitmap()
        val imageFile: File = convertBitmapToFile("file", imgBitmap)
        Log.d("IMAGEM", imageFile.toString());
        //val imgFileRequest: RequestBody = RequestBody.create(MediaType.parse("image/*"), imageFile)
        //val foto: MultipartBody.Part = MultipartBody.Part.createFormData("foto", imageFile.name, imgFileRequest)
    }*/

     */

    private fun convertBitmapToFile(fileName: String, bitmap: Bitmap): File {
            //create a file to write bitmap data
            val file = File(this@ReportActivity.cacheDir, fileName)
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

    private fun report(){
        var titulo = findViewById<EditText>(R.id.tituloReport)
        var descricao = findViewById<EditText>(R.id.descricaoReport)
        if(!titulo.text.isEmpty()&&!descricao.text.isEmpty()){
            val imgBitmap: Bitmap = findViewById<ImageView>(R.id.imageViewContent).drawable.toBitmap()
            val imageFile: File = convertBitmapToFile("file", imgBitmap)

            val imgFileRequest: RequestBody = RequestBody.create(MediaType.parse("image/*"), imageFile)
            val foto: MultipartBody.Part = MultipartBody.Part.createFormData("file", imageFile.name, imgFileRequest)

            val iduser: RequestBody = RequestBody.create(MediaType.parse("text/plain"), id_user)
            val titulo: RequestBody = RequestBody.create(MediaType.parse("text/plain"), titulo.text.toString())
            val descricao: RequestBody = RequestBody.create(MediaType.parse("text/plain"), descricao.text.toString())
            val latitude: RequestBody = RequestBody.create(MediaType.parse("text/plain"), lat)
            val longitude: RequestBody = RequestBody.create(MediaType.parse("text/plain"), long)
            val tipo: RequestBody = RequestBody.create(MediaType.parse("text/plain"), tipo)


            val request = ServiceBuilder.buildService(EndPoints::class.java)

            val call = request.report(foto, iduser, titulo, descricao, latitude, longitude, tipo)

            call.enqueue(object : Callback<OutputPostReport> {

                override fun onResponse(call: Call<OutputPostReport>, response: Response<OutputPostReport>) {
                    if (response.isSuccessful){
                        Toast.makeText(this@ReportActivity, "Report Efetuado com sucesso.", Toast.LENGTH_SHORT).show()
                        finish()
                    }
                }

                override fun onFailure(call: Call<OutputPostReport>, t: Throwable) {
                    Toast.makeText(this@ReportActivity, "Erro", Toast.LENGTH_SHORT).show()
                    Log.d("MACHAS", t.toString())
                }
            })
        }else{
            Toast.makeText(this@ReportActivity, "Não é possível ter campos vazios.", Toast.LENGTH_SHORT).show()
        }
    }
}