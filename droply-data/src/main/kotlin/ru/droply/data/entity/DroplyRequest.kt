package ru.droply.data.entity

import org.hibernate.annotations.OnDelete
import org.hibernate.annotations.OnDeleteAction
import ru.droply.data.entity.base.BaseEntity
import java.time.ZonedDateTime
import javax.persistence.CollectionTable
import javax.persistence.Column
import javax.persistence.ElementCollection
import javax.persistence.Entity
import javax.persistence.JoinColumn
import javax.persistence.ManyToOne

object DroplyRequestConstraints {
    const val MIN_OFFER_LENGTH = 1
    const val MAX_OFFER_LENGTH = 2048

    const val MIN_FILES_SIZE = 1
    const val MAX_FILES_SIZE = 20

    const val MAX_SIGNALING_CONTENT_SIZE = 3000
}

@Entity
class DroplyRequest(
    @field:ManyToOne
    @field:JoinColumn(name = "sender_user_id", nullable = false)
    @field:OnDelete(action = OnDeleteAction.CASCADE)
    val sender: DroplyUser,

    @field:ManyToOne
    @field:JoinColumn(name = "receiver_user_id", nullable = false)
    @field:OnDelete(action = OnDeleteAction.CASCADE)
    val receiver: DroplyUser,

    @field:Column
    val creationTime: ZonedDateTime,

    @field:Column(length = DroplyRequestConstraints.MAX_OFFER_LENGTH, nullable = false)
    val offer: String,

    @field:ElementCollection
    @field:CollectionTable(name = "droply_request_file", joinColumns = [JoinColumn(name = "request_id")])
    val files: Set<DroplyFile> = mutableSetOf(),

    @field:Column(columnDefinition = "boolean default false", nullable = false)
    var active: Boolean = false
) : BaseEntity<Long>()
