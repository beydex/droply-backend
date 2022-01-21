package ru.droply.dao.base

import org.springframework.data.jpa.repository.JpaRepository
import ru.droply.entity.base.BaseEntity
import java.io.Serializable

interface BaseDao<ID : Serializable, T : BaseEntity<ID>> : JpaRepository<T, ID>