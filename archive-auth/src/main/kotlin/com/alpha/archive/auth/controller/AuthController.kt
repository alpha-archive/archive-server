package com.alpha.archive.auth.controller

import com.alpha.archive.auth.dto.request.KakaoLoginRequest
import com.alpha.archive.auth.dto.request.RefreshTokenRequest
import com.alpha.archive.auth.dto.response.AuthUrlResponse
import com.alpha.archive.auth.dto.response.TokenResponse
import com.alpha.archive.auth.external.redis.RedisService
import com.alpha.archive.auth.security.service.ArchiveUserDetails
import com.alpha.archive.auth.service.AuthService
import com.alpha.archive.common.annotations.ArchiveGetMapping
import com.alpha.archive.common.annotations.ArchivePostMapping
import com.alpha.archive.common.dto.ApiResponse
import com.alpha.archive.exception.ErrorTitle
import com.alpha.archive.exception.annotation.CustomFailResponseAnnotation
import com.alpha.archive.util.ResponseUtil
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse as SwaggerApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.validation.Valid
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/auth")
@Tag(name = "인증 관련 API", description = "카카오 로그인 기반 인증 API입니다.")
class AuthController(
    private val authService: AuthService,
    private val redisService: RedisService
) {

    @ArchiveGetMapping("/kakao/url")
    @SwaggerApiResponse(responseCode = "200", description = "카카오 로그인 URL 조회 성공")
    @Operation(summary = "카카오 로그인 URL을 조회하는 api입니다.")
    fun getKakaoAuthUrl(): ResponseEntity<ApiResponse.Success<AuthUrlResponse>> {
        return ResponseUtil.success("카카오 로그인 URL 조회 성공", authService.getKakaoAuthUrl())
    }

    @ArchivePostMapping("/kakao/login")
    @SwaggerApiResponse(responseCode = "200", description = "카카오 로그인 성공")
    @Operation(summary = "카카오 인증 코드로 로그인하는 api입니다.")
    @CustomFailResponseAnnotation(ErrorTitle.InvalidInputValue)
    @CustomFailResponseAnnotation(ErrorTitle.ExternalServerError)
    fun kakaoLogin(
        @RequestBody @Valid request: KakaoLoginRequest
    ): ResponseEntity<ApiResponse.Success<TokenResponse>> {
        return ResponseUtil.success("카카오 로그인 성공", authService.kakaoLogin(request))
    }

    @ArchivePostMapping("/refresh")
    @SwaggerApiResponse(responseCode = "200", description = "토큰 재발급 성공")
    @Operation(summary = "토큰 재발급 하는 api 입니다.")
    @CustomFailResponseAnnotation(ErrorTitle.InvalidToken)
    @CustomFailResponseAnnotation(ErrorTitle.ExpiredToken)
    @CustomFailResponseAnnotation(ErrorTitle.NotFoundUser)
    fun refresh(
        @RequestBody @Valid request: RefreshTokenRequest
    ): ResponseEntity<ApiResponse.Success<TokenResponse>> {
        return ResponseUtil.success("토큰 재발급 성공", authService.refresh(request))
    }

    @ArchivePostMapping("/logout", authenticated = true)
    @SwaggerApiResponse(responseCode = "200", description = "로그아웃 성공")
    @Operation(summary = "현재 로그인한 사용자를 로그아웃하는 api 입니다.")
    @SecurityRequirement(name = "bearerAuth")
    fun logout(
        @AuthenticationPrincipal userDetails: ArchiveUserDetails
    ): ResponseEntity<ApiResponse.Success<String>> {
        redisService.deleteRefreshTokenByUserId(userDetails.getUserId())
        return ResponseUtil.success("로그아웃 성공", "로그아웃이 완료되었습니다.")
    }
}
