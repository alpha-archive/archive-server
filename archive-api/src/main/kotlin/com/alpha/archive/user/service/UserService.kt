package com.alpha.archive.user.service

import com.alpha.archive.domain.user.User
import com.alpha.archive.domain.user.UserRepository
import com.alpha.archive.user.dto.response.UserInfoResponse
import com.alpha.archive.exception.ApiException
import com.alpha.archive.exception.ErrorTitle
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service

interface UserService {
    fun getUserById(userId: String): UserInfoResponse
    fun getUserEntityById(userId: String): User
}

@Service
class UserServiceImpl(
    private val userRepository: UserRepository
) : UserService {

    override fun getUserById(userId: String): UserInfoResponse {
        val user =  userRepository.findByIdOrNull(userId) ?: throw ApiException(ErrorTitle.NotFoundUser)
        return UserInfoResponse(
            id = user.id,
            kakaoId = user.kakaoId,
            name = user.name,
            email = user.email,
            profileImageUrl = user.profileImageUrl
        )
    }

    override fun getUserEntityById(userId: String): User = userRepository.findByIdOrNull(userId) ?: throw ApiException(ErrorTitle.NotFoundUser)
}
