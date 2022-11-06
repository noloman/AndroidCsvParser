package com.nulltwenty.rabobankcsvparser.domain.usecase

import com.nulltwenty.rabobankcsvparser.data.api.model.CsvFileModel
import com.nulltwenty.rabobankcsvparser.data.di.DefaultCoroutineDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.withContext
import java.io.InputStream
import javax.inject.Inject

const val defaultDatePattern = "yyyy-MM-dd'T'HH:mm:ss"

class ParseCsvFileUseCase @Inject constructor(
    @DefaultCoroutineDispatcher private val defaultCoroutineDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(inputStream: InputStream): Flow<List<CsvFileModel>> =
        withContext(defaultCoroutineDispatcher) { readCsv(inputStream) }

    @Throws(Exception::class)
    private fun readCsv(inputStream: InputStream): Flow<List<CsvFileModel>> {
        val reader = inputStream.bufferedReader()
        val headerLine: String? = reader.readLine()
        val headerStringList: List<String>? = headerLine?.split(',')
        checkHeaderCorrectness(headerLine, headerStringList)
        return flow {
            val list = reader.lineSequence().filter { it.isNotBlank() }.map {
                val (firstName, surname, issueCount, birthdate, avatarUrl) = it.split(
                    ',', ignoreCase = false
                )
                CsvFileModel(
                    firstName.removeSurrounding("\""),
                    surname.removeSurrounding("\""),
                    issueCount.toInt(),
                    birthdate.removeSurrounding("\""),
                    avatarUrl.removeSurrounding("\"")
                )
            }.toList()
            emit(list)
        }
    }

    private fun checkHeaderCorrectness(headerLine: String?, headerStringList: List<String>?) {
        if (headerLine != """"First name","Sur name","Issue count","Date of birth","avatar"""") {
            throw Exception()
        }
        if (headerStringList?.size != 5) {
            throw Exception("Unexpected file header size")
        }
        if (headerStringList.first() != "\"First name\"") {
            throw Exception("Unexpected header found at first position")
        }
        if (headerStringList[1] != "\"Sur name\"") {
            throw Exception("Unexpected problem found with header found at second position")
        }
        if (headerStringList[2] != "\"Issue count\"") {
            throw Exception("Unexpected problem found with header found at third position")
        }
        if (headerStringList[3] != "\"Date of birth\"") {
            throw Exception("Unexpected problem found with header found at fourth position")
        }
        if (headerStringList[4] != "\"avatar\"") {
            throw Exception("Unexpected problem found with header found at fifth position")
        }
    }
}