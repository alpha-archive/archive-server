package com.alpha.archive

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

@SpringBootApplication
@EnableFeignClients
class ArchiveApplication

fun main(args: Array<String>) {
	runApplication<ArchiveApplication>(*args)
}
