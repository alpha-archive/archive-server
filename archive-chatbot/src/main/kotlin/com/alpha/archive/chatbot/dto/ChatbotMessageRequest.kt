package com.alpha.archive.chatbot.dto

import io.swagger.v3.oas.annotations.media.Schema
import jakarta.validation.constraints.NotBlank

@Schema(description = "챗봇 메시지 요청 DTO")
data class ChatbotMessageRequest(
    @Schema(description = "사용자가 챗봇에게 보내는 메시지", example = "오늘 친구랑 영화 봤어!")
    @field:NotBlank(message = "메시지는 비어 있을 수 없습니다.")
    val message: String
)
