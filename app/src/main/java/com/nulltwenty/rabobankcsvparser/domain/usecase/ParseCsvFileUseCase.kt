package com.nulltwenty.rabobankcsvparser.domain.usecase

import com.nulltwenty.rabobankcsvparser.data.api.model.CsvFileModel
import com.nulltwenty.rabobankcsvparser.data.di.DefaultCoroutineDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.InputStream
import javax.inject.Inject

const val defaultDatePattern = "yyyy-MM-dd'T'HH:mm:ss"

class ParseCsvFileUseCase @Inject constructor(
    @DefaultCoroutineDispatcher private val defaultCoroutineDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(inputStream: InputStream?) =
        withContext(defaultCoroutineDispatcher) { readCsv(inputStream) }

    private fun readCsv(inputStream: InputStream?): List<CsvFileModel>? {
        val reader = inputStream?.bufferedReader()
        reader?.readLine()
        return reader?.lineSequence()?.filter { it.isNotBlank() }?.map {
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
        }?.toList()
    }
}