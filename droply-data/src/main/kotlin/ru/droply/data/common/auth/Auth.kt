package ru.droply.data.common.auth

import ru.droply.data.entity.DroplyUser

data class Auth(val provider: AuthProvider, val user: DroplyUser)
