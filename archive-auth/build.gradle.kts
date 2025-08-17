import org.gradle.kotlin.dsl.named
import org.springframework.boot.gradle.tasks.bundling.BootJar

dependencies {
    // Module dependencies (provides Spring BOM through common)
    api(project(":archive-common"))
    api(project(":archive-domain"))
    
    // Spring Security
    api("org.springframework.boot:spring-boot-starter-security")
    
    // JWT
    api("io.jsonwebtoken:jjwt-api:0.11.5")
    implementation("io.jsonwebtoken:jjwt-impl:0.11.5")
    implementation("io.jsonwebtoken:jjwt-jackson:0.11.5")
    
    // Redis
    api("org.springframework.boot:spring-boot-starter-data-redis")
    
    // Configuration
    implementation("org.springframework.boot:spring-boot-configuration-processor")
}

tasks.named<BootJar>("bootJar") {
    enabled = false
}

tasks.getByName("jar") {
    enabled = true
}