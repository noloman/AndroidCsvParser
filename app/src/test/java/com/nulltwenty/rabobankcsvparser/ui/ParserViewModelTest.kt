package com.nulltwenty.rabobankcsvparser.ui

import android.content.Context
import com.nulltwenty.rabobankcsvparser.data.repository.CsvFileRepository
import com.nulltwenty.rabobankcsvparser.data.repository.ResultOf
import com.nulltwenty.rabobankcsvparser.domain.usecase.FetchCsvFileUseCase
import com.nulltwenty.rabobankcsvparser.domain.usecase.ParseCsvFileUseCase
import com.nulltwenty.rabobankcsvparser.domain.usecase.SaveFileUseCase
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
        fetchCsvFileUseCase = FetchCsvFileUseCase(mockedCsvFileRepository, testDispatcher)
        parseCsvFileUseCase = ParseCsvFileUseCase(testDispatcher)
        saveFileUseCase = SaveFileUseCase(context, testDispatcher)
    }

    @Test
    fun `given a repository that returns a valid response, when the VM is instantiated, it should update the UI state with the user list`() =
        runTest {
            val testString = """"First name","Sur name","Issue count","Date of birth","avatar"
"Theo","Jansen",5,"1978-01-02T00:00:00","https://api.multiavatar.com/2cdf5db9b4dee297b7.png"
"Fiona","de Vries",7,"1950-11-12T00:00:00","https://api.multiavatar.com/b9339cb9e7a833cd5e.png"
"Petra","Boersma",1,"2001-04-20T00:00:00","https://api.multiavatar.com/2672c49d6099f87274.png""""
            initDependencies(testString.toResponseBody())
            sut = ParserViewModel(fetchCsvFileUseCase, parseCsvFileUseCase, saveFileUseCase)
            val uiState = sut.uiState.first()
            assertNotNull(uiState.userList)
            assertNull(uiState.error)
        }

    @Test
    fun `given a repository that returns an invalid response, when the VM is instantiated, it should update the UI state with the user list`() =
        runTest {
            val testString = ""
            initDependencies(testString.toResponseBody())
            sut = ParserViewModel(fetchCsvFileUseCase, parseCsvFileUseCase, saveFileUseCase)
            val uiState = sut.uiState.first()
            assertNull(uiState.userList)
            assertNotNull(uiState.error)
        }
}