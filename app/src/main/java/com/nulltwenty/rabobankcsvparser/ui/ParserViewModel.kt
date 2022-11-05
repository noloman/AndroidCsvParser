package com.nulltwenty.rabobankcsvparser.ui

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
import java.io.BufferedInputStream
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.text.SimpleDateFormat
import java.util.Locale
import javax.inject.Inject

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
            }.collect { result: ResultOf<ResponseBody> ->
                _uiState.update { state ->
                    when (result) {
                        is ResultOf.Error -> state.copy(error = result.exception.message)
                        is ResultOf.Success -> {
                            val byteInputStream = createInputStreamFromResult(result)
                            saveFileUseCase.invoke(result.data)
                            val csvFileModelList = parseCsvFileUseCase.invoke(byteInputStream)
                            state.copy(
                                userList = csvFileModelList?.toUiModel()
                            )
                        }
                    }
                }
            }
        } catch (e: Exception) {
            _uiState.update {
                it.copy(error = e.message)
            }
        }
    }

    private fun createInputStreamFromResult(result: ResultOf.Success<ResponseBody>): ByteArrayInputStream {
        val inputStream = BufferedInputStream(result.data.byteStream())
        val byteOutputStream = ByteArrayOutputStream()
        inputStream.use { input ->
            byteOutputStream.use { output ->
                input.copyTo(output)
            }
        }
        return ByteArrayInputStream(byteOutputStream.toByteArray())
    }
}

data class CsvFileUiState(val error: String? = null, val userList: List<UserModel>? = null)

private fun List<CsvFileModel>.toUiModel(): List<UserModel> = mutableListOf<UserModel>().apply {
    this@toUiModel.forEach {
        add(
            UserModel(
                fullName = it.firstName + " " + it.surname,
                issueCount = it.issueCount,
                birthdate = SimpleDateFormat(
                    defaultDatePattern, Locale.getDefault()
                ).parse(it.birthdate) ?: throw Exception("There was a problem parsing a date"),
                avatarUrl = it.avatarUrl
            )
        )
    }
}.toList()