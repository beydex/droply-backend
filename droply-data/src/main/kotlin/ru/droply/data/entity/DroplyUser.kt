package ru.droply.data.entity

import ru.droply.data.entity.base.BaseEntity
import javax.persistence.Column
import javax.persistence.Entity

@Entity
class DroplyUser(
    val name: String,
    @Column(unique = true)
    val email: String
) : BaseEntity<Long>()
