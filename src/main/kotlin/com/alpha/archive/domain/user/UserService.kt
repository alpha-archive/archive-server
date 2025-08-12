package com.alpha.archive.domain.user

import com.alpha.archive.auth.dto.response.UserInfoResponse
import com.alpha.archive.exception.ApiException
import com.alpha.archive.exception.ErrorTitle
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

interface UserService {
    fun getUserInfo(userId: String): UserInfoResponse
    fun getUserById(userId: String): User
    fun getUserByKakaoId(kakaoId: Long): User?
    fun getUserByEmail(email: String): User?
    fun existByUserEmail(email: String): Boolean
    fun existByKakaoId(kakaoId: Long): Boolean
    fun save(user: User): User
    fun createOrUpdateKakaoUser(kakaoId: Long, name: String, email: String?, profileImageUrl: String?): User
}

@Service
class UserServiceImpl(
    private val userRepository: UserRepository
) : UserService {

    override fun getUserInfo(userId: String): UserInfoResponse {
        val findUser = userRepository.findByIdOrNull(userId) ?: throw ApiException(ErrorTitle.NotFoundUser)

        return UserInfoResponse(
            id = findUser.id,
            kakaoId = findUser.kakaoId,
            name = findUser.name,
            email = findUser.email,
            profileImageUrl = findUser.profileImageUrl
        )
    }

    override fun getUserById(userId: String): User {
        return userRepository.findByIdOrNull(userId) ?: throw ApiException(ErrorTitle.NotFoundUser)
    }

    override fun getUserByKakaoId(kakaoId: Long): User? {
        return userRepository.findByKakaoId(kakaoId)
    }

    override fun getUserByEmail(email: String): User? {
        return userRepository.findByEmail(email)
    }

    override fun existByUserEmail(email: String): Boolean {
        return userRepository.existsByEmail(email)
    }

    override fun existByKakaoId(kakaoId: Long): Boolean {
        return userRepository.existsByKakaoId(kakaoId)
    }

    override fun save(user: User): User {
        return userRepository.save(user)
    }

    override fun createOrUpdateKakaoUser(kakaoId: Long, name: String, email: String?, profileImageUrl: String?): User {
        val existingUser = getUserByKakaoId(kakaoId)
        
        return if (existingUser != null) {
            // 기존 사용자 업데이트
            existingUser.updateProfile(name, email, profileImageUrl)
            save(existingUser)
        } else {
            // 신규 사용자 생성
            val newUser = User(
                kakaoId = kakaoId,
                name = name,
                email = email,
                profileImageUrl = profileImageUrl
            )
            save(newUser)
        }
    }
}
