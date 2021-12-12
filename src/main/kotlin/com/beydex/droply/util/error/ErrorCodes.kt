package com.beydex.droply.util.error

enum class ErrorCodes(val code: Int, val description: String) {
    OK(0, "Success"),
    INTERNAL_ERROR(1, "Internal server error"),
    ACCESS_DENIED(2, "Access forbidden"),
    INVALID_VALUE(3, "Invalid value"),
    UNKNOWN(999, "Unknown status")
}