package ru.droply.feature.context.auth

import ru.droply.entity.DroplyUser

data class Auth(val provider: AuthProvider, val user: DroplyUser)
