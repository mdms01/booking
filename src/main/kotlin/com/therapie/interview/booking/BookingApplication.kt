package com.therapie.interview.booking

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.context.annotation.ComponentScan

@SpringBootApplication
@ComponentScan(basePackages = ["com.therapie.interview"])
class BookingApplication

fun main(args: Array<String>) {
	runApplication<BookingApplication>(*args)
}
