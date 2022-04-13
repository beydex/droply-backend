package ru.droply.data.entity

import java.time.ZonedDateTime
import javax.persistence.Column
import javax.persistence.Entity
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne
import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import ru.droply.data.entity.base.BaseEntity

@Entity
class DroplyContact(
    @field:ManyToOne
    @field:JoinColumn(name = "owner_user_id", nullable = false)
    @field:OnDelete(action = OnDeleteAction.CASCADE)
    val owner: DroplyUser,

    @field:ManyToOne
    @field:JoinColumn(name = "user_id", nullable = true)
    @field:OnDelete(action = OnDeleteAction.CASCADE)
    val contact: DroplyUser,

    @field:Column
    val lastSuccessRequestDate: ZonedDateTime
): BaseEntity<Long>()