package ru.droply.data.entity

import ru.droply.data.entity.base.BaseEntity
import javax.persistence.Column
import javax.persistence.Entity

@Entity
class DroplyUser(
    @field:Column(nullable = false)
    val name: String,

    @field:Column(nullable = false, unique = true)
    val email: String,

    @field:Column
    val avatarUrl: String? = null
) : BaseEntity<Long>()
