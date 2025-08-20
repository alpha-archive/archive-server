package com.alpha.archive.auth.external.kakao

import com.alpha.archive.auth.external.kakao.dto.KakaoUserInfoResponse
import com.alpha.archive.exception.ApiException
import com.alpha.archive.exception.ErrorTitle
import feign.FeignException
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Service

@Service
class KakaoOAuthClient(
    private val kakaoApiFeignClient: KakaoApiFeignClient
) {
    private val logger = LoggerFactory.getLogger(KakaoOAuthClient::class.java)

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

}
