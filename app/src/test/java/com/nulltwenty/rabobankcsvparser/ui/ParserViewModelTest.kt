package com.nulltwenty.rabobankcsvparser.ui

import android.content.Context
import com.nulltwenty.rabobankcsvparser.data.repository.CsvFileRepository
import com.nulltwenty.rabobankcsvparser.data.repository.ResultOf
import com.nulltwenty.rabobankcsvparser.domain.CsvParser
import com.nulltwenty.rabobankcsvparser.domain.usecase.FetchCsvFileUseCase
import com.nulltwenty.rabobankcsvparser.domain.usecase.ParseCsvFileUseCase
import com.nulltwenty.rabobankcsvparser.domain.usecase.SaveFileUseCase
import com.nulltwenty.rabobankcsvparser.originalCsvString
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import okhttp3.ResponseBody
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertNull
import org.junit.Before
import org.junit.Test
import org.mockito.kotlin.doSuspendableAnswer
import org.mockito.kotlin.mock

@ExperimentalCoroutinesApi
class ParserViewModelTest {
    private val testDispatcher = UnconfinedTestDispatcher()
    private lateinit var sut: ParserViewModel
    private lateinit var mockedCsvFileRepository: CsvFileRepository
    private lateinit var parseCsvFileUseCase: ParseCsvFileUseCase
    private lateinit var fetchCsvFileUseCase: FetchCsvFileUseCase
    private lateinit var saveFileUseCase: SaveFileUseCase
    private lateinit var csvParser: CsvParser
    private val context: Context = mock()

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
    }

    private fun initDependencies(response: ResponseBody) {
        mockedCsvFileRepository = mock {
            onBlocking { fetchCsvFile() } doSuspendableAnswer {
                flowOf(ResultOf.Success(response))
            }
        }
        csvParser = CsvParser
        fetchCsvFileUseCase = FetchCsvFileUseCase(mockedCsvFileRepository, testDispatcher)
        parseCsvFileUseCase = ParseCsvFileUseCase(csvParser, testDispatcher)
        saveFileUseCase = SaveFileUseCase(context, testDispatcher)
    }

    @Test
    fun `given a repository that returns a valid response, when the VM is instantiated, it should update the UI state with the user list`() =
        runTest {
            initDependencies(originalCsvString.toResponseBody())
            sut = ParserViewModel(fetchCsvFileUseCase, parseCsvFileUseCase, saveFileUseCase)
            val uiState = sut.uiState.first()
            assertNotNull(uiState.userList)
            assertNull(uiState.error)
        }

    @Test
    fun `given a repository that returns an empty response, when the VM is instantiated, it should update the UI state with the user list`() =
        runTest {
            val testString = ""
            initDependencies(testString.toResponseBody())
            sut = ParserViewModel(fetchCsvFileUseCase, parseCsvFileUseCase, saveFileUseCase)
            val uiState = sut.uiState.first()
            assertNull(uiState.userList)
            assertNotNull(uiState.error)
        }
}