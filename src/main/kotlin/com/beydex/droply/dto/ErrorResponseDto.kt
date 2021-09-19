package com.beydex.droply.dto

data class ErrorResponseDto(
    val message: String,
    val errors: List<String>,
)