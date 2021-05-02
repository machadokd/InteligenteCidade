package com.example.inteligentecidade

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.inteligentecidade.api.EndPoints
import com.example.inteligentecidade.api.OutputPostUser
import com.example.inteligentecidade.api.ServiceBuilder
import com.example.inteligentecidade.api.User
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    fun abrirNotas(view: View) {
        val intent = Intent(this, Notas::class.java).apply {
        }
        startActivity(intent)
    }

    fun login(view: View){
        val username: String = findViewById<EditText>(R.id.usernameLogin).text.toString()
        val password: String = findViewById<EditText>(R.id.passwordLogin).text.toString()

        val request = ServiceBuilder.buildService(EndPoints::class.java)
        var user = User(username,password)
        val call = request.login(user)

        call.enqueue(object : Callback<OutputPostUser>{

            override fun onResponse(call: Call<OutputPostUser>, response: Response<OutputPostUser>) {
                if (response.isSuccessful){
                    Toast.makeText(this@MainActivity, "Login Efetuado", Toast.LENGTH_SHORT).show()

                    val intent = Intent(this@MainActivity, MapActivity::class.java)

                    intent.putExtra("id_user", response.body()?.id_user)
                    intent.putExtra("username", response.body()?.username)

                    startActivity(intent)

                    finishAffinity()
                }
            }

            override fun onFailure(call:Call<OutputPostUser>, t: Throwable) {
                Toast.makeText(this@MainActivity, "ERRO", Toast.LENGTH_SHORT).show()
            }
        })
    }
}