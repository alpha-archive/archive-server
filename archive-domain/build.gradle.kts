import org.gradle.kotlin.dsl.named
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    kotlin("kapt")
}

dependencies {
    // Common module (provides Spring BOM)
    api(project(":archive-common"))
    
    // Spring Data JPA
    api("org.springframework.boot:spring-boot-starter-data-jpa")
    
    // Querydsl
    api("com.querydsl:querydsl-jpa:5.0.0:jakarta")
    kapt("com.querydsl:querydsl-apt:5.0.0:jakarta")
    
    // ULID
    api("com.github.f4b6a3:ulid-creator:5.2.3")
    
    // Database
    runtimeOnly("org.postgresql:postgresql")
}

tasks.named<BootJar>("bootJar") {
    enabled = false
}

tasks.getByName("jar") {
    enabled = true
}