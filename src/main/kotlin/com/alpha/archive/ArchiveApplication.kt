package com.alpha.archive

import com.alpha.archive.config.KakaoOAuthConfig
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication
import org.springframework.cloud.openfeign.EnableFeignClients

@SpringBootApplication
@EnableConfigurationProperties(KakaoOAuthConfig::class)
@EnableFeignClients
class ArchiveApplication

fun main(args: Array<String>) {
	runApplication<ArchiveApplication>(*args)
}
