package ru.droply.dao

import org.springframework.data.jpa.repository.JpaRepository
import ru.droply.entity.BaseEntity
import java.io.Serializable

interface BaseDao<ID : Serializable, T : BaseEntity<ID>> : JpaRepository<T, ID>