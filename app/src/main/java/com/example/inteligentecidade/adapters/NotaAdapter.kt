package com.example.inteligentecidade.adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat.startActivity
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.RecyclerView
import com.example.inteligentecidade.EditNota
import com.example.inteligentecidade.Entities.Nota
import com.example.inteligentecidade.R
import com.example.inteligentecidade.viewModel.NotaViewModel
import kotlin.coroutines.coroutineContext

class NotaAdapter internal constructor(
    var context: Context,
) : RecyclerView.Adapter<NotaAdapter.NotaViewHolder>() {

    private val inflater: LayoutInflater = LayoutInflater.from(context)
    private var notes = emptyList<Nota>() // Cached copy of cities


    class NotaViewHolder(itemView: View, var nota :Nota? = null) : RecyclerView.ViewHolder(itemView) {
        val titulo: TextView = itemView.findViewById(R.id.titulo)
        val descricao: TextView = itemView.findViewById(R.id.descricao)
        val edit: ImageView = itemView.findViewById(R.id.edit_note)

        companion object {
            val NOTA_TITULO_KEY = "NOTA_TITULO"
            val NOTA_DESCRICAO_KEY = "NOTA_DESCRICAO"
            val NOTA_ID_KEY = "NOTA_ID"
        }

        init{
            edit.setOnClickListener {
                val intent = Intent(itemView.context, EditNota::class.java)
                intent.putExtra(NOTA_TITULO_KEY, nota?.titulo)
                intent.putExtra(NOTA_DESCRICAO_KEY, nota?.descricao)
                intent.putExtra(NOTA_ID_KEY, nota?.id)
                itemView.context.startActivity(intent)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NotaViewHolder {
        val itemView = inflater.inflate(R.layout.linha_nota, parent, false)
        return NotaViewHolder(itemView)
    }

    override fun onBindViewHolder(holder: NotaViewHolder, position: Int) {
        val current = notes[position]
        holder.titulo.text = current.titulo
        holder.descricao.text = current.descricao
        holder?.nota = current
    }

    internal fun setNotas(notas: List<Nota>) {
        this.notes = notas
        notifyDataSetChanged()
    }

    override fun getItemCount() = notes.size

}