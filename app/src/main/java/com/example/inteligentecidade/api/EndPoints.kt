package com.example.inteligentecidade.api

import retrofit2.Call
import retrofit2.http.*

interface EndPoints {

    @POST("api/login")
    fun login(@Body user: User): Call<OutputPostUser>


}