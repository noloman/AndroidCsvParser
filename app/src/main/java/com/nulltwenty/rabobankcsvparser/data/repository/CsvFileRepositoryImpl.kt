package com.nulltwenty.rabobankcsvparser.data.repository

import com.nulltwenty.rabobankcsvparser.data.api.service.CsvFileService
import com.nulltwenty.rabobankcsvparser.data.di.IoCoroutineDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.InputStream
import javax.inject.Inject

class CsvFileRepositoryImpl @Inject constructor(
    @IoCoroutineDispatcher private val ioCoroutineDispatcher: CoroutineDispatcher,
    private val csvFileService: CsvFileService
) : CsvFileRepository {
    override suspend fun fetchCsvFile(): Flow<InputStream?> = withContext(ioCoroutineDispatcher) {
        flow {
            val result: Response<ResponseBody> = csvFileService.fetchCsvFile()
            when {
                result.isSuccessful -> result.body()?.let {
                    emit(it.byteStream())
                }
                else -> emit(result.errorBody()?.byteStream())
            }
        }
    }
}