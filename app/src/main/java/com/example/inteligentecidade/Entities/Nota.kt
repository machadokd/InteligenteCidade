package com.example.inteligentecidade.Entities

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tabela_notas")

class Nota(
    @PrimaryKey(autoGenerate = true) val id: Int? = null,
    @ColumnInfo(name = "titulo") val titulo: String,
    @ColumnInfo(name = "descricao") val descricao: String
)
