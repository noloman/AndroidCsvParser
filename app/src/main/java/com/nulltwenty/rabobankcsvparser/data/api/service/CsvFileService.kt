package com.nulltwenty.rabobankcsvparser.data.api.service

import com.nulltwenty.rabobankcsvparser.data.api.model.CsvFileModel
import retrofit2.Response
import retrofit2.http.GET

interface CsvFileService {
    @GET("main/issues.csv")
    suspend fun fetchCsvFile(): Response<CsvFileModel>

    companion object {
        const val BASE_URL = "https://raw.githubusercontent.com/RabobankDev/AssignmentCSV/"
    }
}