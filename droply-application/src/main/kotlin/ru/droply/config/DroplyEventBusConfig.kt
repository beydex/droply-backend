package ru.droply.config

import org.redisson.Redisson
import org.redisson.api.RTopic
import org.redisson.api.RedissonClient
import org.redisson.codec.SerializationCodec
import org.redisson.config.Config
import org.springframework.beans.factory.annotation.Value
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration
class DroplyEventBusConfig {
    @field:Value("\${droply.bus.redis.url}")
    private lateinit var redisUrl: String

    @field:Value("\${droply.bus.redis.username:#{null}}")
    private var redisUsername: String? = null

    @field:Value("\${droply.bus.redis.password:#{null}}")
    private var redisPassword: String? = null

    // Name of the topic for the events
    @field:Value("\${droply.bus.redis.topic}")
    private lateinit var redisTopic: String

    @Bean
    fun redissonClient(): RedissonClient = Redisson.create(Config().apply {
        val builder = useSingleServer().setAddress(redisUrl)
        if (redisUsername != null) {
            builder.setUsername(redisUsername)
        }
        if (redisPassword != null) {
            builder.setPassword(redisPassword)
        }
        codec = SerializationCodec()
    })

    @Bean
    fun redissonEventTopic(): RTopic = redissonClient().getTopic(redisTopic)
}