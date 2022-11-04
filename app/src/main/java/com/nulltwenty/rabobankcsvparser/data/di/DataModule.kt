package com.nulltwenty.rabobankcsvparser.data.di

import com.nulltwenty.rabobankcsvparser.data.api.service.CsvFileService
import com.nulltwenty.rabobankcsvparser.data.repository.CsvFileRepository
import com.nulltwenty.rabobankcsvparser.data.repository.CsvFileRepositoryImpl
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.CoroutineDispatcher

@Module
@InstallIn(SingletonComponent::class)
object DataModule {
    @Provides
    fun provideRepository(
        @IoCoroutineDispatcher ioCoroutineDispatcher: CoroutineDispatcher, service: CsvFileService
    ): CsvFileRepository =
        CsvFileRepositoryImpl(ioCoroutineDispatcher, service)
}