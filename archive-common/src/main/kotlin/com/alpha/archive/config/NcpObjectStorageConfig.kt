package com.alpha.archive.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client
import java.net.URI

@Configuration
class NcpObjectStorageConfig(
    private val ncpObjectStorageProperties: NcpObjectStorageProperties
) {

    @Bean
    fun s3Client(): S3Client {
        val credentials = AwsBasicCredentials.create(
            ncpObjectStorageProperties.accessKey,
            ncpObjectStorageProperties.secretKey
        )

        return S3Client.builder()
            .endpointOverride(URI.create(ncpObjectStorageProperties.endpoint))
            .credentialsProvider(StaticCredentialsProvider.create(credentials))
            .region(Region.of(ncpObjectStorageProperties.region))
            .build()
    }
}

@ConfigurationProperties(prefix = "ncp.object-storage")
data class NcpObjectStorageProperties(
    val endpoint: String = "",
    val region: String = "",
    val accessKey: String = "",
    val secretKey: String = "",
    val bucketName: String = ""
)
