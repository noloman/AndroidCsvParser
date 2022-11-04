package com.nulltwenty.rabobankcsvparser.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nulltwenty.rabobankcsvparser.data.api.model.CsvFileModel
import com.nulltwenty.rabobankcsvparser.domain.usecase.FetchCsvFileUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.io.InputStream
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ParserViewModel @Inject constructor(private val csvFileUseCase: FetchCsvFileUseCase) :
    ViewModel() {
    private val _uiState = MutableStateFlow(CsvFileUiState())
    val uiState: StateFlow<CsvFileUiState> = _uiState.asStateFlow()

    init {
        getCsvFile()
    }

    private fun getCsvFile() = viewModelScope.launch {
        try {
            csvFileUseCase.invoke()
                //.cachedIn(this)
                .collect { csvFileModel ->
                    _uiState.update { state ->
                        state.copy(error = null, user = csvFileModel.toUiModel())
                    }
                }
        } catch (e: Exception) {
            _uiState.update {
                it.copy(error = e.message)
            }
        }
    }

    private fun parseCsv(inputStream: InputStream): List<CsvFileModel> {
        val reader = inputStream.bufferedReader()
        reader.readLine()
        val dateFormat =
            SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss", Locale.getDefault()) // 1978-01-02T00:00:00
        return reader.lineSequence().filter { it.isNotBlank() }.map {
            val (firstName, surname, issueCount, birthdate, avatarUrl) = it.split(
                ',', ignoreCase = false
            )
            CsvFileModel(
                firstName.removeSurrounding("\""),
                surname.removeSurrounding("\""),
                issueCount.toInt(),
                dateFormat.parse(birthdate.removeSurrounding("\"")) ?: Date(),
                avatarUrl.removeSurrounding("\"")
            )
        }.toList()
    }
}

data class CsvFileUiState(val error: String? = null, val user: UserModel? = null)

fun CsvFileModel.toUiModel(): UserModel =
    UserModel("$firstName $surname", issueCount, birthdate, avatarUrl)