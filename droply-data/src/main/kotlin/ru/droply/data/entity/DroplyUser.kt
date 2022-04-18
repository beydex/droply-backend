package ru.droply.data.entity

import ru.droply.data.entity.base.BaseEntity
import javax.persistence.CascadeType
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.FetchType
import javax.persistence.OneToMany
import javax.persistence.PrimaryKeyJoinColumn
import javax.persistence.SecondaryTable

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

    @field:OneToMany(
        cascade = [CascadeType.ALL],
        fetch = FetchType.LAZY,
        mappedBy = "owner"
    )
    val contacts: MutableList<DroplyContact> = mutableListOf(),

    @field:OneToMany(
        cascade = [CascadeType.ALL],
        fetch = FetchType.LAZY,
        mappedBy = "receiver"
    )
    val incomingRequests: MutableSet<DroplyRequest> = mutableSetOf(),

    @field:OneToMany(
        cascade = [CascadeType.ALL],
        fetch = FetchType.LAZY,
        mappedBy = "sender"
    )
    val outgoingRequests: MutableSet<DroplyRequest> = mutableSetOf(),

) : BaseEntity<Long>()
