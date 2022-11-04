package com.nulltwenty.rabobankcsvparser.domain.usecase

import com.nulltwenty.rabobankcsvparser.data.api.model.CsvFileModel
import com.nulltwenty.rabobankcsvparser.data.di.DefaultCoroutineDispatcher
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

class ParseCsvFileUseCase @Inject constructor(
    @DefaultCoroutineDispatcher private val defaultCoroutineDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(inputStream: InputStream?) =
        withContext(defaultCoroutineDispatcher) { readCsv(inputStream) }

    private fun readCsv(inputStream: InputStream?): List<CsvFileModel>? {
        val reader = inputStream?.bufferedReader()
        reader?.readLine()
        val dateFormat =
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()) // 1978-01-02T00:00:00
        return reader?.lineSequence()?.filter { it.isNotBlank() }?.map {
            val (firstName, surname, issueCount, birthdate, avatarUrl) = it.split(
                ',', ignoreCase = false
            )
            CsvFileModel(
                firstName.removeSurrounding("\""),
                surname.removeSurrounding("\""),
                issueCount.toInt(),
                dateFormat.parse(birthdate.removeSurrounding("\"")) ?: Date(),
                avatarUrl.removeSurrounding("\"")
            )
        }?.toList()
    }
}