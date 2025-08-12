package com.alpha.archive.security.config

import com.alpha.archive.common.annotations.ArchiveDeleteMapping
import com.alpha.archive.common.annotations.ArchiveGetMapping
import com.alpha.archive.common.annotations.ArchivePatchMapping
import com.alpha.archive.common.annotations.ArchivePostMapping
import com.alpha.archive.common.annotations.ArchivePutMapping
import com.alpha.archive.jwt.JwtService
import com.alpha.archive.security.filter.ExceptionFilter
import com.alpha.archive.security.filter.JwtAuthenticationFilter
import com.alpha.archive.security.handler.CustomAccessDeniedHandler
import com.alpha.archive.security.handler.JwtAuthenticationEntryPointHandler
import org.springframework.context.ApplicationContext
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.ComponentScan
import org.springframework.context.annotation.Configuration
import org.springframework.http.HttpMethod
import org.springframework.security.authentication.AuthenticationManager
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity
import org.springframework.security.config.annotation.web.builders.HttpSecurity
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity
import org.springframework.security.config.http.SessionCreationPolicy
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder
import org.springframework.security.crypto.password.PasswordEncoder
import org.springframework.security.web.SecurityFilterChain
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter
import org.springframework.stereotype.Controller
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.cors.CorsConfiguration
import org.springframework.web.cors.CorsConfigurationSource
import org.springframework.web.cors.UrlBasedCorsConfigurationSource
import java.lang.reflect.Method

@Configuration
@EnableWebSecurity
@EnableMethodSecurity
@ComponentScan(basePackages = ["com.alpha.archive"])
class SecurityConfig(
    private val applicationContext: ApplicationContext,
    private val jwtService: JwtService,
) {

    @Bean
    @Throws(Exception::class)
    fun authenticationManager(authenticationConfiguration: AuthenticationConfiguration): AuthenticationManager? {
        return authenticationConfiguration.authenticationManager
    }

    @Bean
    fun passwordEncoder(): PasswordEncoder = BCryptPasswordEncoder()

    @Bean
    @Throws(Exception::class)
    fun filterChain(http: HttpSecurity): SecurityFilterChain = http
        .csrf { it.disable() }
        .cors { it.configurationSource(corsConfigurationSource()) }
        .headers { it.frameOptions { fo -> fo.sameOrigin() } }
        .applyDynamicUrlSecurity(applicationContext)
        .authorizeHttpRequests {
            it
                .requestMatchers(HttpMethod.POST, "/api/auth/**").permitAll()
                .requestMatchers("/swagger-ui/**", "/v3/api-docs/**").permitAll()
                .anyRequest().permitAll()
        }
        .sessionManagement { it.sessionCreationPolicy(SessionCreationPolicy.STATELESS) }
        .addFilterBefore(JwtAuthenticationFilter(jwtService), BasicAuthenticationFilter::class.java)
        .addFilterBefore(ExceptionFilter(), JwtAuthenticationFilter::class.java)
        .exceptionHandling {
            it
                .authenticationEntryPoint(JwtAuthenticationEntryPointHandler())
                .accessDeniedHandler(CustomAccessDeniedHandler())
        }
        .build()

    @Bean
    @Throws(Exception::class)
    fun corsConfigurationSource(): CorsConfigurationSource {
        val config = CorsConfiguration()
        config.allowCredentials = true
        config.allowedOriginPatterns = listOf("*")
        config.allowedMethods = listOf("GET", "POST", "PUT", "DELETE", "PATCH", "OPTIONS")
        config.allowedHeaders = listOf("*")
        config.exposedHeaders = listOf("*")
        val source = UrlBasedCorsConfigurationSource()
        source.registerCorsConfiguration("/**", config)
        return source
    }

    fun HttpSecurity.applyDynamicUrlSecurity(applicationContext: ApplicationContext): HttpSecurity {
        val controllers = applicationContext.getBeansWithAnnotation(Controller::class.java)

        controllers.values.forEach { controller ->
            val parentPath = controller.javaClass.getAnnotation(RequestMapping::class.java)?.value?.firstOrNull()

            controller.javaClass.declaredMethods.forEach { method ->
                handleMapping<ArchiveGetMapping>(method, HttpMethod.GET, parentPath)
                handleMapping<ArchivePostMapping>(method, HttpMethod.POST, parentPath)
                handleMapping<ArchivePatchMapping>(method, HttpMethod.PATCH, parentPath)
                handleMapping<ArchivePutMapping>(method, HttpMethod.PUT, parentPath)
                handleMapping<ArchiveDeleteMapping>(method, HttpMethod.DELETE, parentPath)
            }
        }
        return this
    }

    @Suppress("UNCHECKED_CAST")
    private inline fun <reified T : Annotation> HttpSecurity.handleMapping(
        method: Method,
        httpMethod: HttpMethod,
        parentPath: String?
    ) {
        method.getAnnotation(T::class.java)?.let { mapping ->
            val paths = mapping.javaClass.getMethod("value").invoke(mapping) as Array<String>
            val hasRole = mapping.javaClass.getMethod("hasRole").invoke(mapping) as Array<String>
            val authenticated = mapping.javaClass.getMethod("authenticated").invoke(mapping) as Boolean

            when {
                hasRole.isNotEmpty() -> configureHasRole(httpMethod, parentPath, paths, hasRole)
                authenticated -> configureAuthenticated(httpMethod, parentPath, paths)
            }
        }
    }

    private fun HttpSecurity.configureHasRole(
        httpMethod: HttpMethod,
        parentPath: String?,
        paths: Array<String>,
        roles: Array<String>
    ) {
        authorizeHttpRequests {
            if (paths.isEmpty()) {
                it.requestMatchers(httpMethod, parentPath).hasAnyRole(*roles)
            }
            paths.forEach { p ->
                it.requestMatchers(httpMethod, formatPath(p, parentPath)).hasAnyRole(*roles)
            }
        }
    }

    private fun HttpSecurity.configureAuthenticated(
        httpMethod: HttpMethod,
        parentPath: String?,
        paths: Array<String>
    ) {
        authorizeHttpRequests {
            if (paths.isEmpty()) {
                it.requestMatchers(httpMethod, parentPath).authenticated()
            }
            paths.forEach { p ->
                it.requestMatchers(httpMethod, formatPath(p, parentPath)).authenticated()
            }
        }
    }

    private fun formatPath(path: String, parentPath: String?): String {
        val formattedPath = if (!path.startsWith("/")) "/$path" else path
        return if (parentPath == null || parentPath == "null") path else parentPath + formattedPath
    }
}
