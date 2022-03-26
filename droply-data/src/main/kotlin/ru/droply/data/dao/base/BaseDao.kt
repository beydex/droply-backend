package ru.droply.data.dao.base

import java.io.Serializable
import org.springframework.data.jpa.repository.JpaRepository
import ru.droply.data.entity.base.BaseEntity

interface BaseDao<ID : Serializable, T : BaseEntity<ID>> : JpaRepository<T, ID>
