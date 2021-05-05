package com.example.inteligentecidade.api

import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface EndPoints {

    @POST("api/login")
    fun login(@Body user: User): Call<OutputPostUser>

    @Multipart
    @POST("api/report")
    fun report(
            @Part file: MultipartBody.Part,
            @Part("id_user") id_user: RequestBody,
            @Part("titulo") titulo: RequestBody,
            @Part("descricao") descricao: RequestBody,
            @Part("latitude") latitude: RequestBody,
            @Part("longitude") longitude: RequestBody,
    ) : Call<OutputPostReport>
}