package com.nulltwenty.rabobankcsvparser.data.di

import com.nulltwenty.rabobankcsvparser.data.repository.CsvFileRepository
import com.nulltwenty.rabobankcsvparser.data.repository.ResultOf
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody

@Module
@InstallIn(SingletonComponent::class)
object TestDataModule {
    @Provides
    fun provideRepository(): CsvFileRepository = object : CsvFileRepository {
        override suspend fun fetchCsvFile(): Flow<ResultOf<ResponseBody>> =
            flowOf(
                ResultOf.Success(
                    """
                    "First name","Sur name","Issue count","Date of birth","avatar"
                    "Theo","Jansen",5,"1978-01-02T00:00:00","https://api.multiavatar.com/2cdf5db9b4dee297b7.png"
                    "Fiona","de Vries",7,"1950-11-12T00:00:00","https://api.multiavatar.com/b9339cb9e7a833cd5e.png"
                    "Petra","Boersma",1,"2001-04-20T00:00:00","https://api.multiavatar.com/2672c49d6099f87274.png"
                """.trimIndent().toResponseBody()
                )
            )
    }
}