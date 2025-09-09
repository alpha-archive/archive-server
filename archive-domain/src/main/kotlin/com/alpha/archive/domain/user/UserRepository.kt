package com.alpha.archive.domain.user

import org.springframework.data.jpa.repository.JpaRepository

interface UserRepository : JpaRepository<User, String> {
    fun existsByKakaoId(kakaoId: Long): Boolean
    fun findByKakaoId(kakaoId: Long): User?
    fun existsByEmail(email: String): Boolean
    fun findByEmail(email: String): User?

    fun findByIdAndDeletedAtIsNull()
}
