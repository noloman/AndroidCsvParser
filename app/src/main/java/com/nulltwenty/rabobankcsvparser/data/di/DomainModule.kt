package com.nulltwenty.rabobankcsvparser.data.di

import com.nulltwenty.rabobankcsvparser.data.repository.CsvFileRepository
import com.nulltwenty.rabobankcsvparser.domain.usecase.FetchCsvFileUseCase
import com.nulltwenty.rabobankcsvparser.domain.usecase.ParseCsvFileUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(SingletonComponent::class)
object DomainModule {
    @Provides
    fun provideFetchCsvFileUseCase(
        csvFileRepository: CsvFileRepository,
        @DefaultCoroutineDispatcher coroutineDispatcher: CoroutineDispatcher
    ): FetchCsvFileUseCase = FetchCsvFileUseCase(csvFileRepository, coroutineDispatcher)

    @Provides
    fun provideParseCsvFileUseCase(
        @DefaultCoroutineDispatcher coroutineDispatcher: CoroutineDispatcher
    ): ParseCsvFileUseCase = ParseCsvFileUseCase(coroutineDispatcher)
}