package ru.droply.config

import com.opentable.db.postgres.embedded.EmbeddedPostgres
import com.zaxxer.hikari.HikariDataSource
import org.springframework.beans.factory.ObjectProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.autoconfigure.domain.EntityScan
import org.springframework.boot.autoconfigure.orm.jpa.JpaBaseConfiguration
import org.springframework.boot.autoconfigure.orm.jpa.JpaProperties
import org.springframework.boot.orm.jpa.hibernate.SpringPhysicalNamingStrategy
import org.springframework.boot.test.context.TestConfiguration
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.env.Environment
import org.springframework.core.env.Profiles
import org.springframework.data.jpa.repository.config.EnableJpaRepositories
import org.springframework.orm.jpa.vendor.AbstractJpaVendorAdapter
import org.springframework.orm.jpa.vendor.Database
import org.springframework.orm.jpa.vendor.HibernateJpaVendorAdapter
import org.springframework.transaction.annotation.EnableTransactionManagement
import org.springframework.transaction.jta.JtaTransactionManager
import javax.sql.DataSource

@TestConfiguration
@EnableTransactionManagement
@EnableJpaRepositories(basePackages = ["ru.droply.dao"])
@EntityScan("ru.droply.entity")
class DroplyTestJpaConfig(
    dataSource: DataSource,
    properties: JpaProperties,
    jtaTransactionManager: ObjectProvider<JtaTransactionManager>
) : JpaBaseConfiguration(dataSource, properties, jtaTransactionManager) {

    @Autowired
    private lateinit var environment: Environment

    @Configuration
    internal class DataSourceConfig {
        @Bean
        @Throws(Exception::class)
        fun embeddedPostgres(): EmbeddedPostgres {
            return EmbeddedPostgres.start()
        }

        @Bean
        fun dataSource(postgres: EmbeddedPostgres): DataSource {
            val url = postgres.getJdbcUrl("postgres", "postgres")
            val dataSource = HikariDataSource()
            dataSource.jdbcUrl = url
            return dataSource
        }
    }

    override fun createJpaVendorAdapter(): AbstractJpaVendorAdapter? {
        val adapter = HibernateJpaVendorAdapter()
        adapter.setShowSql(false)
        adapter.setDatabasePlatform("org.eclipse.persistence.platform.database.PostgreSQLPlatform")
        adapter.setDatabase(Database.POSTGRESQL)
        return adapter
    }

    override fun getVendorProperties(): Map<String, Any>? {
        val jpaProperties: MutableMap<String, Any> = HashMap()
        jpaProperties["hibernate.hbm2ddl.auto"] = "update"
        jpaProperties["hibernate.physical_naming_strategy"] = SpringPhysicalNamingStrategy::class.java
        jpaProperties["hibernate.dialect"] = "org.hibernate.dialect.PostgreSQLDialect"

        // Enable SQL log if profile is set
        if (environment.acceptsProfiles(Profiles.of("show-sql"))) {
            jpaProperties["hibernate.show_sql"] = "true"
        }

        return jpaProperties
    }
}