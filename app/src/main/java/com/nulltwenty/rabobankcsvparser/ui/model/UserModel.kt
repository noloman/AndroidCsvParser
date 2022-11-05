package com.nulltwenty.rabobankcsvparser.ui.model

import java.util.Date

data class UserModel(
    val fullName: String,
    val issueCount: Int,
    val birthdate: Date,
    val avatarUrl: String
)