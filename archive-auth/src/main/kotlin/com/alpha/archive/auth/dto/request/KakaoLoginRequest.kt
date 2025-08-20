package com.alpha.archive.auth.dto.request

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

@Schema(description = "카카오 로그인 요청")
data class KakaoLoginRequest(
    @Schema(description = "카카오 액세스 토큰", example = "sc-21321asdsadsa", requiredMode = Schema.RequiredMode.REQUIRED, nullable = false)
    @field:NotBlank(message = "액세스 토큰은 필수입니다")
    val accessToken: String
)
