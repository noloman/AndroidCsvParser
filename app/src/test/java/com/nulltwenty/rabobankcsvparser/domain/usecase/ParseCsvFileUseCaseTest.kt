package com.nulltwenty.rabobankcsvparser.domain.usecase

import com.nulltwenty.rabobankcsvparser.data.api.model.CsvFileModel
import com.nulltwenty.rabobankcsvparser.data.repository.CsvFileRepository
import com.nulltwenty.rabobankcsvparser.data.repository.ResultOf
import com.nulltwenty.rabobankcsvparser.domain.CsvParser
import com.nulltwenty.rabobankcsvparser.missingOneComaInHeaderString
import com.nulltwenty.rabobankcsvparser.missingOneCommaToSeparateFirstNameAndSurNameString
import com.nulltwenty.rabobankcsvparser.missingOneQuoteInHeaderString
import com.nulltwenty.rabobankcsvparser.missingQuotesInHeaderString
import com.nulltwenty.rabobankcsvparser.originalCsvFileModelList
import com.nulltwenty.rabobankcsvparser.originalCsvString
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.doSuspendableAnswer
import org.mockito.kotlin.mock

@ExperimentalCoroutinesApi
class ParseCsvFileUseCaseTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var sut: ParseCsvFileUseCase
    private lateinit var repositoryMock: CsvFileRepository
    private lateinit var csvParser: CsvParser

    @Test
    fun `given a CSV file repository with a CSV file with only headers, then it should return an empty list`() =
        runTest {
            val fakeString = """"First name","Sur name","Issue count","Date of birth","avatar""""
            val fakeInputStream = fakeString.toResponseBody()

            repositoryMock = mock {
                onBlocking { fetchCsvFile() } doSuspendableAnswer {
                    flowOf(ResultOf.Success(fakeInputStream))
                }
            }
            csvParser = CsvParser

            sut = ParseCsvFileUseCase(csvParser, testDispatcher)
            val result = sut.invoke(fakeInputStream.byteStream())
            val csvFileModels = result.first()
            assertTrue(csvFileModels.isEmpty())
        }

    @Test(expected = Exception::class)
    fun `given a CSV file repository with wrong data when fetching the CSV file, the use case should throw an exception`() =
        runTest {
            val fakeString = "fake data"
            val fakeInputStream = fakeString.toResponseBody()

            repositoryMock = mock {
                onBlocking { fetchCsvFile() } doSuspendableAnswer {
                    flowOf(ResultOf.Success(fakeInputStream))
                }
            }
            csvParser = CsvParser

            sut = ParseCsvFileUseCase(csvParser, testDispatcher)
            val result = sut.invoke(fakeInputStream.byteStream())
            val csvFileModels = result.first()
            assertTrue(csvFileModels.isEmpty())
        }

    @Test
    fun `given a CSV file repository with CORRECT data when fetching the CSV file, the use case should throw an exception`() =
        runTest {
            val fakeInputStream = originalCsvString.toResponseBody()

            repositoryMock = mock {
                onBlocking { fetchCsvFile() } doSuspendableAnswer {
                    flowOf(ResultOf.Success(fakeInputStream))
                }
            }
            csvParser = CsvParser

            sut = ParseCsvFileUseCase(csvParser, testDispatcher)
            val csvFileModelList: Flow<List<CsvFileModel>?> =
                sut.invoke(fakeInputStream.byteStream())
            val csvFileModels: List<CsvFileModel>? = csvFileModelList.first()
            assertTrue(csvFileModels?.isEmpty() == false)
            assertEquals(csvFileModels, originalCsvFileModelList)
        }

    @Test(expected = Exception::class)
    fun `given a CSV file repository with missing quotes in header when fetching the CSV file it should throw an exception`() =
        runTest {
            val fakeInputStream = missingQuotesInHeaderString.toResponseBody()

            repositoryMock = mock {
                onBlocking { fetchCsvFile() } doSuspendableAnswer {
                    flowOf(ResultOf.Success(fakeInputStream))
                }
            }
            csvParser = CsvParser

            sut = ParseCsvFileUseCase(csvParser, testDispatcher)
            val csvFileModelList = sut.invoke(fakeInputStream.byteStream())
            csvFileModelList.first()
        }

    @Test(expected = Exception::class)
    fun `given a CSV file repository with missing coma in header when fetching the CSV file it should throw an exception`() =
        runTest {
            val fakeInputStream = missingOneQuoteInHeaderString.toResponseBody()

            repositoryMock = mock {
                onBlocking { fetchCsvFile() } doSuspendableAnswer {
                    flowOf(ResultOf.Success(fakeInputStream))
                }
            }
            csvParser = CsvParser

            sut = ParseCsvFileUseCase(csvParser, testDispatcher)
            val csvFileModelList = sut.invoke(fakeInputStream.byteStream())
            csvFileModelList.first()
        }

    @Test(expected = IndexOutOfBoundsException::class)
    fun `given a CSV file repository with missing coma when fetching the CSV file it should throw an exception`() =
        runTest {
            val fakeInputStream = missingOneCommaToSeparateFirstNameAndSurNameString.toResponseBody()

            repositoryMock = mock {
                onBlocking { fetchCsvFile() } doSuspendableAnswer {
                    flowOf(ResultOf.Success(fakeInputStream))
                }
            }
            csvParser = CsvParser

            sut = ParseCsvFileUseCase(csvParser, testDispatcher)
            val csvFileModelList = sut.invoke(fakeInputStream.byteStream())
            csvFileModelList.first()
        }

    @Test(expected = Exception::class)
    fun `given a CSV file repository with missing comma to separate fields in header when fetching the CSV file it should throw an exception`() =
        runTest {
            val fakeInputStream = missingOneComaInHeaderString.toResponseBody()

            repositoryMock = mock {
                onBlocking { fetchCsvFile() } doSuspendableAnswer {
                    flowOf(ResultOf.Success(fakeInputStream))
                }
            }
            csvParser = CsvParser

            sut = ParseCsvFileUseCase(csvParser, testDispatcher)
            val csvFileModelList = sut.invoke(fakeInputStream.byteStream())
            csvFileModelList.first()
        }
}