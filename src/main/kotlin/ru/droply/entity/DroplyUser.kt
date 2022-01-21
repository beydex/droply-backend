package ru.droply.entity

import ru.droply.entity.base.BaseEntity
import javax.persistence.Column
import javax.persistence.Entity

@Entity
class DroplyUser(
    val name: String,
    @Column(unique = true)
    val email: String
) : BaseEntity<Long>()