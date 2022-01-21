package ru.droply.feature.context.auth

import ru.droply.entity.DroplyUser

data class Auth(val socialProvider: AuthProvider, val user: DroplyUser)
