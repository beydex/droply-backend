package ru.droply.feature.processor

import kotlinx.serialization.Serializable

@Serializable
open class DroplyErrorResponse(var success: Boolean = false, val code: DroplyErrorCode)
