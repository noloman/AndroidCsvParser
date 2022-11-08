package com.nulltwenty.rabobankcsvparser.ui

import androidx.annotation.VisibleForTesting
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nulltwenty.rabobankcsvparser.data.api.model.CsvFileModel
import com.nulltwenty.rabobankcsvparser.data.repository.ResultOf
import com.nulltwenty.rabobankcsvparser.domain.usecase.FetchCsvFileUseCase
import com.nulltwenty.rabobankcsvparser.domain.usecase.ParseCsvFileUseCase
import com.nulltwenty.rabobankcsvparser.domain.usecase.SaveFileUseCase
import com.nulltwenty.rabobankcsvparser.domain.usecase.defaultDatePattern
import com.nulltwenty.rabobankcsvparser.ui.model.UserModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import okhttp3.ResponseBody
import java.io.IOException
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

const val datePattern = "EEEE dd-MM-yyyy HH:mm:ss"

@HiltViewModel
class ParserViewModel @Inject constructor(
    private val fetchCsvFileUseCase: FetchCsvFileUseCase,
    private val parseCsvFileUseCase: ParseCsvFileUseCase,
    private val saveFileUseCase: SaveFileUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(CsvFileUiState())
    val uiState: StateFlow<CsvFileUiState> = _uiState.asStateFlow()

    init {
        getCsvFile()
    }

    private fun getCsvFile() = viewModelScope.launch {
        try {
            fetchCsvFileUseCase.invoke().catch { exception ->
                _uiState.update { state ->
                    state.copy(error = exception.message)
                }
            }.collect { result ->
                when (result) {
                    is ResultOf.Error -> {
                        when (result.exception) {
                            is IOException -> {
                                val byteInputStream: InputStream? = saveAndReadFileFromStorage()
                                if (byteInputStream != null) {
                                    parseCsvFileUseCase.invoke(byteInputStream).catch { exception ->
                                        _uiState.update { state ->
                                            state.copy(error = exception.message)
                                        }
                                    }.collect { csvFileModelList ->
                                        _uiState.update { state ->
                                            state.copy(userList = csvFileModelList.toUiModel())
                                        }
                                    }
                                } else {
                                    TODO("Error")
                                }
                            }
                        }
                        _uiState.update { it.copy(error = result.exception.message) }
                    }
                    is ResultOf.Success -> {
                        val byteInputStream: InputStream? = saveAndReadFileFromStorage(result)
                        if (byteInputStream != null) {
                            parseCsvFileUseCase.invoke(byteInputStream).catch { exception ->
                                _uiState.update { state ->
                                    state.copy(error = exception.message)
                                }
                            }.collect { csvFileModelList ->
                                _uiState.update { state ->
                                    state.copy(userList = csvFileModelList.toUiModel())
                                }
                            }
                        } else {
                            TODO("Error")
                        }
                    }
                }
            }
        } catch (e: Exception) {
            _uiState.update { state ->
                state.copy(error = e.message ?: "Unexpected error happened")
            }
        }
    }

    private suspend fun saveAndReadFileFromStorage(result: ResultOf.Success<ResponseBody>): InputStream? =
        saveFileUseCase.invoke(result.data)
}

data class CsvFileUiState(val error: String? = null, val userList: List<UserModel>? = null)

private fun List<CsvFileModel>.toUiModel(): List<UserModel> = mutableListOf<UserModel>().apply {
    this@toUiModel.forEach { csvFileModel ->
        add(
            UserModel(
                fullName = csvFileModel.firstName + " " + csvFileModel.surname,
                issueCount = csvFileModel.issueCount,
                birthdate = csvFileModel.birthdate.formatBirthdateString(),
                avatarUrl = csvFileModel.avatarUrl
            )
        )
    }
}.toList()

@VisibleForTesting
fun String.formatBirthdateString(): String {
    val originalSimpleDateFormat = SimpleDateFormat(
        defaultDatePattern, Locale.getDefault()
    )
    val newSimpleDateFormat = SimpleDateFormat(datePattern, Locale.getDefault())
    val oldDate: Date = originalSimpleDateFormat.parse(this)
        ?: throw Exception("There was a problem parsing a date")
    return newSimpleDateFormat.format(oldDate)
        ?: throw Exception("There was a problem parsing a date")
}