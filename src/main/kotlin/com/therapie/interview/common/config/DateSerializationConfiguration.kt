package com.therapie.interview.common.config

import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalTimeSerializer
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder
import java.time.format.DateTimeFormatter


@Configuration
class DateSerializationConfiguration {
    companion object {
        const val DATE_FORMAT = "yyyy-MM-dd"
        const val TIME_FORMAT = "'T'HH:mm:ss"
    }

    @Bean
    fun jsonCustomizer(): Jackson2ObjectMapperBuilderCustomizer? {
        return Jackson2ObjectMapperBuilderCustomizer { builder: Jackson2ObjectMapperBuilder ->
            builder.simpleDateFormat(TIME_FORMAT)
            builder.serializers(LocalDateSerializer(DateTimeFormatter.ofPattern(DATE_FORMAT)))
            builder.serializers(LocalTimeSerializer(DateTimeFormatter.ofPattern(TIME_FORMAT)))
        }
    }
}