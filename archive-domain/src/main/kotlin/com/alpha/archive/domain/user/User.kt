package com.alpha.archive.domain.user

import com.alpha.archive.domain.base.UlidPrimaryKeyEntity
import jakarta.persistence.Column
import jakarta.persistence.Entity
import jakarta.persistence.Table
import jakarta.persistence.UniqueConstraint

@Entity
@Table(
    name = "users",
    uniqueConstraints = [
        UniqueConstraint(name = "uk_user_kakao_id", columnNames = ["kakao_id"])
    ]
)
class User(
    kakaoId: Long,
    name: String,
    email: String? = null,
    profileImageUrl: String? = null,
) : UlidPrimaryKeyEntity() {

    @Column(name = "kakao_id", nullable = false, unique = true)
    var kakaoId: Long = kakaoId
        protected set

    @Column(name = "name", nullable = false)
    var name: String = name
        protected set

    @Column(name = "email", nullable = true)
    var email: String? = email
        protected set

    @Column(name = "profile_image_url", nullable = true)
    var profileImageUrl: String? = profileImageUrl
        protected set

    fun updateProfile(name: String, email: String?, profileImageUrl: String?) {
        this.name = name
        this.email = email
        this.profileImageUrl = profileImageUrl
    }
}

