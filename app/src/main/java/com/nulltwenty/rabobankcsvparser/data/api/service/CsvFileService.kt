package com.nulltwenty.rabobankcsvparser.data.api.service

import okhttp3.ResponseBody
import retrofit2.Response
import retrofit2.http.GET

interface CsvFileService {
    @GET("main/issues.csv")
    @Throws(Exception::class)
    suspend fun fetchCsvFile(): Response<ResponseBody>

    companion object {
        const val BASE_URL = "https://raw.githubusercontent.com/RabobankDev/AssignmentCSV/"
    }
}