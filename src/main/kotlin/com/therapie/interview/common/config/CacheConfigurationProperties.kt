package com.therapie.interview.common.config

import org.springframework.boot.context.properties.ConfigurationProperties

@ConfigurationProperties(prefix = "cache")
data class CacheConfigurationProperties(
        var timeoutSeconds: Long = 60,
        var redisPort: Int = 6379,
        var redisHost: String = "localhost",
        var type: CacheType = CacheType.MEMORY,
        var configs: Map<String, CacheConfig> = HashMap())

data class CacheConfig(
        var expirationInSeconds: Long = 60,
        var enabled: Boolean = true,
)

enum class CacheType() {
    REDIS, MEMORY
}

