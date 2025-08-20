package com.alpha.archive.auth.service

import com.alpha.archive.auth.dto.request.KakaoLoginRequest
import com.alpha.archive.auth.dto.request.RefreshTokenRequest
import com.alpha.archive.auth.dto.response.TokenResponse
import com.alpha.archive.auth.external.kakao.KakaoOAuthClient
import com.alpha.archive.auth.external.redis.RedisService
import com.alpha.archive.auth.jwt.JwtService
import com.alpha.archive.domain.user.User
import com.alpha.archive.domain.user.UserRepository
import com.alpha.archive.exception.ApiException
import com.alpha.archive.exception.ErrorTitle
import io.jsonwebtoken.Jwts
import org.slf4j.LoggerFactory
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.util.concurrent.TimeUnit

interface AuthService {
    fun kakaoLogin(request: KakaoLoginRequest): TokenResponse
    fun refresh(request: RefreshTokenRequest): TokenResponse
}

@Service
class AuthServiceImpl(
    private val userRepository: UserRepository,
    private val jwtService: JwtService,
    private val redisService: RedisService,
    private val kakaoOAuthClient: KakaoOAuthClient
) : AuthService {

    @Transactional
    override fun kakaoLogin(request: KakaoLoginRequest): TokenResponse {
        // 1. 안드로이드 SDK에서 제공받은 액세스 토큰으로 사용자 정보 직접 조회
        val kakaoUserInfo = kakaoOAuthClient.getUserInfo(request.accessToken)
        
        // 2. 사용자 정보 추출
        val kakaoId = kakaoUserInfo.id
        
        // 닉네임 추출 (동의 여부 확인)
        val nickname = when {
            kakaoUserInfo.kakaoAccount?.profileNicknameNeedsAgreement == true -> {
                "카카오사용자_$kakaoId"
            }
            else -> kakaoUserInfo.kakaoAccount?.profile?.nickname 
                ?: kakaoUserInfo.properties?.nickname 
                ?: "카카오사용자_$kakaoId"
        }
        
        // 프로필 이미지 추출 (동의 여부 확인)
        val profileImageUrl = when {
            kakaoUserInfo.kakaoAccount?.profileImageNeedsAgreement == true -> {
                null
            }
            else -> kakaoUserInfo.kakaoAccount?.profile?.profileImageUrl
                ?: kakaoUserInfo.properties?.profileImage
        }
        
        val email = kakaoUserInfo.kakaoAccount?.email
        
        // 3. 사용자 생성 또는 업데이트
        val user = createOrUpdateKakaoUser(kakaoId, nickname, email, profileImageUrl)
        
        // 4. JWT 토큰 발급
        return handleTokenCreation(user)
    }

    override fun refresh(request: RefreshTokenRequest): TokenResponse {
        // Redis에서 refresh token으로 userId 조회
        val userId = redisService.getUserIdByRefreshToken(request.refreshToken) 
            ?: throw ApiException(ErrorTitle.InvalidToken, "유효하지 않은 리프레시 토큰입니다.")

        val user = userRepository.findByIdOrNull(userId) ?: throw ApiException(ErrorTitle.NotFoundUser)

        // 기존 refresh token 무효화 (Token Rotation)
        redisService.deleteRefreshToken(request.refreshToken)

        // 새로운 토큰 세트 발급
        return handleTokenCreation(user)
    }

    private fun handleTokenCreation(user: User): TokenResponse {
        val claims = createClaims(user.name)

        val refreshToken = createAndSaveRefreshToken(user.id)
        return TokenResponse(
            accessToken = jwtService.createJwt(user.id, claims),
            refreshToken = refreshToken
        )
    }

    private fun createClaims(name: String) = Jwts.claims().apply {
        set("name", name)
    }

    private fun createAndSaveRefreshToken(userId: String): String {
        val refreshToken = jwtService.createRefreshToken()

        // UUID -> userId 매핑과 userId -> UUID 매핑 둘 다 저장
        redisService.saveRefreshToken(userId, refreshToken, 14L, TimeUnit.DAYS)
        return refreshToken
    }

    private fun createOrUpdateKakaoUser(kakaoId: Long, name: String, email: String?, profileImageUrl: String?): User {
        val existingUser = userRepository.findByKakaoId(kakaoId)

        return if (existingUser != null) {
            // 기존 사용자 업데이트
            existingUser.updateProfile(name, email, profileImageUrl)
            userRepository.save(existingUser)
        } else {
            // 신규 사용자 생성
            val newUser = User(
                kakaoId = kakaoId,
                name = name,
                email = email,
                profileImageUrl = profileImageUrl
            )
            userRepository.save(newUser)
        }
    }
}
