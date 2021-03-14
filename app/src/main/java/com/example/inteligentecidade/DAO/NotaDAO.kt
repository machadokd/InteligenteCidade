package com.example.inteligentecidade.DAO

import androidx.lifecycle.LiveData
import androidx.room.*
import com.example.inteligentecidade.Entities.Nota

@Dao
interface NotaDAO {
    @Query("SELECT * from tabela_notas")
    fun getAllNotas(): LiveData<List<Nota>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(nota: Nota)



    @Query("DELETE FROM tabela_notas")
    suspend fun deleteAll()

    @Query("DELETE FROM tabela_notas where id == :id")
    suspend fun deleteByNotasId(id: Int)

    @Query("UPDATE tabela_notas SET titulo=:titulo, descricao=:descricao WHERE id=:id")
    suspend fun updateNotaByNotasId(id: Int, titulo: String, descricao: String)
}