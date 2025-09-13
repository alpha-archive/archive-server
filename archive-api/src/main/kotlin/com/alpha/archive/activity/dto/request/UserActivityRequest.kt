package com.alpha.archive.activity.dto.request

import com.alpha.archive.domain.event.enums.EventCategory
import com.alpha.archive.exception.ApiException
import com.alpha.archive.exception.ErrorTitle
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.*
import java.time.LocalDateTime

@Schema(description = "사용자 활동 기록 요청")
data class UserActivityRequest(
    @field:Schema(
        description = "활동 제목 (공공 활동인 경우 선택사항, 개인 활동인 경우 필수)", 
        example = "해리포터 뮤지컬 관람", 
        nullable = true
    )
    @field:Size(max = 300, message = "활동 제목은 300자 이내로 입력해주세요.")
    val customTitle: String? = null,

    @field:Schema(description = "활동 카테고리 (공공 활동인 경우 선택사항, 개인 활동인 경우 필수)", example = "MUSICAL", nullable = true)
    val customCategory: EventCategory? = null,

    @field:Schema(description = "활동 장소 (공공 활동인 경우 선택사항)", example = "충무아트센터 대극장", nullable = true)
    @field:Size(max = 200, message = "활동 장소는 200자 이내로 입력해주세요.")
    val customLocation: String? = null,

    @field:Schema(description = "활동 날짜 (필수)", example = "2024-01-15T19:30:00", nullable = false)
    @field:NotNull(message = "활동 날짜는 필수입니다.")
    val activityDate: LocalDateTime,

    @field:Schema(description = "활동 평점 (1-5점)", example = "5", nullable = true)
    @field:Min(value = 1, message = "평점은 1점 이상이어야 합니다.")
    @field:Max(value = 5, message = "평점은 5점 이하여야 합니다.")
    val rating: Int? = null,

    @field:Schema(description = "활동 메모", example = "정말 감동적인 공연이었다. 배우들의 연기가 훌륭했고...", nullable = true)
    val memo: String? = null,

    @field:Schema(
        description = "연결할 이미지 ID 목록 (선택사항)", 
        example = "[\"01HZ8X9GYR7J8Q3N5V2BPMKWD1\", \"01HZ8X9GYR7J8Q3N5V2BPMKWD2\"]", 
        nullable = true
    )
    val imageIds: List<String>? = null,

    @field:Schema(description = "공공 문화 활동 ID (선택사항)", example = "01HZ8X9GYR7J8Q3N5V2BPMKWD1", nullable = true)
    val publicEventId: String? = null,
) {
    /**
     * 개인 활동인 경우 필수 필드 검증
     */
    fun validateForPersonalActivity() {
        if (publicEventId == null) {
            if (customTitle.isNullOrBlank()) {
                throw ApiException(ErrorTitle.BadRequest, "개인 활동의 경우 활동 제목은 필수입니다.")
            }
            if (customCategory == null) {
                throw ApiException(ErrorTitle.BadRequest, "개인 활동의 경우 활동 카테고리는 필수입니다.")
            }
        }
    }
}
