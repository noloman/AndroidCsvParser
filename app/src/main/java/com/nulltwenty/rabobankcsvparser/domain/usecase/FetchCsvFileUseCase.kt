package com.nulltwenty.rabobankcsvparser.domain.usecase

import com.nulltwenty.rabobankcsvparser.data.di.DefaultCoroutineDispatcher
import com.nulltwenty.rabobankcsvparser.data.repository.CsvFileRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import javax.inject.Inject

class FetchCsvFileUseCase @Inject constructor(
    private val repository: CsvFileRepository,
    @DefaultCoroutineDispatcher private val defaultCoroutineDispatcher: CoroutineDispatcher
) {
    suspend operator fun invoke() = withContext(defaultCoroutineDispatcher) {
        repository.fetchCsvFile()
    }
}