package com.beydex.droply.util.dto

data class ErrorResponseDto(
    val message: String,
    val errors: List<String>,
)