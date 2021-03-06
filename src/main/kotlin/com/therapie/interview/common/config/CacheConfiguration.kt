package com.therapie.interview.common.config

import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.cache.CacheManager
import org.springframework.cache.annotation.CachingConfigurerSupport
import org.springframework.cache.concurrent.ConcurrentMapCacheManager
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.cache.RedisCacheConfiguration
import org.springframework.data.redis.cache.RedisCacheManager
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import java.time.Duration

@Configuration
//@Conditional()
@EnableConfigurationProperties(value = [CacheConfigurationProperties::class])
class CacheConfiguration() : CachingConfigurerSupport() {

    fun createCacheConfiguration(timeoutInSeconds: Long): RedisCacheConfiguration {
        return RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofSeconds(timeoutInSeconds))
    }

    @Bean
    fun redisConnectionFactory(properties: CacheConfigurationProperties): LettuceConnectionFactory {
        val redisStandaloneConfiguration = RedisStandaloneConfiguration();
        redisStandaloneConfiguration.hostName = properties.redisHost
        redisStandaloneConfiguration.port = properties.redisPort
        return LettuceConnectionFactory(redisStandaloneConfiguration);
    }

    @Bean
    fun redisTemplate(connectionFactory: RedisConnectionFactory): RedisTemplate<String, String> {
        val redisTemplate = RedisTemplate<String, String>();
        redisTemplate.setConnectionFactory(connectionFactory);
        return redisTemplate;
    }


    fun cacheConfiguration(properties: CacheConfigurationProperties): RedisCacheConfiguration {
        return createCacheConfiguration(properties.timeoutSeconds);
    }

    @Bean
    fun cacheManager(properties: CacheConfigurationProperties): CacheManager {
        if (properties.type == CacheType.REDIS) {
            val redisConnectionFactory = redisConnectionFactory(properties)
            val cacheConfigurations = HashMap<String, RedisCacheConfiguration>();
            properties.configs.entries.forEach {
                val cacheConfig = it.value
                if (cacheConfig.enabled) {
                    cacheConfigurations[it.key] = createCacheConfiguration(cacheConfig.expirationInSeconds)
                }
            }

            return RedisCacheManager
                    .builder(redisConnectionFactory)
                    .cacheDefaults(cacheConfiguration(properties))
                    .withInitialCacheConfigurations(cacheConfigurations).build();
        } else {
            return ConcurrentMapCacheManager(*properties.configs.keys.toTypedArray())
        }
    }
}