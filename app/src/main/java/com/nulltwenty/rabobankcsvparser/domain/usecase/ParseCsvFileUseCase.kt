package com.nulltwenty.rabobankcsvparser.domain.usecase

import com.nulltwenty.rabobankcsvparser.data.api.model.CsvFileModel
import com.nulltwenty.rabobankcsvparser.data.di.DefaultCoroutineDispatcher
import com.nulltwenty.rabobankcsvparser.domain.CsvParser
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.io.InputStream
import javax.inject.Inject

const val defaultDatePattern = "yyyy-MM-dd'T'HH:mm:ss"

class ParseCsvFileUseCase @Inject constructor(
    private val csvParser: CsvParser,
    @DefaultCoroutineDispatcher private val defaultCoroutineDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke(inputStream: InputStream): Flow<List<CsvFileModel>> =
        withContext(defaultCoroutineDispatcher) { csvParser.parse(inputStream) }
}