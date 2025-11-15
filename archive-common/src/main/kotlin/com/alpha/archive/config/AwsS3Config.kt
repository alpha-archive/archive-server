package com.alpha.archive.config

import org.springframework.boot.context.properties.ConfigurationProperties
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Profile
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider
import software.amazon.awssdk.regions.Region
import software.amazon.awssdk.services.s3.S3Client

/**
 * AWS S3 설정 (Elastic Beanstalk 환경용)
 * EC2 인스턴스 프로파일을 사용하여 인증
 */
@Configuration
class AwsS3Config(
    private val awsS3Properties: AwsS3Properties
) {

    @Bean
    fun s3Client(): S3Client {
        return S3Client.builder()
            .region(Region.of(awsS3Properties.region))
            .credentialsProvider(DefaultCredentialsProvider.create())  // IAM Role 자동 인식
            .build()
    }
}

@ConfigurationProperties(prefix = "aws.s3")
data class AwsS3Properties(
    val region: String = "ap-northeast-2",
    val bucketName: String = ""
)
