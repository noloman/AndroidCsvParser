package com.nulltwenty.rabobankcsvparser.data.repository

import com.nulltwenty.rabobankcsvparser.data.api.service.CsvFileService
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Assert.fail
import org.junit.Test
import org.mockito.kotlin.doSuspendableAnswer
import org.mockito.kotlin.doThrow
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
    fun `given a CsvFileService when it returns an successful response, the repository should return the string in a ResultOf#Success`() =
        runTest {
            val testString = """"First name","Sur name","Issue count","Date of birth","avatar"
"Theo","Jansen",5,"1978-01-02T00:00:00","https://api.multiavatar.com/2cdf5db9b4dee297b7.png"
"Fiona","de Vries",7,"1950-11-12T00:00:00","https://api.multiavatar.com/b9339cb9e7a833cd5e.png"
"Petra","Boersma",1,"2001-04-20T00:00:00","https://api.multiavatar.com/2672c49d6099f87274.png""""
            csvFileServiceMock = mock {
                onBlocking { fetchCsvFile() } doSuspendableAnswer {
                    Response.success(
                        testString.toResponseBody()
                    )
                }
            }
            sut = CsvFileRepositoryImpl(testDispatcher, csvFileServiceMock)
            sut.fetchCsvFile().collect { result ->
                when (result) {
                    is ResultOf.Error -> fail("This case should never be an error")
                    is ResultOf.Success -> {
                        val bytes = result.data.readBytes()
                        val text = String(bytes, StandardCharsets.UTF_8)
                        assertEquals(text, testString)
                    }
                }
            }
        }

    @Test
    fun `given a CsvFileService when it returns an error response, the repository should return a ResultOf#Error with an exception message`() =
        runTest {
            val fakeErrorMessage = "Error fetching the CSV file"
            csvFileServiceMock = mock {
                onBlocking { fetchCsvFile() } doSuspendableAnswer {
                    Response.error(404, "not found".toResponseBody())
                }
            }
            sut = CsvFileRepositoryImpl(testDispatcher, csvFileServiceMock)
            sut.fetchCsvFile().collect {
                when (it) {
                    is ResultOf.Error -> assert(it.exception.message?.contains(fakeErrorMessage) == true)
                    is ResultOf.Success -> fail("This case should never be successful")
                }
            }
        }

    @Test
    fun `given a CsvFileService that throws an exception, the repository should return a ResultOf#Error with an exception message`() = runTest {
        val fakeErrorMessage = "There was some error"
        csvFileServiceMock = mock {
            onBlocking { fetchCsvFile() } doThrow Exception(fakeErrorMessage)
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