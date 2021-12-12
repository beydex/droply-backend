package com.beydex.droply.entity

import com.mongodb.lang.NonNull
import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.DBRef
import org.springframework.data.mongodb.core.mapping.Document

@Document
data class User(
    @Id
    val id: String? = null,

    // E.g. contacts
    @NonNull
    val name: String,
    @NonNull
    val email: String,
    val code: String? = null,

    // Contact list here
    @DBRef
    val contactList: List<User>
)