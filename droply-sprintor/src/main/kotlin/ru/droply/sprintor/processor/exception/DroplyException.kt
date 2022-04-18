package ru.droply.sprintor.processor.exception

import ru.droply.sprintor.processor.DroplyErrorCode

class DroplyException(val code: DroplyErrorCode, override val message: String? = null) : Exception(message)
