package com.nulltwenty.rabobankcsvparser.data.repository

import kotlinx.coroutines.flow.Flow
import okhttp3.ResponseBody

interface CsvFileRepository {
    suspend fun fetchCsvFile(): Flow<ResultOf<ResponseBody>>
}