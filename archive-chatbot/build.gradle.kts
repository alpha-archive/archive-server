plugins {
    kotlin("jvm")
}

group = "com.alpha"
version = "0.0.1-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    api(project(":archive-domain"))
    api("org.springframework.boot:spring-boot-starter-webflux")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-reactor")
    testImplementation(kotlin("test"))
    implementation("org.springframework.boot:spring-boot-starter-data-redis")
}

tasks.test {
    useJUnitPlatform()
}
kotlin {
    jvmToolchain(17)
}