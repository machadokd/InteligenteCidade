package com.example.inteligentecidade.api

data class Report(
        val id_report: String,
        val id_user: String,
        val titulo: String,
        val descrição: String,
        val data: String,
        val latitude: String,
        val longitude: String,
        val fotografia: String,
        val tipo: String
)
