package com.nulltwenty.rabobankcsvparser.ui

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nulltwenty.rabobankcsvparser.data.api.model.CsvFileModel
import com.nulltwenty.rabobankcsvparser.domain.usecase.FetchCsvFileUseCase
import com.nulltwenty.rabobankcsvparser.domain.usecase.ParseCsvFileUseCase
import com.nulltwenty.rabobankcsvparser.ui.model.UserModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
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
            fetchCsvFileUseCase.invoke().collect { inputStream ->
                _uiState.update { state ->
                    val userList = parseCsvFileUseCase.invoke(inputStream)?.toUiModel()
                    state.copy(
                        error = null, userList = userList
                    )
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

fun List<CsvFileModel>.toUiModel(): List<UserModel> = mutableListOf<UserModel>().apply {
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