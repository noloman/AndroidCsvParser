package com.nulltwenty.rabobankcsvparser.data.repository

import com.nulltwenty.rabobankcsvparser.data.api.model.CsvFileModel
import com.nulltwenty.rabobankcsvparser.data.api.service.CsvFileService
import com.nulltwenty.rabobankcsvparser.data.di.IoCoroutineDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import javax.inject.Inject

class CsvFileRepositoryImpl @Inject constructor(
    @IoCoroutineDispatcher private val ioCoroutineDispatcher: CoroutineDispatcher,
    private val csvFileService: CsvFileService
) : CsvFileRepository {
    override suspend fun fetchCsvFile(): Flow<CsvFileModel> = withContext(ioCoroutineDispatcher) {
        flow {
            val result = csvFileService.fetchCsvFile()
            when {
                result.isSuccessful -> result.body()
                else -> result.errorBody()
            }
        }
    }
}