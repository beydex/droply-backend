package ru.droply.service.extensions

import ru.droply.data.common.auth.Auth
import ru.droply.sprintor.context.Context
import ru.droply.sprintor.processor.DroplyErrorCode
import ru.droply.sprintor.processor.exception.DroplyException

var Context.auth: Auth?
    get() = get("auth") as? Auth?
    set(value) = set("auth", value)

val Context.storedAuth: Auth
    get() = get("auth") as? Auth? ?: throw DroplyException(code = DroplyErrorCode.UNAUTHORIZED)
