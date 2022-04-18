package ru.droply.data.entity

import javax.persistence.Embeddable

@Embeddable
class DroplyFile(
    var name: String,
    var size: Long
)
