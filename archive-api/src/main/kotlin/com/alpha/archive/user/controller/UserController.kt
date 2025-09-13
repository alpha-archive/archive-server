package com.alpha.archive.user.controller

import com.alpha.archive.auth.security.service.ArchiveUserDetails
import com.alpha.archive.user.dto.response.UserInfoResponse
import com.alpha.archive.common.annotations.ArchiveGetMapping
import com.alpha.archive.common.dto.ApiResponse
import com.alpha.archive.exception.ApiException
import com.alpha.archive.exception.ErrorTitle
import com.alpha.archive.user.service.UserService
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
    private val userService: UserService
) {

    @ArchiveGetMapping("/me", authenticated = true)
    @SwaggerApiResponse(responseCode = "200", description = "사용자 정보 조회 성공")
    @Operation(summary = "현재 로그인한 사용자 정보를 조회하는 api 입니다.")
    fun getCurrentUser(
        @AuthenticationPrincipal userDetails: ArchiveUserDetails
    ): ResponseEntity<ApiResponse.Success<UserInfoResponse>> {
        val rest = userService.getUserById(userDetails.getUserId())
        return ResponseUtil.success("사용자 정보 조회 성공", rest)
    }
}
