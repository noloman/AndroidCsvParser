package com.nulltwenty.rabobankcsvparser.data.di

import android.content.Context
import com.nulltwenty.rabobankcsvparser.data.repository.CsvFileRepository
import com.nulltwenty.rabobankcsvparser.domain.CsvParser
import com.nulltwenty.rabobankcsvparser.domain.usecase.FetchCsvFileUseCase
import com.nulltwenty.rabobankcsvparser.domain.usecase.ParseCsvFileUseCase
import com.nulltwenty.rabobankcsvparser.domain.usecase.SaveFileUseCase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
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
        csvParser: CsvParser, @DefaultCoroutineDispatcher coroutineDispatcher: CoroutineDispatcher
    ): ParseCsvFileUseCase = ParseCsvFileUseCase(csvParser, coroutineDispatcher)

    @Provides
    fun provideSaveFileUseCase(
        @ApplicationContext context: Context,
        @DefaultCoroutineDispatcher coroutineDispatcher: CoroutineDispatcher
    ): SaveFileUseCase = SaveFileUseCase(context, coroutineDispatcher)

    @Provides
    fun provideCsvParser(): CsvParser = CsvParser
}