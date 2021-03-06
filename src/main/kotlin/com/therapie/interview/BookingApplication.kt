package com.therapie.interview

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.cache.annotation.EnableCaching
import org.springframework.cloud.openfeign.EnableFeignClients

@SpringBootApplication
@EnableCaching
@EnableConfigurationProperties
@EnableFeignClients
class BookingApplication

fun main(args: Array<String>) {
    runApplication<BookingApplication>(*args)
}
