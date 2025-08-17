package com.alpha.archive.auth.external.kakao

import com.alpha.archive.auth.config.KakaoOAuthConfig
import com.alpha.archive.auth.external.kakao.dto.KakaoTokenResponse
import com.alpha.archive.auth.external.kakao.dto.KakaoUserInfoResponse
import com.alpha.archive.exception.ApiException
import com.alpha.archive.exception.ErrorTitle
import feign.FeignException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class KakaoOAuthClient(
    private val kakaoOAuthConfig: KakaoOAuthConfig,
    private val kakaoAuthFeignClient: KakaoAuthFeignClient,
    private val kakaoApiFeignClient: KakaoApiFeignClient
) {
    private val logger = LoggerFactory.getLogger(KakaoOAuthClient::class.java)

    fun getAccessToken(authorizationCode: String): KakaoTokenResponse {
        logger.info("카카오 액세스 토큰 요청 시작")
        return try {
            val result = kakaoAuthFeignClient.getAccessToken(
                grantType = "authorization_code",
                clientId = kakaoOAuthConfig.clientId,
                clientSecret = kakaoOAuthConfig.clientSecret,
                redirectUri = kakaoOAuthConfig.redirectUri,
                code = authorizationCode
            )
            logger.info("카카오 액세스 토큰 요청 성공")
            result
        } catch (e: FeignException) {
            logger.error("카카오 액세스 토큰 요청 실패 - Status: ${e.status()}, Message: ${e.message}")
            throw ApiException(ErrorTitle.ExternalServerError, "카카오 액세스 토큰 요청 실패: ${e.message}")
        } catch (e: Exception) {
            logger.error("카카오 액세스 토큰 요청 중 예외 발생", e)
            throw ApiException(ErrorTitle.ExternalServerError, "카카오 액세스 토큰 요청 실패: ${e.message}")
        }
    }

    fun getUserInfo(accessToken: String): KakaoUserInfoResponse {
        logger.info("카카오 사용자 정보 요청 시작")
        return try {
            val result = kakaoApiFeignClient.getUserInfo("Bearer $accessToken")
            logger.info("카카오 사용자 정보 요청 성공 - 사용자 ID: ${result.id}")
            result
        } catch (e: FeignException) {
            logger.error("카카오 사용자 정보 요청 실패 - Status: ${e.status()}, Message: ${e.message}")
            throw ApiException(ErrorTitle.ExternalServerError, "카카오 사용자 정보 요청 실패: ${e.message}")
        } catch (e: Exception) {
            logger.error("카카오 사용자 정보 요청 중 예외 발생", e)
            throw ApiException(ErrorTitle.ExternalServerError, "카카오 사용자 정보 요청 실패: ${e.message}")
        }
    }

    fun getAuthorizationUrl(): String {
        return "${kakaoOAuthConfig.authorizationUri}?" +
                "client_id=${kakaoOAuthConfig.clientId}&" +
                "redirect_uri=${kakaoOAuthConfig.redirectUri}&" +
                "response_type=code"
    }
}
