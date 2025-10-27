package com.alpha.archive.chatbot.dto

import io.swagger.v3.oas.annotations.media.Schema

@Schema(description = "챗봇 메시지 응답 DTO")
data class ChatbotMessageResponse(
    @Schema(description = "챗봇의 응답 메시지", example = "오, 좋은 시간 보내셨네요! 어떤 영화 보셨어요?")
    val reply: String
)
