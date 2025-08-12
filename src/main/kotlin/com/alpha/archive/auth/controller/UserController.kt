package com.alpha.archive.auth.controller

import com.alpha.archive.auth.dto.response.UserInfoResponse
import com.alpha.archive.common.annotations.ArchiveGetMapping
import com.alpha.archive.common.annotations.ArchivePostMapping
import com.alpha.archive.common.dto.ApiResponse
import com.alpha.archive.domain.user.UserService
import com.alpha.archive.external.redis.RedisService
import com.alpha.archive.security.service.ArchiveUserDetails
import com.alpha.archive.util.ResponseUtil
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.responses.ApiResponse as SwaggerApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/api/users")
@Tag(name = "사용자 관련 API", description = "사용자 관련 API 입니다.")
@SecurityRequirement(name = "bearerAuth")
class UserController(
    private val userService: UserService,
    private val redisService: RedisService
) {

    @ArchiveGetMapping("/me", authenticated = true)
    @SwaggerApiResponse(responseCode = "200", description = "사용자 정보 조회 성공")
    @Operation(summary = "현재 로그인한 사용자 정보를 조회하는 api 입니다.")
    fun getCurrentUser(
        @AuthenticationPrincipal userDetails: ArchiveUserDetails
    ): ResponseEntity<ApiResponse.Success<UserInfoResponse>> {
        val userInfo = userService.getUserInfo(userDetails.getUserId())
        return ResponseUtil.success("사용자 정보 조회 성공", userInfo)
    }

    @ArchivePostMapping("/logout", authenticated = true)
    @SwaggerApiResponse(responseCode = "200", description = "로그아웃 성공")
    @Operation(summary = "현재 로그인한 사용자를 로그아웃하는 api 입니다.")
    fun logout(
        @AuthenticationPrincipal userDetails: ArchiveUserDetails
    ): ResponseEntity<ApiResponse.Success<String>> {
        redisService.deleteRefreshTokenByUserId(userDetails.getUserId())
        return ResponseUtil.success("로그아웃 성공", "로그아웃이 완료되었습니다.")
    }
}
