package com.beydex.droply.dto

import com.beydex.droply.controller.error.ErrorCodes
import com.fasterxml.jackson.annotation.JsonProperty

data class BaseResponse<T>(
    @JsonProperty("") val body: T,
    val errorCodes: ErrorCodes,
    val message: String?,
) {
    companion object {
        inline fun <reified T> success(body: T) = BaseResponse(body, ErrorCodes.OK, null)
        fun fail(errorCodes: ErrorCodes, message: String) = BaseResponse(null, errorCodes, message)
    }
}