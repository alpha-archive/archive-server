package com.alpha.archive.chatbot.dto

import com.fasterxml.jackson.annotation.JsonProperty

data class OpenAPIRequest (
    val model: String,
    val messages: List<Message>,
    // JSON 모드를 활성화하여 AI가 더 안정적으로 JSON을 반환하도록 합니다.
    val response_format: Map<String, String>? = null,

    @JsonProperty(value = "max_tokens")
    val maxTokens: Int = 300,

    val temperature: Double = 0.7,

    @JsonProperty("top_p")
    val topP: Double = 0.5
)