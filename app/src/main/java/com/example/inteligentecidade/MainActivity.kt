package com.example.inteligentecidade

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log

import android.view.View
import android.widget.Button
import android.widget.CheckBox
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

        val sharedPref : SharedPreferences = getSharedPreferences(getString(R.string.preference_key), Context.MODE_PRIVATE)
        val check = sharedPref.getBoolean(getString(R.string.check_login), false)

        if(check){
            val intent = Intent(this@MainActivity, MapActivity::class.java)
            startActivity(intent)
            finishAffinity()
        }
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
                    val checkBox = findViewById<CheckBox>(R.id.checkBox)
                    val sharedPref : SharedPreferences = getSharedPreferences(getString(R.string.preference_key), Context.MODE_PRIVATE)
                    with(sharedPref.edit()){
                        putBoolean(getString(R.string.check_login), checkBox.isChecked)
                        putString(getString(R.string.id_user), response.body()?.id_user.toString())
                        commit()
                    }
                    val intent = Intent(this@MainActivity, MapActivity::class.java)
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