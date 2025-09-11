package com.alpha.archive.image.dto.response

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "이미지 업로드 응답")
data class ImageUploadResponse(
    @Schema(description = "DB에 저장된 이미지 ID (ULID)", example = "01HQR8G3KXRT8QPC6Q8Q8Q8Q8Q")
    val id: String,
    
    @Schema(description = "업로드된 이미지의 고유 키", example = "images/2024/09/11/ulid-filename.jpg")
    val imageKey: String,
    
    @Schema(description = "이미지에 접근할 수 있는 URL", example = "https://kr.object.ncloudstorage.com/bucket-name/images/2024/09/11/ulid-filename.jpg")
    val imageUrl: String,
    
    @Schema(description = "원본 파일명", example = "my-photo.jpg")
    val originalFilename: String,
    
    @Schema(description = "파일 크기 (bytes)", example = "1024000")
    val fileSize: Long,
    
    @Schema(description = "파일 MIME 타입", example = "image/jpeg")
    val contentType: String,
    
    @Schema(description = "이미지 상태", example = "TEMP")
    val status: String
)
