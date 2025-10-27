import org.gradle.kotlin.dsl.named
import org.springframework.boot.gradle.tasks.bundling.BootJar

plugins {
    id("org.springframework.boot")
    id("io.spring.dependency-management")
}

dependencies {
    // Module dependencies (provides Spring BOM through common)
    implementation(project(":archive-common"))
    implementation(project(":archive-domain"))
    implementation(project(":archive-auth"))
    implementation(project(":archive-chatbot"))
    
    // Spring Boot Actuator
    implementation("org.springframework.boot:spring-boot-starter-actuator")
    
    // Test dependencies
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("com.h2database:h2")
    testImplementation("org.mockito:mockito-core")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

}

springBoot {
    mainClass.set("com.alpha.archive.ArchiveApplicationKt")
}

tasks.named<BootJar>("bootJar") {
    enabled = true
}

tasks.getByName("jar") {
    enabled = false
}