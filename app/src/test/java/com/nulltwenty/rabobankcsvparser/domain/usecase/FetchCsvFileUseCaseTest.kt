package com.nulltwenty.rabobankcsvparser.domain.usecase

import com.nulltwenty.rabobankcsvparser.data.repository.CsvFileRepository
import com.nulltwenty.rabobankcsvparser.data.repository.ResultOf
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.fail
import org.junit.Test
import org.mockito.kotlin.doSuspendableAnswer
import org.mockito.kotlin.mock
import java.io.BufferedInputStream
import java.io.BufferedReader

@ExperimentalCoroutinesApi
class FetchCsvFileUseCaseTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var sut: FetchCsvFileUseCase
    private lateinit var repositoryMock: CsvFileRepository

    @Test
    fun `given a repository, when it returns an InputStream, the use case should receive it`() =
        runTest {
            val fakeString = "fake data"
            val fakeInputStream = fakeString.toResponseBody()
            repositoryMock = mock {
                onBlocking { fetchCsvFile() } doSuspendableAnswer {
                    flowOf(ResultOf.Success(fakeInputStream))
                }
            }
            sut = FetchCsvFileUseCase(repositoryMock, testDispatcher)
            val result = sut.invoke()
            when (val resultOf = result.first()) {
                is ResultOf.Error -> fail("This case should never be an error")
                is ResultOf.Success -> {
                    val obtainedInputStream = BufferedInputStream(resultOf.data.byteStream())
                    val content: String =
                        obtainedInputStream.bufferedReader().use(BufferedReader::readText)
                    assertEquals(content, fakeString)
                }
            }
        }
}