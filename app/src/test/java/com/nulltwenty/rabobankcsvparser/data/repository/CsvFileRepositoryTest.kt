package com.nulltwenty.rabobankcsvparser.data.repository

import com.nulltwenty.rabobankcsvparser.data.api.service.CsvFileService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import org.mockito.kotlin.doSuspendableAnswer
import org.mockito.kotlin.mock
import retrofit2.Response
import java.nio.charset.StandardCharsets

@ExperimentalCoroutinesApi
class CsvFileRepositoryTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var sut: CsvFileRepository
    private lateinit var csvFileServiceMock: CsvFileService

    @Test
    fun `given a CsvFileService when it returns an empty successful response, the repository should return an empty string ResultOf#Success`() =
        runTest {
            csvFileServiceMock = mock {
                onBlocking { fetchCsvFile() } doSuspendableAnswer {
                    Response.success("".toResponseBody())
                }
            }
            sut = CsvFileRepositoryImpl(testDispatcher, csvFileServiceMock)
            sut.fetchCsvFile().collect { result ->
                when (result) {
                    is ResultOf.Error -> fail("This case should never be an error")
                    is ResultOf.Success -> {
                        val bytes = result.data.readBytes()
                        val text = String(bytes, StandardCharsets.UTF_8)
                        assertTrue(text.isEmpty())
                    }
                }
            }
        }

    @Test
    fun `given a CsvFileService when it returns an error response, the repository should return a ResultOf#Error with an exception message`() =
        runTest {
            val fakeErrorMessage = "There was some error"
            csvFileServiceMock = mock {
                onBlocking { fetchCsvFile() } doSuspendableAnswer {
                    Response.error(404, fakeErrorMessage.toResponseBody())
                }
            }
            sut = CsvFileRepositoryImpl(testDispatcher, csvFileServiceMock)
            sut.fetchCsvFile().collect {
                when (it) {
                    is ResultOf.Error -> assertTrue(it.exception.message == fakeErrorMessage)
                    is ResultOf.Success -> fail("This case should never be successful")
                }
            }
        }
}