package ru.droply.sprintor.event

import ru.droply.data.entity.DroplyUser

class UserLogoutEvent(val user: DroplyUser) : DroplyEvent(user)
