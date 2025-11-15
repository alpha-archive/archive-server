package com.alpha.archive.image.service

import com.alpha.archive.domain.event.UserEventImage
import com.alpha.archive.domain.event.repository.UserEventImageRepository
import com.alpha.archive.domain.event.enums.UserEventImageStatus
import com.alpha.archive.exception.ApiException
import com.alpha.archive.exception.ErrorTitle
import com.alpha.archive.image.dto.response.ImageUploadResponse
import com.alpha.archive.storage.ObjectStorageService
import com.alpha.archive.user.service.UserService
import com.alpha.archive.util.ImageUtils
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service
import org.springframework.web.multipart.MultipartFile


interface ImageService {
    fun uploadImage(userId: String, file: MultipartFile): ImageUploadResponse
    fun deleteImage(imageId: String)
    fun getTempImagesByUserId(userId: String): List<ImageUploadResponse>
}
@Service
class ImageServiceImpl(
    private val userService: UserService,
    private val userEventImageRepository: UserEventImageRepository,
    private val objectStorageService: ObjectStorageService
) : ImageService {

    /**
     * 이미지 파일을 Object Storage에 업로드하고 DB에 저장
     */
    @Transactional
    override fun uploadImage(userId: String, file: MultipartFile): ImageUploadResponse {
        ImageUtils.validateImageFile(file)
        
        val user = userService.getUserEntityById(userId)
        val imageKey = ImageUtils.generateImageKey(file.originalFilename)
        val imageUrl = objectStorageService.uploadFile(file, imageKey)
        
        val userEventImage = UserEventImage(
            user = user,
            userEvent = null,
            url = imageUrl,
            fileName = file.originalFilename,
            contentType = file.contentType,
            status = UserEventImageStatus.TEMP
        ).also { userEventImageRepository.save(it) }
        
        return userEventImage.toImageUploadResponse(file.size)
    }

    /**
     * 이미지 ID로 이미지 삭제 (소프트 삭제)
     */
    @Transactional
    override fun deleteImage(imageId: String) {
        val userEventImage = userEventImageRepository.findByIdAndDeletedAtIsNull(imageId)
            ?: throw ApiException(ErrorTitle.NotFoundUser)
        
        runCatching {
            val imageKey = objectStorageService.extractObjectKey(userEventImage.url)
            objectStorageService.deleteFile(imageKey)
        }.onFailure { 
            println("Object Storage 파일 삭제 실패: ${it.message}")
        }
        
        userEventImageRepository.delete(userEventImage)
    }

    /**
     * 사용자의 TEMP 상태 이미지들 조회
     */
    override fun getTempImagesByUserId(userId: String): List<ImageUploadResponse> =
        userEventImageRepository.findAllByUserIdAndDeletedAtIsNull(userId)
            .map { it.toImageUploadResponse() }

    /**
     * UserEventImage를 ImageUploadResponse로 변환
     */
    private fun UserEventImage.toImageUploadResponse(fileSize: Long = 0L): ImageUploadResponse {
        return ImageUploadResponse(
            id = this.getId(),
            imageKey = objectStorageService.extractObjectKey(this.url),
            imageUrl = this.url,
            originalFilename = ImageUtils.getSafeFilename(this.fileName),
            fileSize = fileSize,
            contentType = ImageUtils.getSafeContentType(this.contentType),
            status = this.status.name
        )
    }
}
