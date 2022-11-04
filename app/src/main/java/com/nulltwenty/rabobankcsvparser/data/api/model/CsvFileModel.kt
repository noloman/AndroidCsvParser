package com.nulltwenty.rabobankcsvparser.data.api.model

import java.util.Date

data class CsvFileModel(
    val firstName: String,
    val surname: String,
    val issueCount: Int,
    val birthdate: Date,
    val avatarUrl: String
)
