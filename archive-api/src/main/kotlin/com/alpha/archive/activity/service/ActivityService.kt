package com.alpha.archive.activity.service

import com.alpha.archive.activity.dto.request.UserActivityRequest
import com.alpha.archive.domain.event.PublicEvent
import com.alpha.archive.domain.event.UserEvent
import com.alpha.archive.domain.event.embeddable.ActivityInfo
import com.alpha.archive.domain.event.enums.UserEventImageStatus
import com.alpha.archive.domain.event.repository.PublicEventRepository
import com.alpha.archive.domain.event.repository.UserEventImageRepository
import com.alpha.archive.domain.event.repository.UserEventRepository
import com.alpha.archive.exception.ApiException
import com.alpha.archive.exception.ErrorTitle
import com.alpha.archive.user.service.UserService
import jakarta.transaction.Transactional
import org.springframework.stereotype.Service

interface ActivityService {
    fun createUserActivity(userId: String, request: UserActivityRequest)
}

@Service
class ActivityServiceImpl(
    private val userService: UserService,
    private val userEventRepository: UserEventRepository,
    private val userEventImageRepository: UserEventImageRepository,
    private val publicEventRepository: PublicEventRepository
) : ActivityService {

    /**
     * 사용자 활동 기록 생성
     * 1. 개인 활동 필드 검증 (publicEventId가 없는 경우)
     * 2. PublicEvent 조회 (publicEventId가 있는 경우)
     * 3. ActivityInfo 생성 (공공/개인 데이터 활용)
     * 4. UserEvent 생성 및 저장
     * 5. 이미지 연결 (imageIds가 있는 경우)
     */
    @Transactional
    override fun createUserActivity(userId: String, request: UserActivityRequest) {
        // 1. 요청 검증
        request.validateForPersonalActivity()
        
        // 2. 사용자 조회
        val user = userService.getUserEntityById(userId)
        
        // 3. PublicEvent 조회 (있는 경우)
        val publicEvent = request.publicEventId?.let { publicEventId ->
            publicEventRepository.findByIdAndDeletedAtIsNull(publicEventId)
                ?: throw ApiException(ErrorTitle.NotFoundPublicEvent)
        }
        
        // 4. ActivityInfo 생성 (공공/개인 데이터 결합)
        val activityInfo = ActivityInfo(
            customTitle = request.title,
            customCategory = request.category,
            customLocation = request.location,
            rating = request.rating,
            memo = request.memo
        )
        
        // 5. UserEvent 생성 및 저장
        val userEvent = UserEvent(
            user = user,
            publicEvent = publicEvent,
            activityDate = request.activityDate,
            activityInfo = activityInfo
        )
        val savedUserEvent = userEventRepository.save(userEvent)
        
        // 6. 이미지 연결 (imageIds가 있는 경우)
        request.imageIds?.takeIf { it.isNotEmpty() }?.let { imageIds ->
            linkImagesToUserEvent(userId, imageIds, savedUserEvent)
        }
    }

    
    /**
     * TEMP 상태의 이미지들을 UserEvent와 연결
     */
    private fun linkImagesToUserEvent(
        userId: String,
        imageIds: List<String>,
        userEvent: UserEvent
    ) {
        // TEMP 상태의 이미지들 조회
        val tempImages = userEventImageRepository.findByIdInAndUserIdAndStatusAndDeletedAtIsNull(
            ids = imageIds,
            userId = userId,
            status = UserEventImageStatus.TEMP
        )
        
        // 요청된 imageIds와 실제 조회된 이미지 개수가 다르면 예외 발생
        if (tempImages.size != imageIds.size) {
            val foundIds = tempImages.map { it.getId() }.toSet()
            val notFoundIds = imageIds.filterNot { it in foundIds }
            throw ApiException(ErrorTitle.NotFoundUserEventImage, "존재하지 않거나 이미 연결된 이미지가 있습니다: $notFoundIds")
        }
        
        // 각 이미지를 UserEvent와 연결하고 상태 변경
        tempImages.forEach { image ->
            image.linkToUserEvent(userEvent)
        }
        
        // 변경사항 저장
        userEventImageRepository.saveAll(tempImages)
    }
}