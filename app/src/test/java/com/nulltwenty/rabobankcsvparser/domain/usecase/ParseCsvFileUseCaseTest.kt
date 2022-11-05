package com.nulltwenty.rabobankcsvparser.domain.usecase

import com.nulltwenty.rabobankcsvparser.data.api.model.CsvFileModel
import com.nulltwenty.rabobankcsvparser.data.repository.CsvFileRepository
import com.nulltwenty.rabobankcsvparser.data.repository.ResultOf
import com.nulltwenty.rabobankcsvparser.fakeCsvFileContents
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test
import org.mockito.kotlin.doSuspendableAnswer
import org.mockito.kotlin.mock
import java.io.ByteArrayInputStream

@ExperimentalCoroutinesApi
class ParseCsvFileUseCaseTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var sut: ParseCsvFileUseCase
    private lateinit var repositoryMock: CsvFileRepository

    @Test
    fun `given a CSV file repository with wrong data when fetching the CSV file, the use case should return an empty list`() =
        runTest {
            val fakeString = "fake data"
            val fakeInputStream: ByteArrayInputStream = fakeString.byteInputStream()

            repositoryMock = mock {
                onBlocking { fetchCsvFile() } doSuspendableAnswer {
                    flowOf(ResultOf.Success(fakeInputStream))
                }
            }

            sut = ParseCsvFileUseCase(testDispatcher)
            val result: List<CsvFileModel>? = sut.invoke(fakeInputStream)
            assertTrue(result?.isEmpty() == true)
        }

    @Test
    fun `given a CSV file repository with data when fetching the CSV file, the use case should return a list of CsvFileModel`() =
        runTest {
            val testString = """"First name","Sur name","Issue count","Date of birth","avatar"
"Theo","Jansen",5,"1978-01-02T00:00:00","https://api.multiavatar.com/2cdf5db9b4dee297b7.png"
"Fiona","de Vries",7,"1950-11-12T00:00:00","https://api.multiavatar.com/b9339cb9e7a833cd5e.png"
"Petra","Boersma",1,"2001-04-20T00:00:00","https://api.multiavatar.com/2672c49d6099f87274.png""""
            val fakeInputStream: ByteArrayInputStream = testString.byteInputStream()

            repositoryMock = mock {
                onBlocking { fetchCsvFile() } doSuspendableAnswer {
                    flowOf(ResultOf.Success(fakeInputStream))
                }
            }

            sut = ParseCsvFileUseCase(testDispatcher)
            val csvFileModelList = sut.invoke(fakeInputStream)
            assertTrue(csvFileModelList?.isEmpty() == false)
            assertEquals(csvFileModelList, fakeCsvFileContents)
        }
}