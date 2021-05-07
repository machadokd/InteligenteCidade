package com.example.inteligentecidade.api

import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.Response
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface EndPoints {

    @POST("api/login")
    fun login(@Body user: User): Call<OutputPostUser>

    @Multipart
    @POST("api/insere_report")
    fun report(
            @Part file: MultipartBody.Part,
            @Part("id_user") id_user: RequestBody,
            @Part("titulo") titulo: RequestBody,
            @Part("descricao") descricao: RequestBody,
            @Part("latitude") latitude: RequestBody,
            @Part("longitude") longitude: RequestBody,
            @Part("tipo") tipo: RequestBody
    ) : Call<OutputPostReport>

    @GET("api/reports")
    fun getReports(): Call<List<Report>>

    @GET("api/getReport/{id}")
    fun getReportById(@Path("id") id:Int?): Call<Report>

    @DELETE("api/deleteReport/{id}")
    fun deleteReportById(@Path("id") id:Int?): Call<OutputDeleteReport>

    @Multipart
    @POST("api/updateReport")
    fun updateReportById(
        @Part file: MultipartBody.Part,
        @Part("id") id: RequestBody,
        @Part("titulo") titulo: RequestBody,
        @Part("descricao") descricao: RequestBody,
        @Part("tipo") tipo: RequestBody
    ) : Call<OutputUpdateReport>

}