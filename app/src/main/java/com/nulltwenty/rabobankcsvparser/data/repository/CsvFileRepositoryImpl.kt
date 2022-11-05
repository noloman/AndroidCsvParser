package com.nulltwenty.rabobankcsvparser.data.repository

import com.nulltwenty.rabobankcsvparser.data.api.service.CsvFileService
import com.nulltwenty.rabobankcsvparser.data.di.IoCoroutineDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import okhttp3.ResponseBody
import retrofit2.Response
import java.io.IOException
import java.io.InputStream
import javax.inject.Inject

class CsvFileRepositoryImpl @Inject constructor(
    @IoCoroutineDispatcher private val ioCoroutineDispatcher: CoroutineDispatcher,
    private val csvFileService: CsvFileService
) : CsvFileRepository {
    override suspend fun fetchCsvFile(): Flow<ResultOf<InputStream>> =
        withContext(ioCoroutineDispatcher) {
            flow {
                emit(safeApiCall { requestFile() })
            }
        }

    private suspend fun requestFile(): ResultOf<InputStream> {
        val response: Response<ResponseBody> = csvFileService.fetchCsvFile()
        if (response.isSuccessful) {
            val body = response.body()
            if (body != null) {
                return ResultOf.Success(body.byteStream())
            }
        }
        return ResultOf.Error(IOException("Error fetching the CSV file: ${response.code()} ${response.message()}"))
    }
}

sealed class ResultOf<out T : Any> {
    data class Success<out T : Any>(val data: T) : ResultOf<T>()
    data class Error(val exception: Exception) : ResultOf<Nothing>()

    override fun toString(): String {
        return when (this) {
            is Success<*> -> "Success[data=$data]"
            is Error -> "Error[exception=$exception]"
        }
    }
}

suspend fun <T : Any> safeApiCall(
    call: suspend () -> ResultOf<T>
): ResultOf<T> {
    return try {
        call()
    } catch (e: Exception) {
        // An exception was thrown when calling the API so we're converting this to an IOException
        ResultOf.Error(IOException(e.message, e))
    }
}