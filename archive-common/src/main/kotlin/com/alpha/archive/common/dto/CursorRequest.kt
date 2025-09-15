package com.alpha.archive.common.dto

import io.swagger.v3.oas.annotations.media.Schema

data class CursorRequest(
    @Schema(requiredMode = Schema.RequiredMode.NOT_REQUIRED, description = "커서 (null로 보낸다면 첫페이지)")
    val cursor: String?,
    @Schema(requiredMode = Schema.RequiredMode.REQUIRED, description = "페이지 사이즈", defaultValue = "10")
    val size: Int = 10
)