package ru.droply.entity

import org.springframework.data.util.ProxyUtils
import java.io.Serializable
import javax.persistence.GeneratedValue
import javax.persistence.GenerationType
import javax.persistence.Id
import javax.persistence.MappedSuperclass

@MappedSuperclass
abstract class BaseEntity<T: Serializable> {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: T? = null

    override fun equals(other: Any?): Boolean {
        if (other == null) {
            return false
        }

        if (this === other) {
            return true
        }

        if (javaClass != ProxyUtils.getUserClass(other)) {
            return false
        }

        other as BaseEntity<*>

        return this.id != null && this.id == other.id
    }

    override fun hashCode(): Int = id?.hashCode() ?: 25

    override fun toString(): String {
        return "${this.javaClass.simpleName}(id=$id)"
    }
}