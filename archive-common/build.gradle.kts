import org.springframework.boot.gradle.tasks.bundling.BootJar

dependencies {
    // Spring (BOM은 루트에서 관리)
    api("org.springframework.boot:spring-boot-starter-web")
    api("org.springframework.boot:spring-boot-starter-validation")
    api("org.springframework.cloud:spring-cloud-starter-openfeign")
    
    // Jackson
    api("com.fasterxml.jackson.module:jackson-module-kotlin")
    
    // Kotlin
    api("org.jetbrains.kotlin:kotlin-reflect")
    
    // Swagger
    api("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.3.0")
    
    // Slack SDK
    api("com.slack.api:slack-api-client:1.42.0")
}

tasks.named<BootJar>("bootJar") {
    enabled = false
}

tasks.getByName("jar") {
    enabled = true
}