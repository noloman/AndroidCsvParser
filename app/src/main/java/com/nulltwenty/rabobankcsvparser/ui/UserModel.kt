package com.nulltwenty.rabobankcsvparser.ui

import java.util.Date

data class UserModel(
    val fullName: String, val issueCount: Int, val birthdate: Date, val avatarUrl: String
)