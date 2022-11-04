package com.nulltwenty.rabobankcsvparser.data.repository

import kotlinx.coroutines.flow.Flow
import java.io.InputStream

interface CsvFileRepository {
    suspend fun fetchCsvFile(): Flow<InputStream?>
}