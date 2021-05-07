package com.example.inteligentecidade

import android.location.Geocoder
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.Spinner
import android.widget.Toast
import com.example.inteligentecidade.api.EndPoints
import com.example.inteligentecidade.api.Report
import com.example.inteligentecidade.api.ServiceBuilder
import com.squareup.picasso.Picasso
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

private var id : String? = null

class ReportOutrosActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_outros)

        id = intent.getStringExtra("id_report")
        getReport(id?.toInt())
    }

    private fun getReport(id_report : Int?){
        val request = ServiceBuilder.buildService(EndPoints::class.java)
        val call = request.getReportById(id_report)

        var report : Report? = null

        call.enqueue(object : Callback<Report> {

            override fun onResponse(call: Call<Report>, response: Response<Report>) {
                if (response.isSuccessful){
                    report=response.body()
                    findViewById<EditText>(R.id.tituloReportOutrosEdit).setText(report?.titulo)
                    findViewById<EditText>(R.id.descricaoReportOutrosEdit).setText(report?.descrição)
                    val imageView : ImageView = findViewById(R.id.imageViewReportOutros)
                    Log.d("MACHAS", report?.fotografia.toString())
                    val url = "https://cidadeinteligentecm.000webhostapp.com/meuslim/fotos_reports/"+ report?.fotografia
                    Picasso.get().load(url).into(imageView)
                    findViewById<EditText>(R.id.tipoReportOutrosEdit).setText(report?.tipo)
                    val address = getAddress(report!!.latitude.toDouble(), report!!.longitude.toDouble())
                    findViewById<EditText>(R.id.moradaReportOutrosEdit).setText(address)
                }
            }

            override fun onFailure(call: Call<Report>, t: Throwable) {
                Toast.makeText(this@ReportOutrosActivity, R.string.loginerro, Toast.LENGTH_SHORT).show()
                Log.d("MACHAS", t.toString())
            }
        })
    }

    private fun getAddress(lat :Double, long: Double):String?{
        val geocoder = Geocoder(this)
        val list = geocoder.getFromLocation(lat, long, 1)
        return list[0].getAddressLine(0)
    }
}