package com.alpha.archive.activity.dto.request

import com.alpha.archive.domain.event.enums.EventCategory
import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.*
import java.time.LocalDateTime

@Schema(description = "활동 수정 요청")
data class UpdateActivityRequest(
    @Schema(description = "활동 제목", example = "해리포터 뮤지컬 관람 (재관람)", nullable = true)
    @Size(max = 300, message = "활동 제목은 300자 이내로 입력해주세요.")
    val title: String?,

    @Schema(description = "활동 카테고리", example = "MUSICAL", nullable = true)
    val category: EventCategory?,

    @Schema(description = "활동 장소", example = "충무아트센터 대극장", nullable = true)
    @Size(max = 200, message = "활동 장소는 200자 이내로 입력해주세요.")
    val location: String?,

    @Schema(description = "활동 날짜", example = "2024-01-15T19:30:00", nullable = true)
    val activityDate: LocalDateTime?,

    @Schema(description = "활동 평점 (1-5점)", example = "5", nullable = true)
    @Min(value = 1, message = "평점은 1점 이상이어야 합니다.")
    @Max(value = 5, message = "평점은 5점 이하여야 합니다.")
    val rating: Int?,

    @Schema(description = "활동 메모", example = "두 번째 관람이었는데도 새로운 감동이...", nullable = true)
    val memo: String?,

    @Schema(
        description = "새로 추가할 이미지 ID 목록 (선택사항)", 
        example = "[\"01HZ8X9GYR7J8Q3N5V2BPMKWD3\", \"01HZ8X9GYR7J8Q3N5V2BPMKWD4\"]", 
        nullable = true
    )
    val addImageIds: List<String>? = null,

    @Schema(
        description = "삭제할 이미지 ID 목록 (선택사항)", 
        example = "[\"01HZ8X9GYR7J8Q3N5V2BPMKWD1\"]", 
        nullable = true
    )
    val removeImageIds: List<String>? = null
)
