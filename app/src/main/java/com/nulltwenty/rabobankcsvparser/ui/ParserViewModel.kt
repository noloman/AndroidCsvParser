package com.nulltwenty.rabobankcsvparser.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nulltwenty.rabobankcsvparser.data.api.model.CsvFileModel
import com.nulltwenty.rabobankcsvparser.data.repository.ResultOf
import com.nulltwenty.rabobankcsvparser.domain.usecase.FetchCsvFileUseCase
import com.nulltwenty.rabobankcsvparser.domain.usecase.ParseCsvFileUseCase
import com.nulltwenty.rabobankcsvparser.ui.model.UserModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.text.DateFormat
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class ParserViewModel @Inject constructor(
    private val fetchCsvFileUseCase: FetchCsvFileUseCase,
    private val parseCsvFileUseCase: ParseCsvFileUseCase
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
                _uiState.update { state ->
                    when (result) {
                        is ResultOf.Error -> state.copy(error = result.exception.message)
                        is ResultOf.Success -> state.copy(
                            userList = parseCsvFileUseCase.invoke(result.data)?.toUiModel()
                        )
                    }
                }
            }
        } catch (e: Exception) {
            _uiState.update {
                it.copy(error = e.message)
            }
        }
    }
}

data class CsvFileUiState(val error: String? = null, val userList: List<UserModel>? = null)

private fun List<CsvFileModel>.toUiModel(): List<UserModel> = mutableListOf<UserModel>().apply {
    this@toUiModel.forEach {
        add(
            UserModel(
                fullName = it.firstName + " " + it.surname,
                issueCount = it.issueCount,
                birthdate = DateFormat.getDateInstance(
                    DateFormat.SHORT, Locale.getDefault()
                ).format(it.birthdate),
                avatarUrl = it.avatarUrl
            )
        )
    }
}.toList()