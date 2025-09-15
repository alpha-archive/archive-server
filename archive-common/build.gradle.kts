import org.springframework.boot.gradle.tasks.bundling.BootJar

dependencies {
    // Spring (BOM은 루트에서 관리)
    api("org.springframework.boot:spring-boot-starter-web")
    api("org.springframework.boot:spring-boot-starter-validation")
    api("org.springframework.cloud:spring-cloud-starter-openfeign")
    
    // Jackson
    api("com.fasterxml.jackson.module:jackson-module-kotlin")
    api("com.fasterxml.jackson.dataformat:jackson-dataformat-xml")
    
    // Kotlin
    api("org.jetbrains.kotlin:kotlin-reflect")
    
    // Coroutines
    api("org.jetbrains.kotlinx:kotlinx-coroutines-core")
    api("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    
    // Swagger
    api("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")
    
    // Slack SDK
    api("com.slack.api:slack-api-client:1.42.0")
    
    // AWS SDK for NCP Object Storage (S3 compatible)
    api("software.amazon.awssdk:s3:2.20.26")
    api("software.amazon.awssdk:auth:2.20.26")
    
    // ULID
    api("com.github.f4b6a3:ulid-creator:5.2.3")
}

tasks.named<BootJar>("bootJar") {
    enabled = false
}

tasks.getByName("jar") {
    enabled = true
}