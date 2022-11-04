package com.nulltwenty.rabobankcsvparser.data.repository

import com.nulltwenty.rabobankcsvparser.data.api.model.CsvFileModel
import kotlinx.coroutines.flow.Flow

interface CsvFileRepository {
    suspend fun fetchCsvFile(): Flow<CsvFileModel>
}