package com.example.inteligentecidade

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.widget.Toast
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.inteligentecidade.Entities.Nota
import com.example.inteligentecidade.adapters.NotaAdapter
import com.example.inteligentecidade.viewModel.NotaViewModel
import com.google.android.material.floatingactionbutton.FloatingActionButton

class Notas : AppCompatActivity() {

    private lateinit var notaViewModel: NotaViewModel
    private val newWordActivityRequestCode = 1

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notas)

        //actionbar
        val actionbar = supportActionBar
        //set actionbar title
        actionbar!!.title = "New Activity"
        //set back button
        actionbar.setDisplayHomeAsUpEnabled(true)
        actionbar.setDisplayHomeAsUpEnabled(true)

        val recyclerView = findViewById<RecyclerView>(R.id.notas_recycler)
        val adapter = NotaAdapter(this)

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        // view model
        notaViewModel = ViewModelProvider(this).get(NotaViewModel::class.java)
        notaViewModel.allNotas.observe(this, Observer { notes->
            // Update the cached copy of the words in the adapter.
            notes?.let { adapter.setNotas(it) }
        })


        val fab = findViewById<FloatingActionButton>(R.id.fab)
        fab.setOnClickListener {
            val intent = Intent(this@Notas, AddNota::class.java)
            startActivityForResult(intent, newWordActivityRequestCode)
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == newWordActivityRequestCode && resultCode == Activity.RESULT_OK) {
            val ptitulo = data?.getStringExtra(AddNota.EXTRA_REPLY_TITULO)
            val pdescricao = data?.getStringExtra(AddNota.EXTRA_REPLY_DESCRICAO)

            if (ptitulo!= null && pdescricao != null) {
                val note = Nota(titulo = ptitulo, descricao = pdescricao)
                notaViewModel.insert(note)
            }

        } else {
            Toast.makeText(
                applicationContext,
                R.string.empty_not_saved,
                Toast.LENGTH_LONG).show()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        val inflater: MenuInflater = menuInflater
        inflater.inflate(R.menu.menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        notaViewModel = ViewModelProvider(this).get(NotaViewModel::class.java)
        // Handle item selection
        return when (item.itemId) {
            R.id.apagar -> {
                notaViewModel.deleteAll()
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