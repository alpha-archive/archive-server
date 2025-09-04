import org.jetbrains.kotlin.allopen.gradle.AllOpenExtension
import org.jetbrains.kotlin.gradle.dsl.KotlinJvmProjectExtension
import org.jetbrains.kotlin.noarg.gradle.NoArgExtension

plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25" apply false
	id("org.springframework.boot") version "3.3.8" apply false
	id("io.spring.dependency-management") version "1.1.7"
	kotlin("plugin.jpa") version "1.9.25" apply false
}

extra["springCloudVersion"] = "2023.0.6"

// 모든 프로젝트에 적용되는 설정
allprojects {
	group = "com.alpha"
	version = "0.0.1-SNAPSHOT"

	repositories {
		mavenCentral()
	}
}

// 서브프로젝트에만 적용되는 설정
subprojects {
	apply(plugin = "org.jetbrains.kotlin.jvm")
	apply(plugin = "org.jetbrains.kotlin.plugin.spring")
	apply(plugin = "org.springframework.boot")
	apply(plugin = "io.spring.dependency-management")

	configure<JavaPluginExtension> {
		toolchain {
			languageVersion = JavaLanguageVersion.of(17)
		}
	}

	dependencyManagement {
		imports {
			mavenBom("org.springframework.cloud:spring-cloud-dependencies:${property("springCloudVersion")}")
		}
	}

	dependencies {
		implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
		implementation("org.jetbrains.kotlin:kotlin-reflect")

		// 테스트 공통 의존성
		testImplementation("org.springframework.boot:spring-boot-starter-test")
		testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
		testRuntimeOnly("org.junit.platform:junit-platform-launcher")
	}

	configure<KotlinJvmProjectExtension> {
		compilerOptions {
			freeCompilerArgs.addAll("-Xjsr305=strict")
		}
	}

	// JPA 관련 설정은 domain 모듈에서만 적용
	if (name == "archive-domain") {
		apply(plugin = "org.jetbrains.kotlin.plugin.jpa")

		configure<AllOpenExtension> {
			annotation("jakarta.persistence.Entity")
			annotation("jakarta.persistence.MappedSuperclass")
			annotation("jakarta.persistence.Embeddable")
		}

		configure<NoArgExtension> {
			annotation("jakarta.persistence.Entity")
			annotation("jakarta.persistence.MappedSuperclass")
			annotation("jakarta.persistence.Embeddable")
		}
	}

	tasks.withType<Test> {
		useJUnitPlatform()
	}
}