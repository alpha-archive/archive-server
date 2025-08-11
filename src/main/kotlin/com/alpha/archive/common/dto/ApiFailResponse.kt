package com.alpha.archive.common.dto

import com.alpha.archive.exception.ApiException
import com.alpha.archive.exception.ErrorTitle
import io.swagger.v3.oas.annotations.media.Schema

/**
 * @deprecated ApiResponse.Failure를 사용하세요.
 * @see ApiResponse.Failure
 */
@Deprecated(
    message = "ApiResponse.Failure를 사용하세요",
    replaceWith = ReplaceWith("ApiResponse.Failure", "com.alpha.archive.common.dto.ApiResponse")
)
@Schema(description = "에러 응답 DTO")
class ApiFailResponse(
        private val apiException : ApiException
) {
    @Schema(description = "성공 여부", example = "false", requiredMode = Schema.RequiredMode.REQUIRED)
    val success: Boolean = false
    @Schema(description = "응답 메세지", example = "string", requiredMode = Schema.RequiredMode.REQUIRED)
    val message: String? = apiException.message
    @Schema(description = "에러 타이틀", example = "NotFoundAccount", requiredMode = Schema.RequiredMode.REQUIRED)
    val errorTitle: ErrorTitle = apiException.errorTitle
    constructor(errorTitle: ErrorTitle, message: String?) : this(
        ApiException(
            errorTitle,
            message
        )
    )
}