package com.alpha.archive

import com.alpha.archive.config.NcpObjectStorageProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

@SpringBootApplication
@EnableFeignClients
@EnableConfigurationProperties(NcpObjectStorageProperties::class)
class ArchiveApplication

fun main(args: Array<String>) {
	runApplication<ArchiveApplication>(*args)
}
