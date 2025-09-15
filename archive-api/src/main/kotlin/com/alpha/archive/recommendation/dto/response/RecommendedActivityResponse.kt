package com.alpha.archive.recommendation.dto.response

import com.alpha.archive.domain.event.enums.EventCategory
import io.swagger.v3.oas.annotations.media.Schema
import java.time.LocalDateTime

@Schema(description = "추천 활동 응답")
data class RecommendedActivityResponse(
    @Schema(description = "공공 이벤트 ID", example = "01HZ8X9GYR7J8Q3N5V2BPMKWD1")
    val id: String,
    
    @Schema(description = "활동 제목", example = "해리포터와 저주받은 아이")
    val title: String,
    
    @Schema(description = "활동 설명", example = "세계적인 베스트셀러 해리포터 시리즈의 연극 버전")
    val description: String?,
    
    @Schema(description = "활동 카테고리", example = "MUSICAL")
    val category: EventCategory,
    
    @Schema(description = "시작 날짜", example = "2024-01-15T19:30:00")
    val startAt: LocalDateTime?,
    
    @Schema(description = "종료 날짜", example = "2024-01-15T22:00:00")
    val endAt: LocalDateTime?,
    
    @Schema(description = "장소 이름", example = "충무아트센터 대극장")
    val placeName: String?,
    
    @Schema(description = "장소 주소", example = "서울특별시 중구 퇴계로 387")
    val placeAddress: String?,
    
    @Schema(description = "도시", example = "서울특별시")
    val placeCity: String?,
    
    @Schema(description = "구/군", example = "중구")
    val placeDistrict: String?,
    
    @Schema(description = "위도", example = "37.5595")
    val placeLatitude: Double?,
    
    @Schema(description = "경도", example = "126.9945")
    val placeLongitude: Double?,
    
    @Schema(description = "연락처", example = "02-2230-6600")
    val placePhone: String?,
    
    @Schema(description = "홈페이지", example = "https://www.caci.or.kr")
    val placeHomepage: String?,
    
    @Schema(description = "데이터 수집 시각", example = "2024-01-10T10:00:00")
    val ingestedAt: LocalDateTime
)
