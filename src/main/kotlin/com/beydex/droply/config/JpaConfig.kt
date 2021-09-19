package com.beydex.droply.config

import com.opentable.db.postgres.embedded.EmbeddedPostgres
import com.zaxxer.hikari.HikariDataSource
import org.springframework.beans.factory.ObjectProvider
import org.springframework.boot.autoconfigure.orm.jpa.JpaBaseConfiguration
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties
import org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.jpa.vendor.AbstractJpaVendorAdapter
import org.springframework.orm.jpa.vendor.Database
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.transaction.jta.JtaTransactionManager
import javax.sql.DataSource

@Configuration
@EnableTransactionManagement
@EnableJpaRepositories
open class JpaConfig protected constructor(
    dataSource: DataSource,
    properties: JpaProperties,
    jtaTransactionManager: ObjectProvider<JtaTransactionManager>,
) : JpaBaseConfiguration(dataSource, properties, jtaTransactionManager) {
    @Configuration
    internal open class DataSourceConfig {
        @Bean
        open fun embeddedPostgres(): EmbeddedPostgres {
            return EmbeddedPostgres.start()
        }

        @Bean
        open fun dataSource(postgres: EmbeddedPostgres): DataSource {
            val url = postgres.getJdbcUrl("postgres", "postgres")
            val dataSource = HikariDataSource()
            dataSource.jdbcUrl = url
            return dataSource
        }
    }

    override fun createJpaVendorAdapter(): AbstractJpaVendorAdapter {
        val adapter = HibernateJpaVendorAdapter()
        adapter.setShowSql(false)
        adapter.setDatabasePlatform("org.eclipse.persistence.platform.database.PostgreSQLPlatform")
        adapter.setDatabase(Database.POSTGRESQL)
        return adapter
    }

    override fun getVendorProperties(): Map<String, Any> {
        val jpaProperties: MutableMap<String, Any> = HashMap()
        jpaProperties["hibernate.hbm2ddl.auto"] = "create"
        jpaProperties["hibernate.physical_naming_strategy"] = SpringPhysicalNamingStrategy::class.java
        jpaProperties["hibernate.dialect"] = "org.hibernate.dialect.PostgreSQL92Dialect"
        return jpaProperties
    }
}