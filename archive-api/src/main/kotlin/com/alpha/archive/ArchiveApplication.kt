package com.alpha.archive

import com.alpha.archive.config.AwsS3Properties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

@SpringBootApplication
@EnableFeignClients
@EnableConfigurationProperties(AwsS3Properties::class)
class ArchiveApplication

fun main(args: Array<String>) {
	runApplication<ArchiveApplication>(*args)
}
