package ru.droply.test

import ru.droply.data.common.auth.Auth
import ru.droply.data.common.auth.AuthProvider
import ru.droply.data.entity.DroplyUser

fun DroplyTest.useAuth(username: String? = null, email: String? = null, provider: AuthProvider? = null) {
    context.auth = Auth(
        provider = provider ?: AuthProvider.CUSTOM,
        user = DroplyUser(
            name = username ?: "misterX",
            email = email ?: "misterX@grandson.us"
        )
    )
}

fun <T> DroplyTest.useAuth(
    username: String? = null,
    email: String? = null,
    provider: AuthProvider? = null,
    action: (Auth) -> T
): T {
    val previousAuth = context.auth
    val currentAuth = Auth(
        provider = provider ?: AuthProvider.CUSTOM,
        user = DroplyUser(
            name = username ?: "misterX",
            email = email ?: "misterX@grandson.us"
        )
    )

    val result: T
    try {
        context.auth = currentAuth
        result = action(currentAuth)
    } finally {
        context.auth = previousAuth
    }

    return result
}

fun <T> DroplyTest.useAuthUser(
    username: String? = null,
    email: String? = null,
    provider: AuthProvider? = null,
    action: (DroplyUser) -> T
): T {
    val concreteEmail = email ?: "misterX@grandson.us"
    val candidate = userService.findByEmail(concreteEmail)
        ?: userService.save(
            DroplyUser(
                name = username ?: "misterX",
                email = concreteEmail
            )
        )

    val previousAuth = context.auth
    val currentAuth = Auth(
        provider = provider ?: AuthProvider.CUSTOM,
        user = candidate
    )

    val result: T
    try {
        context.auth = currentAuth
        result = action(candidate)
    } finally {
        context.auth = previousAuth
        userService.removeUserByEmail(concreteEmail)
    }

    return result
}

fun DroplyTest.makeUser(
    username: String? = null,
    email: String? = null
): DroplyUser {
    val concreteEmail = email ?: "misterX@grandson.us"
    return userService.findByEmail(concreteEmail) ?: userService.save(
        DroplyUser(
            name = username ?: "misterX",
            email = concreteEmail
        )
    )
}