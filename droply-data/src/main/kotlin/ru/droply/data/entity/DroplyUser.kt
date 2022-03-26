package ru.droply.data.entity

import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.PrimaryKeyJoinColumn
import javax.persistence.SecondaryTable
import ru.droply.data.entity.base.BaseEntity

@Entity
@SecondaryTable(
    name = "droply_user_id_urid",
    pkJoinColumns = [PrimaryKeyJoinColumn(name = "droply_user_id", referencedColumnName = "id")]
)
class DroplyUser(
    @field:Column(nullable = false)
    val name: String,

    @field:Column(nullable = false, unique = true)
    val email: String,

    @field:Column
    val avatarUrl: String? = null,

    @field:Column(table = "droply_user_id_urid", name = "urid")
    var urid: Int? = null,
) : BaseEntity<Long>()
