package com.example.inteligentecidade

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.lifecycle.ViewModelProvider
import com.example.inteligentecidade.DAO.NotaDAO
import com.example.inteligentecidade.DB.NotaRepository
import com.example.inteligentecidade.Entities.Nota
import com.example.inteligentecidade.adapters.NotaAdapter
import com.example.inteligentecidade.viewModel.NotaViewModel

class EditNota : AppCompatActivity() {

    private lateinit var notaViewModel: NotaViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_edit_nota)

        //actionbar
        val actionbar = supportActionBar
        //set actionbar title
        actionbar!!.title = "New Activity"
        //set back button
        actionbar.setDisplayHomeAsUpEnabled(true)
        actionbar.setDisplayHomeAsUpEnabled(true)

        var titulo = intent.getStringExtra(NotaAdapter.NotaViewHolder.NOTA_TITULO_KEY)
        var descricao = intent.getStringExtra(NotaAdapter.NotaViewHolder.NOTA_DESCRICAO_KEY)
        var id = intent.getIntExtra(NotaAdapter.NotaViewHolder.NOTA_ID_KEY, 0)

        findViewById<EditText>(R.id.tituloEditarNota).setText(titulo)
        findViewById<EditText>(R.id.descricaoEditarNota).setText(descricao)

        val botao= findViewById<Button>(R.id.guardarEditarNota)

        botao.setOnClickListener {
            val ptitulo = findViewById<EditText>(R.id.tituloEditarNota)
            val pdescricao = findViewById<EditText>(R.id.descricaoEditarNota)

            if(TextUtils.isEmpty(ptitulo.text) || TextUtils.isEmpty(pdescricao.text)){
                Toast.makeText(
                    applicationContext,
                    R.string.empty_not_saved,
                    Toast.LENGTH_LONG).show()
                finish()
            }else{
                if (ptitulo!= null && pdescricao != null) {
                    notaViewModel = ViewModelProvider(this).get(NotaViewModel::class.java)
                    notaViewModel.updateNotaByNotasId(id, ptitulo.text.toString(), pdescricao.text.toString())
                    Toast.makeText(
                        applicationContext,
                        R.string.saved,
                        Toast.LENGTH_LONG).show()
                    finish()
                }
            }
        }
    }
    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        var id = 0
        id = intent.getIntExtra(NotaAdapter.NotaViewHolder.NOTA_ID_KEY, 0)
        notaViewModel = ViewModelProvider(this).get(NotaViewModel::class.java)
        // Handle item selection
        return when (item.itemId) {
            R.id.apagar -> {
                notaViewModel.deleteNotasById(id)
                finish()
                true
            }

            else -> super.onOptionsItemSelected(item)
        }

    }
    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}