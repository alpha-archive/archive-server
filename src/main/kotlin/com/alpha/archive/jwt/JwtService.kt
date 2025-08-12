package com.alpha.archive.jwt

import com.alpha.archive.exception.ApiException
import com.alpha.archive.exception.ErrorTitle
import io.jsonwebtoken.Claims
import io.jsonwebtoken.ExpiredJwtException
import io.jsonwebtoken.Jwts
import io.jsonwebtoken.SignatureAlgorithm
import io.jsonwebtoken.io.Decoders
import io.jsonwebtoken.security.Keys
import org.springframework.beans.factory.annotation.Value
import org.springframework.stereotype.Service
import java.security.Key
import java.util.*

@Service
class JwtService(
    @Value("\${jwt.common.key}")
    private val jwtCommonKey: String
) {
    val key: Key by lazy {
        Keys.hmacShaKeyFor(Decoders.BASE64.decode(jwtCommonKey))
    }

    fun createJwt(subject: String, customClaims: Claims? = null, audience: String? = null): String {
        return Jwts.builder()
            .setSubject(subject)
            .setIssuer("archive")
            .setAudience(audience)
            .signWith(key, SignatureAlgorithm.HS512)
            .setExpiration(Date(System.currentTimeMillis() + 1000 * 60 * 60 * 3)) // 3 hours
            .addClaims(customClaims ?: mapOf())
            .compact()
    }

    fun getClaimsFromJwt(token: String): Claims = try {
        Jwts
            .parserBuilder()
            .setSigningKey(key)
            .build()
            .parseClaimsJws(token)
            .body
    } catch (e: ExpiredJwtException) {
        throw ApiException(ErrorTitle.ExpiredToken)
    } catch (e: Exception) {
        throw ApiException(ErrorTitle.InvalidToken)
    }

    fun createRefreshToken(): String {
        return UUID.randomUUID().toString()
    }
}
