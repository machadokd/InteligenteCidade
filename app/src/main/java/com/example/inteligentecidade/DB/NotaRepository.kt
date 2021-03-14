package com.example.inteligentecidade.DB

import androidx.lifecycle.LiveData
import com.example.inteligentecidade.DAO.NotaDAO
import com.example.inteligentecidade.Entities.Nota

class NotaRepository(private val notaDao: NotaDAO) {

    // Room executes all queries on a separate thread.
    // Observed LiveData will notify the observer when the data has changed.
    val allNotes: LiveData<List<Nota>> = notaDao.getAllNotas()


    suspend fun insert(nota: Nota) {
        notaDao.insert(nota)
    }

    suspend fun deleteAll(){
        notaDao.deleteAll()
    }

    suspend fun deleteNotasById(id: Int){
        notaDao.deleteByNotasId(id)
    }

    suspend fun updateNotaByNotasId(id: Int, titulo: String, descricao: String){
        notaDao.updateNotaByNotasId(id, titulo, descricao)
    }
}