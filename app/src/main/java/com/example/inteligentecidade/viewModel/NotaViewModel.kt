package com.example.inteligentecidade.viewModel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.inteligentecidade.DB.NotaDB
import com.example.inteligentecidade.DB.NotaRepository
import com.example.inteligentecidade.Entities.Nota
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class NotaViewModel(application: Application) : AndroidViewModel(application) {

    private val repository: NotaRepository

    // Using LiveData and caching what getAlphabetizedWords returns has several benefits:
    // - We can put an observer on the data (instead of polling for changes) and only update the
    //   the UI when the data actually changes.
    // - Repository is completely separated from the UI through the ViewModel.
    val allNotas: LiveData<List<Nota>>

    init {
        val notasDao = NotaDB.getDatabase(application, viewModelScope).NotaDao()
        repository = NotaRepository(notasDao)
        allNotas = repository.allNotes
    }

    /**
     * Launching a new coroutine to insert the data in a non-blocking way
     */
    fun insert(nota: Nota) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(nota)
    }


    fun deleteAll() = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteAll()
    }


    fun deleteNotasById(id: Int) = viewModelScope.launch(Dispatchers.IO) {
        repository.deleteNotasById(id)
    }

    fun updateNotaByNotasId(id: Int, titulo: String, descricao: String) = viewModelScope.launch {
        repository.updateNotaByNotasId(id, titulo, descricao)
    }
}
