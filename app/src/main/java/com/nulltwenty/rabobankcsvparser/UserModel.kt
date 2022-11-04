package com.nulltwenty.rabobankcsvparser

import java.util.Date

data class UserModel(
    val firstName: String,
    val surname: String,
    val issueCount: Int,
    val birthdate: Date,
    val avatarUrl: String
)
