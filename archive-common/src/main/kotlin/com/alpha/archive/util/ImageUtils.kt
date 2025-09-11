package com.alpha.archive.util

import com.github.f4b6a3.ulid.UlidCreator
import org.springframework.web.multipart.MultipartFile
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

object ImageUtils {

    private val ALLOWED_IMAGE_TYPES = setOf(
        "image/jpeg", "image/jpg", "image/png", 
        "image/gif", "image/webp"
    )
    
    private const val MAX_FILE_SIZE = 10 * 1024 * 1024L // 10MB
    private val DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy/MM/dd")

    /**
     * 이미지 파일 유효성 검증
     */
    fun validateImageFile(file: MultipartFile) {
        require(!file.isEmpty) { "파일이 비어있습니다." }
        require(file.size <= MAX_FILE_SIZE) { 
            "파일 크기가 너무 큽니다. 최대 10MB까지 업로드 가능합니다." 
        }
        
        val contentType = file.contentType?.lowercase()
        require(contentType != null && contentType in ALLOWED_IMAGE_TYPES) { 
            "지원하지 않는 파일 형식입니다. (jpeg, jpg, png, gif, webp만 지원)" 
        }
    }

    /**
     * 이미지 저장 키 생성 (날짜 기반 경로 + ULID)
     */
    fun generateImageKey(originalFilename: String?): String {
        val now = LocalDateTime.now()
        val datePath = now.format(DATE_FORMATTER)
        val ulid = UlidCreator.getUlid().toString()
        val extension = originalFilename?.let { extractFileExtension(it) } ?: ""
        
        return "images/$datePath/$ulid$extension"
    }

    /**
     * 파일 확장자 추출
     */
    fun extractFileExtension(filename: String): String {
        val lastDotIndex = filename.lastIndexOf('.')
        return when {
            lastDotIndex > 0 && lastDotIndex < filename.length - 1 -> 
                filename.substring(lastDotIndex)
            else -> ""
        }
    }

    /**
     * 안전한 파일명 반환
     */
    fun getSafeFilename(filename: String?): String = filename ?: "unknown"

    /**
     * 안전한 Content-Type 반환
     */
    fun getSafeContentType(contentType: String?): String = 
        contentType ?: "application/octet-stream"
}
