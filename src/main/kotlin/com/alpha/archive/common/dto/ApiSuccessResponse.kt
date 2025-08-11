package com.alpha.archive.common.dto

import com.fasterxml.jackson.annotation.JsonInclude
import io.swagger.v3.oas.annotations.media.Schema

/**
 * @deprecated ApiResponse.Success를 사용하세요.
 * @see ApiResponse.Success
 */
@Deprecated(
    message = "ApiResponse.Success를 사용하세요",
    replaceWith = ReplaceWith("ApiResponse.Success<T>", "com.alpha.archive.common.dto.ApiResponse")
)
@Schema(description = "응답 DTO")
data class ApiSuccessResponse<T>(
        @Schema(description = "성공 여부", example = "true", requiredMode = Schema.RequiredMode.REQUIRED)
        val success: Boolean = true,

        @Schema(description = "응답 메세지", example = "string", requiredMode = Schema.RequiredMode.REQUIRED)
        val message: String,

        @Schema(description = "응답 바디", requiredMode = Schema.RequiredMode.NOT_REQUIRED)
        @JsonInclude(JsonInclude.Include.ALWAYS)
        val data: T?
) {
   constructor(message: String, data: T?) : this(true, message, data)
   constructor(message: String) : this(true, message, null)
}