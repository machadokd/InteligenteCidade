package com.example.inteligentecidade

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.widget.Button
import android.widget.EditText

class AddNota : AppCompatActivity() {

    private lateinit var tituloText: EditText
    private lateinit var descricaoText: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_add_nota)

        //actionbar
        val actionbar = supportActionBar
        //set actionbar title
        actionbar!!.title = "New Activity"
        //set back button
        actionbar.setDisplayHomeAsUpEnabled(true)
        actionbar.setDisplayHomeAsUpEnabled(true)

        tituloText = findViewById(R.id.tituloNovaNota)
        descricaoText = findViewById(R.id.descricaoNovaNota)

        val button = findViewById<Button>(R.id.guardarNovaNota)
        button.setOnClickListener {
            val replyIntent = Intent()

            if(TextUtils.isEmpty(tituloText.text) || TextUtils.isEmpty(descricaoText.text)){
                setResult(Activity.RESULT_CANCELED, replyIntent)
            }else{
                replyIntent.putExtra(EXTRA_REPLY_TITULO, tituloText.text.toString())
                replyIntent.putExtra(EXTRA_REPLY_DESCRICAO, descricaoText.text.toString())
                setResult(Activity.RESULT_OK, replyIntent)
            }
            finish()
        }
    }

    companion object{
        const val EXTRA_REPLY_TITULO = "com.example.android.titulo"
        const val EXTRA_REPLY_DESCRICAO = "com.example.android.descricao"
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}