package com.alpha.archive.image.controller

import com.alpha.archive.auth.security.service.ArchiveUserDetails
import com.alpha.archive.common.annotations.ArchiveDeleteMapping
import com.alpha.archive.common.annotations.ArchiveGetMapping
import com.alpha.archive.common.annotations.ArchivePostMapping
import com.alpha.archive.common.dto.ApiResponse
import com.alpha.archive.image.dto.response.ImageUploadResponse
import com.alpha.archive.image.service.ImageService
import com.alpha.archive.util.ResponseUtil
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.responses.ApiResponse as SwaggerApiResponse
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.MediaType
import org.springframework.http.ResponseEntity
import org.springframework.security.core.annotation.AuthenticationPrincipal
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/api/images")
@Tag(name = "이미지 업로드 API", description = "이미지 업로드 및 관리 관련 API")
@SecurityRequirement(name = "bearerAuth")
class ImageController(
    private val imageService: ImageService
) {

    @ArchivePostMapping(
        value = ["/upload"],
        authenticated = true,
        consumes = [MediaType.MULTIPART_FORM_DATA_VALUE]
    )
    @SwaggerApiResponse(responseCode = "200", description = "이미지 업로드 성공")
    @SwaggerApiResponse(responseCode = "400", description = "잘못된 파일 형식 또는 크기")
    @Operation(
        summary = "이미지 파일 업로드",
        description = "이미지 파일을 NCP Object Storage에 업로드합니다. 지원 형식: JPEG, JPG, PNG, GIF, WEBP (최대 10MB)"
    )
    fun uploadImage(
        @AuthenticationPrincipal userDetails: ArchiveUserDetails,
        @Parameter(
            description = "업로드할 이미지 파일",
            required = true
        )
        @RequestParam("file") file: MultipartFile
    ): ResponseEntity<ApiResponse.Success<ImageUploadResponse>> {
        val uploadResponse = imageService.uploadImage(userDetails.getUserId(), file)
        return ResponseUtil.success("이미지 업로드 성공", uploadResponse)
    }

    @ArchiveDeleteMapping("/{imageId}", authenticated = true)
    @SwaggerApiResponse(responseCode = "200", description = "이미지 삭제 성공")
    @SwaggerApiResponse(responseCode = "404", description = "이미지를 찾을 수 없음")
    @Operation(
        summary = "이미지 파일 삭제",
        description = "업로드된 이미지 파일을 NCP Object Storage 및 DB에서 삭제합니다."
    )
    fun deleteImage(
        @Parameter(description = "삭제할 이미지 ID (ULID)", example = "01HQR8G3KXRT8QPC6Q8Q8Q8Q8Q")
        @PathVariable imageId: String
    ): ResponseEntity<ApiResponse.Success<String>> {
        imageService.deleteImage(imageId)
        return ResponseUtil.success("이미지 삭제 성공", "이미지가 성공적으로 삭제되었습니다.")
    }

    @ArchiveGetMapping("/temp", authenticated = true)
    @SwaggerApiResponse(responseCode = "200", description = "임시 이미지 목록 조회 성공")
    @Operation(
        summary = "사용자의 임시 이미지 목록 조회",
        description = "현재 사용자의 TEMP 상태인 이미지들을 조회합니다."
    )
    fun getTempImages(
        @AuthenticationPrincipal userDetails: ArchiveUserDetails
    ): ResponseEntity<ApiResponse.Success<List<ImageUploadResponse>>> {
        val tempImages = imageService.getTempImagesByUserId(userDetails.getUserId())
        return ResponseUtil.success("임시 이미지 목록 조회 성공", tempImages)
    }
}
